package com.hz.demo.pmt.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.jet.core.metrics.Measurement;
import com.hazelcast.jet.core.metrics.MetricTags;

public class ReadStatsClient {

    public static void main(String[] args) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setClusterName("dev");
        clientConfig.getNetworkConfig().addAddress("localhost:5701");
        HazelcastInstance hzClient = HazelcastClient.newHazelcastClient(clientConfig);

        //start a thread to read stats
        new Thread(() -> {
            while (true) {

                Map<String, Map<String, Double>> jobMetricsTotals = new HashMap<>();
                
                hzClient.getJet().getJobs().forEach(job -> {
                    Map<String, Double> metricTotals = StreamSupport.stream(job.getMetrics().spliterator(), false)
                        //get all the measurements
                        .flatMap(metric -> StreamSupport.stream(metric.getValue().spliterator(), false))
                        //filter out the measurements that are not user metrics
                        .filter(measurement -> {
                            String userStr = measurement.tag(MetricTags.USER);
                            return userStr != null && userStr.equals("true");
                        })
                        //group the measurements by metric name and sum the values
                        .collect(Collectors.groupingBy(
                            Measurement::metric,
                            Collectors.summingDouble(Measurement::value)
                        ));
                    
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
        //wait for the user to press a key
        System.out.println("Press any key to exit");
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
