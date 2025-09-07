package com.sy.product_service.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "user-service", url = "\${feign.client.user-service.url}")
interface UserClient {

    /**
     * 사용자 ID를 이용해 사용자 정보를 조회.
     * @param userId 조회할 사용자의 ID
     * @return 조회된 사용자 정보 DTO
     */
    @GetMapping("/api/users/{email}")
    fun getUserInfo(@PathVariable("email") email: String): UserResponse
}

/**
 * User Service 로부터 받아올 사용자 정보를 담는 DTO
 */
data class UserResponse(
    val email: String,
    val nickname: String,
)
