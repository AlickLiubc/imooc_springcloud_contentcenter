package com.itmuch.contentcenter.domain.dto.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserAddBonusMessageDTO {

    private Integer userId;

    private Integer bonus;

}
