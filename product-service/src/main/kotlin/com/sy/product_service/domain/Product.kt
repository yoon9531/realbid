package com.sy.product_service.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "products")
class Product(
    @Column(nullable = false, length = 100)
    var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var description: String,

    @Column(nullable = false)
    val startPrice: Long,

    @Column(nullable = false, name = "seller_id")
    val sellerId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ProductStatus = ProductStatus.FOR_SALE,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}

enum class ProductStatus {
    FOR_SALE,  // 판매 중
    SOLD,      // 판매 완료
    CANCELLED  // 판매 취소
}
