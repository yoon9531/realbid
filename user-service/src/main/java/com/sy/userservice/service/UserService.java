package com.sy.userservice.service;

import com.sy.userservice.dto.UserResponseDto;
import com.sy.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto.UserBasicInfoResponse getUserBasicInfo () {

    }

}
