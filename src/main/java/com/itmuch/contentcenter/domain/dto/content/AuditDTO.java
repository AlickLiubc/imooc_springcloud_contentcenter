package com.itmuch.contentcenter.domain.dto.content;


import com.itmuch.contentcenter.domain.enums.AuditStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditDTO {

    private AuditStatusEnum auditStatusEnum;

    private String reason;

}
