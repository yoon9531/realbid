package com.sy.userservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class testController {

    @GetMapping("/me")
    public String hello() {
        return "Hello user-service";
    }
}