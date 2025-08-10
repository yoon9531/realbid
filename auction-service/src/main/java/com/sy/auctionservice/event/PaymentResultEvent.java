package com.sy.auctionservice.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@ToString
public class PaymentResultEvent {

    private Long auctionId;
    private Long userId;
    private BigDecimal amount; // 처리된 금액
    private PaymentStatus status;
    private String reason; // 실패 사유 (예: "INSUFFICIENT_FUNDS")

    // 성공 시 사용할 생성자
    public PaymentResultEvent(Long auctionId, Long userId, BigDecimal amount, PaymentStatus status) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.reason = null; // 성공 시 이유는 없음
    }

    // 실패 시 사용할 생성자
    public PaymentResultEvent(Long auctionId, Long userId, PaymentStatus status, String reason) {
        this.auctionId = auctionId;
        this.userId = userId;
        this.status = status;
        this.reason = reason;
        this.amount = null; // 실패 시 처리 금액 없음
    }
}