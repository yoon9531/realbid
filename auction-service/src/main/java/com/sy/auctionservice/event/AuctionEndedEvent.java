package com.sy.auctionservice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuctionEndedEvent {

    /**
     * 경매의 고유 ID
     */
    private Long auctionId;

    /**
     * 최종 낙찰자의 사용자 ID
     */
    private Long winnerId;

    /**
     * 상품의 고유 ID
     */
    private Long productId;

    /**
     * 상품명 (결제 내역 표시 등 편의성을 위해 포함)
     */
    private String productName;

    /**
     * 최종 낙찰 금액
     */
    private BigDecimal finalPrice;

    /**
     * 이벤트 발생 시간
     */
    private LocalDateTime timestamp;

}