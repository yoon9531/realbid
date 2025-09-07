package com.sy.product_service.controller

import com.sy.product_service.dto.ProductCreateRequest
import com.sy.product_service.dto.ProductResponse
import com.sy.product_service.dto.ProductUpdateRequest
import com.sy.product_service.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {

    /**
     * 새로운 상품을 등록합니다.
     * @param createRequest 상품 생성 정보 DTO
     * @return 생성된 상품 정보 (HTTP 201 Created)
     */
    @PostMapping
    fun createProduct(
        @Valid @RequestBody createRequest: ProductCreateRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<ProductResponse> {
        val currentUserId = userDetails.username.toLong()
        val response = productService.createProduct(createRequest, currentUserId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * 등록된 모든 상품 목록을 조회합니다.
     * @return 상품 목록 (HTTP 200 OK)
     */
    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductResponse>> {
        val responses = productService.getAllProducts()
        return ResponseEntity.ok(responses)
    }

    /**
     * 특정 ID의 상품 상세 정보를 조회합니다.
     * @param productId 조회할 상품의 ID
     * @return 상품 상세 정보 (HTTP 200 OK)
     */
    @GetMapping("/{productId}")
    fun getProductById(@PathVariable productId: Long): ResponseEntity<ProductResponse> {
        val response = productService.getProductById(productId)
        return ResponseEntity.ok(response)
    }

    /**
     * 특정 ID의 상품 정보를 수정.
     * @param productId 수정할 상품의 ID
     * @param updateRequest 상품 수정 정보 DTO
     */
    @PutMapping("/{productId}")
    fun updateProduct(
        @PathVariable productId: Long,
        @Valid @RequestBody updateRequest: ProductUpdateRequest
    ): ResponseEntity<ProductResponse> {
        // TODO: 실제로는 Spring Security의 Authentication 객체에서 사용자 ID를 가져와야 함
        val currentUserId = 1L // 임시 사용자 ID
        val response = productService.updateProduct(productId, updateRequest, currentUserId)
        return ResponseEntity.ok(response)
    }

    /**
     * 특정 ID의 상품을 삭제.
     * @param productId 삭제할 상품의 ID
     */
    @DeleteMapping("/{productId}")
    fun deleteProduct(@PathVariable productId: Long): ResponseEntity<Void> {
        // TODO: 실제로는 Spring Security의 Authentication 객체에서 사용자 ID를 가져와야 함
        val currentUserId = 1L // 임시 사용자 ID
        productService.deleteProduct(productId, currentUserId)
        return ResponseEntity.noContent().build()
    }
}