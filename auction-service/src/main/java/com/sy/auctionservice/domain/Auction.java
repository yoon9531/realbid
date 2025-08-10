package com.sy.auctionservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private Long productId; // 상품 ID
    private Long sellerId; // 판매자 ID
    private Long highestBidderId; // 최고 입찰자 ID
    private BigDecimal highestBid; // 최고 입찰가
    private Long startTime; // 경매 시작 시간 (타임스탬프)
    private Long endTime; // 경매 종료 시간 (타임스탬프)
    private boolean isActive; // 경매 활성화 여부
    private boolean isCompleted; // 경매 완료 여부
    private boolean isCancelled; // 경매 취소 여부
    private Long startingPrice; // 시작 가격
    private Long currentPrice; // 현재 가격
    private Long bidCount; // 입찰 수
}
