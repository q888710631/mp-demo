package com.mp.web;

import com.mp.config.MyResponse;
import com.mp.config.TenantHelper;
import com.mp.config.jwt.LoginTypeEnum;
import com.mp.config.jwt.TokenProvider;
import com.mp.config.jwt.my.MyAuthenticationToken;
import com.mp.config.seurity.SecurityConfiguration;
import com.mp.config.seurity.UserPrincipal;
import com.mp.dto.LoginRequestDTO;
import com.mp.dto.LoginResponseDTO;
import com.mp.model.Role;
import com.mp.service.MyUserDetailService;
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
        UserPrincipal userPrincipal = (UserPrincipal) authenticate.getPrincipal();
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
