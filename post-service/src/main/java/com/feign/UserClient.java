package com.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.config.FeignConfig;
import com.dto.UserDTO;

@FeignClient(name = "user-service", url = "http://localhost:8001", configuration = FeignConfig.class )
public interface UserClient {
    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
}
