package com.hz.demo.pmt.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hz.demo.pmt.domain.Payment;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SenderService {

    private static final Logger LOG = LoggerFactory.getLogger(SenderService.class);

    AtomicLong id = new AtomicLong();
    @Autowired
    KafkaTemplate<String, Payment> template;

    @Value("${POD:pmt-producer}")
    private String pod;
    @Value("${NAMESPACE:empty}")
    private String namespace;
    @Value("${CLUSTER:localhost}")
    private String cluster;
    @Value("${TOPIC:payments}")
    private String topic;

    @Scheduled(fixedRate = 1000)
    public void send() {
        var banks = getAllBank("/banks.txt");
        String id = UUID.randomUUID().toString();
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        Payment payment = createPayment(id, rnd, banks);
        CompletableFuture<SendResult<String, Payment>> result = template.send(topic, payment.getId(), payment);
        result.whenComplete((sr, ex) ->
                LOG.info("Sent({}): {}. With any Exception: {}", sr.getProducerRecord().key(), sr.getProducerRecord().value(), ex));
    }

    private Payment createPayment(String id, ThreadLocalRandom rnd, List<String> banks) {
        //get two random banks but make sure they are not the same
        String debtor = banks.get(rnd.nextInt(banks.size()));
        String creditor = banks.get(rnd.nextInt(banks.size()));
        while (debtor.equals(creditor)) {
            creditor = banks.get(rnd.nextInt(banks.size()));
        }

        return new Payment(id, debtor, creditor, rnd.nextInt(1, 1000), "EUR", System.currentTimeMillis() );
    }

    private List<String> getAllBank(String filePath) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                PaymentProducerSpring.class.getResourceAsStream(filePath), UTF_8))) {
            return reader.lines().collect(toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}