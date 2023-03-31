package com.honyee.app.web;

import com.honyee.app.config.http.MyResponse;
import com.honyee.app.config.TenantHelper;
import com.honyee.app.config.jwt.LoginTypeEnum;
import com.honyee.app.config.jwt.TokenProvider;
import com.honyee.app.config.jwt.my.MyAuthenticationToken;
import com.honyee.app.config.seurity.SecurityConfiguration;
import com.honyee.app.dto.LoginRequestDTO;
import com.honyee.app.dto.LoginResponseDTO;
import com.honyee.app.model.Role;
import com.honyee.app.model.User;
import com.honyee.app.service.MyUserDetailService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/api")
public class AuthenticateController {

    @Resource
    private SecurityConfiguration securityConfiguration;

    @Resource
    private MyUserDetailService userDetailService;

    @Operation(summary = "登录")
    @PostMapping("/authenticate")
    public MyResponse<LoginResponseDTO> login(@Validated @RequestBody LoginRequestDTO dto) {
        AuthenticationManager authenticationManager = securityConfiguration.getAuthenticationManager();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        Authentication authenticate = authenticationManager.authenticate(token);
        User userPrincipal = (User) authenticate.getPrincipal();
        MyAuthenticationToken authenticationToken = new MyAuthenticationToken(userPrincipal.getId());
        String jwt = TokenProvider.createToken(
            authenticationToken,
            false,
            LoginTypeEnum.COMMON.toString(),
            authenticationToken.getUserId(),
            data -> data
        );
        return MyResponse.ok(new LoginResponseDTO(jwt));
    }

    @Operation(summary = "用户角色")
    @GetMapping("/my-roles")
    public ResponseEntity<List<Role>> myRoles() {
        return ResponseEntity.ok(userDetailService.findRolesByUserId(TenantHelper.getTenantId()));
    }
}
