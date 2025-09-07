package com.sy.product_service.repository

import com.sy.product_service.domain.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findBySellerId(sellerId: Long): List<Product>
    fun findByStatus(status: String): List<Product>
    fun findByTitle(title: String): Optional<Product>
}