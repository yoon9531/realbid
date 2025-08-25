package com.sy.auctionservice.kafka.producer;

import com.sy.auctionservice.event.AuctionEndedEvent;
import com.sy.auctionservice.event.BidPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendAuctionEndedEvent(AuctionEndedEvent event) {
        log.info("Issuing AuctionEndedEvent for auctionId: {}", event.getAuctionId());
        // 키를 auctionId로 지정하면 동일 경매 이벤트는 같은 파티션으로 전달되어 순서가 보장됨
        kafkaTemplate.send("auction-events", String.valueOf(event.getAuctionId()), event);
    }

    public void sendBidPlacedEvent(BidPlacedEvent event) {
        log.debug("Issuing BidPlacedEvent for auctionId: {}", event.getAuctionId());
        kafkaTemplate.send("bid-events", String.valueOf(event.getAuctionId()), event);
    }
}