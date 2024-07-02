package org.project.wine.gateway_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
public class LogoutController {

    @PostMapping("/logout")
    public Mono<Void> logout(ServerWebExchange exchange){
        SecurityContextServerLogoutHandler logoutHandler = new SecurityContextServerLogoutHandler();
        return logoutHandler.logout((WebFilterExchange) exchange, SecurityContextHolder.getContext().getAuthentication());
    }

    @GetMapping("/logout/success")
    public ResponseEntity<String> logoutSuccess(){
        return new ResponseEntity<>("Forza Lazio Carica La La La", HttpStatus.OK);
    }
}
