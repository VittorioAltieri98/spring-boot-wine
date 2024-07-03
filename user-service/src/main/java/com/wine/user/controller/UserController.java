package com.wine.user.controller;

import com.wine.user.dto.UserWinePairingDTO;
import com.wine.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

   @Autowired
   private UserService userService;

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/my-pairings")
    public ResponseEntity<List<UserWinePairingDTO>> getAllUserWinePairings(@AuthenticationPrincipal Jwt jwt){

        List<UserWinePairingDTO> response = userService.getAllUserWinePairings(jwt);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
