package com.sy.auctionservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // 상품명

    @Lob // 긴 텍스트를 저장하기 위해 사용
    @Column(nullable = false)
    private String description; // 상품 설명

    @Column(nullable = false)
    private Long sellerId; // 판매자 ID (User 서비스의 PK)

    @Column(nullable = false)
    private BigDecimal startingPrice; // 경매 시작 가격

    // 이미지 URL 목록을 별도의 테이블에 저장
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_image_urls", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 등록 일시

    // --- 생성자 ---
    @Builder
    private Product(String name, String description, Long sellerId, BigDecimal startingPrice, List<String> imageUrls) {
        // 필수 값 검증
        Assert.hasText(name, "Product name must not be empty");
        Assert.hasText(description, "Product description must not be empty");
        Assert.notNull(sellerId, "Seller ID must not be null");
        Assert.notNull(startingPrice, "Starting price must not be null");

        this.name = name;
        this.description = description;
        this.sellerId = sellerId;
        this.startingPrice = startingPrice;
        this.imageUrls = (imageUrls != null) ? imageUrls : new ArrayList<>();
        this.createdAt = LocalDateTime.now();
    }
}