package tech.punklu.contentcenter.controller.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.punklu.contentcenter.auth.CheckLogin;
import tech.punklu.contentcenter.domain.dto.content.ShareDTO;
import tech.punklu.contentcenter.service.content.ShareService;

@RestController
@RequestMapping("/shares")
public class ShareController {

    @Autowired
    private ShareService shareService;

    @GetMapping("/{id}")
    @CheckLogin
    public ShareDTO findById(@PathVariable Integer id){
        return this.shareService.findById(id);
    }

}
