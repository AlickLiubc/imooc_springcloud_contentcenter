package com.itmuch.contentcenter.service.content;

import com.itmuch.contentcenter.dao.content.ShareMapper;
import com.itmuch.contentcenter.domain.dto.content.ShareDTO;
import com.itmuch.contentcenter.domain.dto.user.UserDTO;
import com.itmuch.contentcenter.domain.entity.content.Share;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareService {

    @Autowired
    private final ShareMapper shareMapper;

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    private final DiscoveryClient discoveryClient;

    public ShareDTO findById(Integer id) {
        // 获取分享详情
        Share share = shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();

        List<ServiceInstance> instances = discoveryClient.getInstances("user-center");
//        String targetUrl = instances.stream()
//                .map(x -> x.getUri().toString() + "/users/{userId}")
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("没有找到对应的用户中心服务！"));

//        List<String> targetURLs = instances.stream()
//                .map(x -> x.getUri().toString() + "/users/{userId}")
//                .collect(Collectors.toList());
//
//        int i = ThreadLocalRandom.current().nextInt(targetURLs.size());
//
//        userId = 1;
//        String targetURL = targetURLs.get(i);
//        log.info("请求的目标地址为：[{}]", targetURL);
//        // "http://localhost:8080/users/{userId}"
//        UserDTO userDTO = restTemplate.getForObject(
//                targetURL,
//                UserDTO.class,
//                userId);

        userId = 1;
        UserDTO userDTO = restTemplate.getForObject(
                "http://user-center/users/{userId}",
                UserDTO.class,
                userId);

        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());

        return shareDTO;
    }

    public static void main(String[] args) {

    }

}
