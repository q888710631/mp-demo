package com.honyee.web;

import com.honyee.config.TenantHelper;
import com.honyee.service.GenericService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "当前用户可用菜单")
    @GetMapping("/user-menu")
    public List<String> userMenu() {
        return genericService.userMenu(TenantHelper.getTenantId());
    }

}
