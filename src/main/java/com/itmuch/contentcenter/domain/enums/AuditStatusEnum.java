package com.itmuch.contentcenter.domain.enums;


import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public enum AuditStatusEnum {

    /**
     * 未审核
     */
    NOT_YET,

    /**
     * 审核通过
     */
    PASS,

    /**
     * 审核未通过
     */
    REJECT;

}
