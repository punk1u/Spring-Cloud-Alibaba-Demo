package tech.punklu.contentcenter.domain.dto.content;

import lombok.Data;
import tech.punklu.contentcenter.domain.enums.AuditStatusEnum;

@Data
public class ShareAuditDTO {

    /**
     * 审核状态
     */
    private AuditStatusEnum auditStatusEnum;

    /**
     * 原因
     */
    private String reason;
}
