package com.itmuch.contentcenter.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "baidu-service", url = "https://www.baidu.com")
public interface TestBaiduFeignClient {

    @GetMapping("")
    String index();

}
