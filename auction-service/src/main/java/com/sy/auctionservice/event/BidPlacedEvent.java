package com.sy.auctionservice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 새로운 입찰이 발생했을 때 생성되는 이벤트를 나타내는 클래스.
 * 실시간 분석 및 모든 입찰 행위를 기록(Event Sourcing)하는 데 사용된다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BidPlacedEvent {

    /**
     * 입찰이 발생한 경매의 고유 ID
     */
    private Long auctionId;

    /**
     * 입찰을 한 사용자의 ID
     */
    private Long bidderId;

    /**
     * 입찰한 금액
     */
    private BigDecimal bidAmount;

    /**
     * 입찰이 발생한 정확한 시간
     */
    private LocalDateTime timestamp;

}
