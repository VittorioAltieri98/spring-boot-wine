package com.wine.user.controller;

import com.wine.user.dto.UserInfo;
import com.wine.user.dto.UserInfoWithID;
import com.wine.user.dto.UserWinePairingDTO;
import com.wine.user.exception.UserAlreadyExistsException;
import com.wine.user.exception.UserNotFoundException;
import com.wine.user.service.UserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    @PreAuthorize("hasAnyRole('admin')")
    @GetMapping("/allUsers")
    public ResponseEntity<List<UserInfoWithID>> getAllUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("my-profile")
    public ResponseEntity<UserInfo> getUserProfile(@AuthenticationPrincipal Jwt jwt) {
        return new ResponseEntity<>(userService.getUserProfileInfo(jwt.getSubject()), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @PutMapping("/update")
    public void updateUser(@AuthenticationPrincipal Jwt jwt, @RequestBody UserInfo userInfo) throws UserAlreadyExistsException {
        userService.updateUser(jwt.getSubject(), userInfo);
    }

    @PreAuthorize("hasAnyRole('admin')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) throws UserNotFoundException {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('user')")
    @DeleteMapping("/my-profile/delete")
    public ResponseEntity<Void> deleteUserProfile(@AuthenticationPrincipal Jwt jwt) {
        userService.deleteUserProfile(jwt.getSubject());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
