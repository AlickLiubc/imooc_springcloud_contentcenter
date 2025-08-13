package com.itmuch.contentcenter.feignclient;

import com.itmuch.contentcenter.domain.dto.user.UserAddBonusDTO;
import com.itmuch.contentcenter.domain.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

//@FeignClient(name = "user-center", configuration = UserCenterFeignConfiguration.class)
@FeignClient(name = "user-center")
public interface UserCenterFeignClient {

    @GetMapping("/users/{userId}")
    UserDTO findByUserId(@PathVariable Integer userId);

    @PutMapping("/users/add-bonus")
    UserDTO addBonus(UserAddBonusDTO userAddBonusDTO);

}
