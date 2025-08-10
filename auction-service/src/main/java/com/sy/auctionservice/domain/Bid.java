package com.sy.auctionservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bids", indexes = {
        // 특정 경매의 입찰 내역을 빠르게 조회하기 위해 인덱스 추가
        @Index(name = "idx_bid_auction_id", columnList = "auction_id")
})
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 하나의 경매(Auction)는 여러 개의 입찰(Bid)을 가질 수 있음 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @Column(nullable = false)
    private Long bidderId; // 입찰자 ID (User 서비스의 PK)

    @Column(nullable = false)
    private BigDecimal bidAmount; // 입찰 금액

    @Column(nullable = false, updatable = false)
    private LocalDateTime bidTime; // 입찰 시각 (한 번 생성되면 변경 불가)

    // --- 생성자 ---
    public Bid(Auction auction, Long bidderId, BigDecimal bidAmount) {
        // 필수 값 검증
        Assert.notNull(auction, "Auction must not be null");
        Assert.notNull(bidderId, "Bidder ID must not be null");
        Assert.notNull(bidAmount, "Bid amount must not be null");

        this.auction = auction;
        this.bidderId = bidderId;
        this.bidAmount = bidAmount;
        this.bidTime = LocalDateTime.now(); // 객체 생성 시 현재 시각으로 자동 할당
    }
}