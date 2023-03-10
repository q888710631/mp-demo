package com.mp.web;

import com.mp.config.MyResponse;
import com.mp.config.TenantHelper;
import com.mp.config.jwt.LoginTypeEnum;
import com.mp.config.jwt.TokenProvider;
import com.mp.config.jwt.my.MyAuthenticationToken;
import com.mp.config.seurity.UserPrincipal;
import com.mp.dto.LoginRequestDTO;
import com.mp.dto.LoginResponseDTO;
import com.mp.model.Role;
import com.mp.service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api")
public class AuthenticateController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailService userDetailService;

    @PostMapping("/authenticate")
    public MyResponse<LoginResponseDTO> login(@Validated @RequestBody LoginRequestDTO dto) throws Exception {
        Authentication authenticate = authenticate(dto.getUsername(), dto.getPassword());
        UserPrincipal userPrincipal = (UserPrincipal) authenticate.getPrincipal();
        MyAuthenticationToken authenticationToken = new MyAuthenticationToken(userPrincipal.getId(), userPrincipal.getAuthorities());
        String jwt = TokenProvider.createToken(
            authenticationToken,
            false,
            LoginTypeEnum.COMMON.toString(),
            authenticationToken.getUserId(),
            data -> data
        );
        return MyResponse.ok(new LoginResponseDTO(jwt));
    }

    private Authentication authenticate(String username, String password) throws Exception {
        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @GetMapping("/my-roles")
    public ResponseEntity<List<Role>> myRoles() {
        return ResponseEntity.ok(userDetailService.findRolesByUserId(TenantHelper.getTenantId()));
    }
}
