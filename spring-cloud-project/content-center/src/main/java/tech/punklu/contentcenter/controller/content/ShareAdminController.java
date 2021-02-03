package tech.punklu.contentcenter.controller.content;

import org.springframework.web.bind.annotation.*;
import tech.punklu.contentcenter.domain.dto.content.ShareAuditDTO;
import tech.punklu.contentcenter.domain.entity.content.Share;
import tech.punklu.contentcenter.service.content.ShareService;

@RestController
@RequestMapping("/admin/shares")
public class ShareAdminController {

    private ShareService shareService;

    @PutMapping("/audit/{id}")
    public Share auditById(@PathVariable Integer id,
                           @RequestBody ShareAuditDTO auditDTO){
        // TODO 认证、授权
        return this.shareService.auditById(id,auditDTO);
    }
}
