package com.sy.product_service.service

import com.sy.product_service.domain.Product
import com.sy.product_service.dto.ProductCreateRequest
import com.sy.product_service.dto.ProductResponse
import com.sy.product_service.dto.ProductUpdateRequest

interface ProductService {
    fun createProduct(createRequest: ProductCreateRequest, sellerId: Long): ProductResponse
    fun getAllProducts(): List<ProductResponse>
    fun getProductById(productId: Long): ProductResponse
    fun updateProduct(productId: Long, updateRequest: ProductUpdateRequest, userId: Long): ProductResponse
    fun deleteProduct(productId: Long, userId: Long)
    fun findProductOrThrow(productId: Long): Product
    fun Product.toResponse(sellerNickname: String): ProductResponse
    fun getSellerNickname(sellerId: Long): String
}
