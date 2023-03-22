package com.mp.web;

import com.mp.config.TenantHelper;
import com.mp.service.GenericService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api")
public class GenericController {

    @Resource
    private GenericService genericService;

    /**
     * 当前用户可用菜单
     */
    @GetMapping("/user-menu")
    public List<String> userMenu() {
        return genericService.userMenu(TenantHelper.getTenantId());
    }

}
