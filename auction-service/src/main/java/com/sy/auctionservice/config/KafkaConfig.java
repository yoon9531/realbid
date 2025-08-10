package com.sy.auctionservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic auctionEvents() {
        return TopicBuilder.name("auction-events").build(); // 경매 생성, 종료 등
    }

    @Bean
    public NewTopic bidEvents() {
        return TopicBuilder.name("bid-events").build(); // 모든 입찰 기록
    }

    @Bean
    public NewTopic paymentResultEvents() {
        // 결제 서비스의 처리 결과를 구독할 토픽
        return TopicBuilder.name("payment-result-events").build();
    }
}
