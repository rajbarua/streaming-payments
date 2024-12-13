package com.hz.demos;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.UserCodeNamespaceConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.core.metrics.Measurement;
import com.hazelcast.jet.core.metrics.MetricTags;
import com.hz.demo.pmt.pain001.Document;
import com.hz.demo.pmt.pain001.Pain001Generator;
import com.hz.demo.pmt.pain001.cs.GrpHdrCS;

public class ReadStatsClient {

    public static void main(String[] args) throws Exception {
        var readStatsClient = new ReadStatsClient();
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setClusterName("dev");
        clientConfig.getNetworkConfig().addAddress("localhost:5701");
        // Compact Serialiser
        clientConfig.getSerializationConfig()
                .getCompactSerializationConfig()
                .addSerializer(new GrpHdrCS());

        HazelcastInstance hzClient = HazelcastClient.newHazelcastClient(clientConfig);
        readStatsClient.testHybridCS(hzClient);
        Document doc = hzClient.<String, Document>getMap("pain001").get("1");
        System.out.println(doc.getCstmrCdtTrfInitn().getGrpHdr().getMsgId());
        // start a thread to read stats
        // readStatsClient.printMetrics(hzClient);

        // wait for the user to press a key
        System.out.println("Press any key to exit");
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * See if we can use zero config compact classes and custom compact serializers
     * together
     * 
     * @throws Exception
     */
    private void testHybridCS(HazelcastInstance hzClient) throws Exception {
        addDomainClass(hzClient);
        Pain001Generator gen = new Pain001Generator();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Document doc = gen.outXML(os, 1, 50);
        try {
            os.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        hzClient.getMap("pain001").put("1", doc);
    }

    private void addDomainClass(HazelcastInstance hzClient) throws Exception {
        UserCodeNamespaceConfig defaultNSConfig = new UserCodeNamespaceConfig("default");
        defaultNSConfig.addJar(
                new File("/Users/raj/src/streaming-payments/pmt-domain/target/pmt-domain-1.0.0-SNAPSHOT.jar").toURI()
                        .toURL(),
                "com.hz.demo:pmt-domain:1.0.0:jar");
        MapConfig customersMapConfig = new MapConfig("customers");
        // customersMapConfig.getEventJournalConfig().setEnabled(true);//not supported
        // on client side
        customersMapConfig.setUserCodeNamespace("default");
        hzClient.getConfig().getNamespacesConfig().addNamespaceConfig(defaultNSConfig);
        hzClient.getConfig().addMapConfig(customersMapConfig);

    }

    private void printMetrics(HazelcastInstance hzClient) {
        new Thread(() -> {
            while (true) {

                Map<String, Map<String, Double>> jobMetricsTotals = new HashMap<>();

                hzClient.getJet().getJobs().forEach(job -> {
                    Map<String, Double> metricTotals = StreamSupport.stream(job.getMetrics().spliterator(), false)
                            // get all the measurements
                            .flatMap(metric -> StreamSupport.stream(metric.getValue().spliterator(), false))
                            // filter out the measurements that are not user metrics
                            .filter(measurement -> {
                                String userStr = measurement.tag(MetricTags.USER);
                                return userStr != null && userStr.equals("true");
                            })
                            // group the measurements by metric name and sum the values
                            .collect(Collectors.groupingBy(
                                    Measurement::metric,
                                    Collectors.summingDouble(Measurement::value)));

                    jobMetricsTotals.put(job.getName(), metricTotals);
                });

                jobMetricsTotals.forEach((jobName, metrics) -> {
                    System.out.println("Job: " + jobName);
                    metrics.forEach((metric, total) -> {
                        System.out.println("  Metric: " + metric + " Total Value: " + total);
                    });

                    // Calculate and print averages
                    metrics.forEach((metric, total) -> {
                        if (metric.endsWith("Timer")) {
                            String counterMetric = metric.replace("Timer", "Counter");
                            Double counterValue = metrics.get(counterMetric);
                            if (counterValue != null && counterValue != 0) {
                                double average = total / counterValue;
                                System.out.println("  Average for " + metric + ": " + average);
                            }
                        }
                    });
                });
                try {
                    TimeUnit.SECONDS.sleep(5L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
