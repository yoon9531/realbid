package com.sy.product_service.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime


/**
 * 상품 생성 요청 DTO
 * @property title 상품명
 * @property description 상품 설명
 * @property startPrice 경매 시작 가격
 */
data class ProductCreateRequest(
    @field:NotBlank(message = "상품명은 필수 입력 항목입니다.")
    @field:Size(max = 100, message = "상품명은 100자를 초과할 수 없습니다.")
    val title: String,

    @field:NotBlank(message = "상품 설명은 필수 입력 항목입니다.")
    val description: String,

    @field:NotNull(message = "시작 가격은 필수 입력 항목입니다.")
    @field:Positive(message = "시작 가격은 0보다 커야 합니다.")
    val startPrice: Long
)

/**
 * 상품 정보 수정 요청 DTO
 * @property title 상품명
 * @property description 상품 설명
 */
data class ProductUpdateRequest(
    @field:NotBlank(message = "상품명은 필수 입력 항목입니다.")
    @field:Size(max = 100, message = "상품명은 100자를 초과할 수 없습니다.")
    val title: String,

    @field:NotBlank(message = "상품 설명은 필수 입력 항목입니다.")
    val description: String
)

/**
 * 상품 정보 응답 DTO
 * @property productId 상품 ID
 * @property title 상품명
 * @property description 상품 설명
 * @property sellerNickname 판매자 닉네임
 * @property status 상품 상태
 * @property createdAt 등록 일시
 */
data class ProductResponse(
    val productId: Long,
    val title: String,
    val description: String,
    val sellerNickname: String,
    val status: String,
    val createdAt: LocalDateTime
)