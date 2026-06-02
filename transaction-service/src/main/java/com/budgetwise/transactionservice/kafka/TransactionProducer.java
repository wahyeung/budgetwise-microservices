package com.budgetwise.transactionservice.kafka;

import com.budgetwise.transactionservice.event.TransactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionProducer {

    private final KafkaTemplate<String, TransactionCreatedEvent> kafkaTemplate;
    private static final String TOPIC = "transaction-created";

    public void sendTransactionCreatedEvent(TransactionCreatedEvent event) {
        // partition key = accountId, ensuring events for the same account are ordered.
        kafkaTemplate.send(TOPIC, String.valueOf(event.getAccountId()), event);
        log.info("Published TransactionCreated event: accountId={}, amount={}, type={}",
                event.getAccountId(), event.getAmount(), event.getType());
    }
}