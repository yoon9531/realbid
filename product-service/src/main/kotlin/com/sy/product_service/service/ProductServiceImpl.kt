package com.sy.product_service.service

import com.sy.product_service.client.UserClient
import com.sy.product_service.client.UserResponse
import com.sy.product_service.domain.Product
import com.sy.product_service.dto.ProductCreateRequest
import com.sy.product_service.dto.ProductResponse
import com.sy.product_service.dto.ProductUpdateRequest
import com.sy.product_service.exception.UnauthorizedAccessException
import com.sy.product_service.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val userClient: UserClient
): ProductService {

    @Transactional
    override fun createProduct(createRequest: ProductCreateRequest, sellerId: Long): ProductResponse {
        val product = Product(
            title = createRequest.title,
            description = createRequest.description,
            startPrice = createRequest.startPrice,
            sellerId = sellerId
        )

        val savedProduct = productRepository.save(product)
        return savedProduct.toResponse(getSellerNickname(sellerId).toString())
    }

    override fun getAllProducts(): List<ProductResponse> {
        return productRepository.findAll()
            .map { it.toResponse(getSellerNickname(it.sellerId)) }
    }

    override fun getProductById(productId: Long): ProductResponse {
        val product = findProductOrThrow(productId)
        return product.toResponse(getSellerNickname(product.sellerId))
    }

    override fun updateProduct(productId: Long, updateRequest: ProductUpdateRequest, userId: Long): ProductResponse {
        val product = findProductOrThrow(productId)

        if (product.sellerId != userId) {
            throw UnauthorizedAccessException("이 상품을 수정할 권한이 없습니다.")
        }

        product.title = updateRequest.title
        product.description = updateRequest.description

        return product.toResponse(getSellerNickname(product.sellerId))
    }

    override fun deleteProduct(productId: Long, userId: Long) {
        val product = findProductOrThrow(productId)

        if (product.sellerId != userId) {
            throw UnauthorizedAccessException("이 상품을 삭제할 권한이 없습니다.")
        }

        productRepository.delete(product)
    }

    override fun findProductOrThrow(productId: Long): Product {
        return productRepository.findById(productId).orElseThrow {
            IllegalArgumentException("Product with ID $productId not found")
        }
    }

    override fun Product.toResponse(sellerNickname: String): ProductResponse {
        return ProductResponse(
            productId = this.id,
            title = this.title,
            description = this.description,
            sellerNickname = sellerNickname,
            status = this.status.name,
            createdAt = this.createdAt
        )
    }

    override fun getSellerNickname(email: String): UserResponse {
        // TODO : 실제 사용자 서비스와 연동하여 닉네임을 가져오도록 구현 필요
        return userClient.getUserInfo(email)
    }
}