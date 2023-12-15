package com.hz.demo.pmt.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

public class PaymentProducer {

    private static final int MAX_BATCH_SIZE = 16 * 1024;
    private static final int QUANTITY = 10_000;
    private static final String TOPIC = "payments";

    private final int rate;
    private final Map<String, Integer> symbolToPrice;
    private final KafkaProducer<String, String> producer;
    private final List<String> symbols;

    private long emitSchedule;

    public static void main(String[] args) throws InterruptedException, IOException {
        if (args.length == 0) {
            System.out.println("PaymentProducer <bootstrap servers> <rate>");
            System.exit(1);
        }
        String servers = args[0];
        int rate = Integer.parseInt(args[1]);
        Properties props = new Properties();
        props.load(PaymentProducer.class.getResourceAsStream("kafka.properties"));
        if (!servers.isEmpty()) {
            props.setProperty("bootstrap.servers", servers);
        }
        props.setProperty("key.serializer", StringSerializer.class.getName());
        props.setProperty("value.serializer", StringSerializer.class.getName());

        new PaymentProducer(props, rate, loadSymbols()).run();
    }

    private PaymentProducer(Properties props, int rate, List<String> symbols) {
        this.rate = rate;
        this.symbols = symbols;
        this.symbolToPrice = symbols.stream().collect(Collectors.toMap(t -> t, t -> 2500));
        this.producer = new KafkaProducer<>(props);
        this.emitSchedule = System.nanoTime();
    }
    
    /**
     * This method will create pain.001 messages and send them to Kafka
     * @throws InterruptedException
     */
    private void run() throws InterruptedException {
        System.out.println("Producing " + rate + " payments per second");
        long interval = TimeUnit.SECONDS.toNanos(1) / rate;
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        var banks = getAllBank("banks.txt");
        while (true) {
            for (int i = 0; i < MAX_BATCH_SIZE; i++) {
                if (System.nanoTime() < emitSchedule) {
                    break;
                }
                String id = UUID.randomUUID().toString();
                String payment = createPayment(id, rnd, banks);
                producer.send(new ProducerRecord<>(TOPIC, id, payment));
                emitSchedule += interval;
            }
            Thread.sleep(1);
        }
    }

    private String createPayment(String id, ThreadLocalRandom rnd, List<String> banks) {
        //get two random banks but make sure they are not the same
        String debtor = banks.get(rnd.nextInt(banks.size()));
        String creditor = banks.get(rnd.nextInt(banks.size()));
        while (debtor.equals(creditor)) {
            creditor = banks.get(rnd.nextInt(banks.size()));
        }
        //create a pain.001 message
        String payment = String.format("{" +
                        "\"id\": \"%s\"," +
                        "\"timestamp\": %d," +
                        "\"amount\": %d," +
                        "\"currency\": \"%s\"," +
                        "\"debtor\": \"%s\"," +
                        "\"creditor\": \"%s\"" +
                        "}",
                id,
                System.currentTimeMillis(),
                rnd.nextInt(1, 1000),
                "EUR",
                debtor,
                creditor
        );
        return payment;
    }

    private List<String> getAllBank(String filePath) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                PaymentProducer.class.getResourceAsStream(filePath), UTF_8))) {
            return reader.lines().collect(toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
 

    private static List<String> loadSymbols() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                PaymentProducer.class.getResourceAsStream("/nasdaqlisted.txt"), UTF_8))
        ) {
            return reader.lines()
                         .skip(1)
                         .map(l -> l.split("\\|")[0])
                         .collect(toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}