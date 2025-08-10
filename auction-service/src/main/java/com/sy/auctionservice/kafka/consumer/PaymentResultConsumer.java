package com.sy.auctionservice.kafka.consumer;

import com.sy.auctionservice.service.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentResultConsumer {

    private final AuctionService auctionService; // 보상 트랜잭션 등을 위임

    @KafkaListener(topics = "payment-result-events", groupId = "auction-group")
    public void consume(PaymentResultEvent event) {
        log.info("Consumed PaymentResultEvent: {}", event);

        if (event.getStatus() == PaymentStatus.SUCCESS) {
            // 결제 성공 시 후속 처리
            auctionService.finalizeAuction(event.getAuctionId());
        } else {
            // 결제 실패 시 보상 트랜잭션 실행
            auctionService.compensateAuction(event.getAuctionId());
        }
    }
}