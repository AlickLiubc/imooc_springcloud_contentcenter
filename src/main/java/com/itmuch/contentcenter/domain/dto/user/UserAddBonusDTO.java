package com.itmuch.contentcenter.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserAddBonusDTO {

    private Integer userId;

    private Integer bonus;

    private String event;

    private String description;

}
