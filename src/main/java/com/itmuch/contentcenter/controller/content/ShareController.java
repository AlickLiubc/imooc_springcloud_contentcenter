package com.itmuch.contentcenter.controller.content;

import com.github.pagehelper.PageInfo;
import com.itmuch.contentcenter.auth.CheckLogin;
import com.itmuch.contentcenter.domain.dto.content.ShareDTO;
import com.itmuch.contentcenter.domain.entity.content.Share;
import com.itmuch.contentcenter.service.content.ShareService;
import com.itmuch.contentcenter.util.JwtOperator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/shares")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareController {

    @Autowired
    private ShareService shareService;

    @Autowired
    private JwtOperator jwtOperator;

    @GetMapping("/{id}")
    @CheckLogin
    public ShareDTO findById(@PathVariable("id")Integer id) {
        return shareService.findById(id);
    }


    @GetMapping("/q")
    public PageInfo<Share> q(@RequestParam(required = false) String title,
                             @RequestParam(required = false, defaultValue = "1")Integer pageNo,
                             @RequestParam(required = false, defaultValue = "100")Integer pageSize,
                             @RequestHeader("X-token") String token
                            ) {
        if (pageSize > 100) {
            pageSize = 100;
        }

        Integer userId = null;
        if (StringUtils.isNotBlank(token)) {
            Claims claimsFromToken = jwtOperator.getClaimsFromToken(token);
            userId = (Integer) claimsFromToken.get("id");
        }

        return this.shareService.selectByParam(title, pageNo, pageSize, userId);
    }

    @GetMapping("/exchange/{id}")
    @CheckLogin
    public Share exchangeById(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        return this.shareService.exchangeById(id, httpServletRequest);
    }

}
