package com.sy.auctionservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 무분별한 객체 생성을 막기 위해 접근 레벨을 PROTECTED로 설정
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 상품 정보는 Product 엔티티를 통해 관리 (단방향 OneToOne 관계)
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Long sellerId; // 판매자 ID (User 서비스의 PK)

    private Long highestBidderId; // 최고 입찰자 ID (User 서비스의 PK)

    @Column(nullable = false)
    private BigDecimal highestBid; // 최고 입찰가 (초기값은 시작가)

    @Column(nullable = false)
    private LocalDateTime startTime; // 경매 시작 시간

    @Column(nullable = false)
    private LocalDateTime endTime; // 경매 종료 시간

    @Enumerated(EnumType.STRING) // Enum의 이름을 DB에 문자열로 저장
    @Column(nullable = false)
    private AuctionStatus status; // 경매 상태 (LIVE, ENDED 등)

    @Version // 낙관적 잠금(Optimistic Lock)을 위한 버전 필드
    private Long version;

    // --- 생성자 ---
    @Builder
    private Auction(Product product, Long sellerId, BigDecimal startingPrice, LocalDateTime startTime, LocalDateTime endTime) {
        // 필수 값 검증
        Assert.notNull(product, "Product must not be null");
        Assert.notNull(sellerId, "Seller ID must not be null");
        Assert.notNull(startingPrice, "Starting price must not be null");
        Assert.notNull(startTime, "Start time must not be null");
        Assert.notNull(endTime, "End time must not be null");

        this.product = product;
        this.sellerId = sellerId;
        this.highestBid = startingPrice; // 경매 시작 시 최고 입찰가는 시작가와 동일
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AuctionStatus.SCHEDULED; // 생성 시 기본 상태는 '예정'
    }

    // --- 비즈니스 로직 (상태 변경 메서드) ---

    /**
     * 최고 입찰가를 갱신합니다.
     * 이 메서드를 통해서만 최고 입찰가 관련 정보가 변경될 수 있습니다.
     */
    public void updateHighestBid(Long bidderId, BigDecimal newBidAmount) {
        this.highestBidderId = bidderId;
        this.highestBid = newBidAmount;
    }

    /**
     * 경매 상태를 변경합니다.
     */
    public void changeStatus(AuctionStatus newStatus) {
        this.status = newStatus;
    }
}
