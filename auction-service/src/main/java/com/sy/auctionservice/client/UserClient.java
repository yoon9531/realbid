package com.sy.auctionservice.client;

import com.sy.auctionservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service") // Eureka에 등록된 서비스 이름
public interface UserClient {
    @GetMapping("/api/users/{userId}")
    UserDto getUser(@PathVariable Long userId); // 사용자 정보를 가져오는 API
}
