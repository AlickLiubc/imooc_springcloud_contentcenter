package com.itmuch.contentcenter;

import com.itmuch.contentcenter.dao.content.ShareMapper;
import com.itmuch.contentcenter.domain.entity.content.Share;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class TestController {

    @Autowired
    private ShareMapper shareMapper;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/test")
    public List<Share> test() {
        Share share = new Share();
        share.setAuthor("xxx");
        share.setCreateTime(new Date());
        share.setUpdateTime(new Date());

        shareMapper.insertSelective(share);

        List<Share> shares = shareMapper.selectAll();

        return shares;
    }

    /**
     * 证明用户中心总是能找到内容中心
     * @return
     */
    @GetMapping("/test2")
    public List<ServiceInstance> getInstances() {
        return this.discoveryClient.getInstances("user-center");
    }

}
