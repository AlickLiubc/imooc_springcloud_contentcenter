package com.itmuch.contentcenter.feignclient;

import com.itmuch.contentcenter.UserCenterFeignConfiguration;
import com.itmuch.contentcenter.domain.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "user-center", configuration = UserCenterFeignConfiguration.class)
@FeignClient(name = "user-center")
public interface UserCenterFeignClient {

    @GetMapping("/users/{userId}")
    UserDTO findByUserId(@PathVariable Integer userId);

}
