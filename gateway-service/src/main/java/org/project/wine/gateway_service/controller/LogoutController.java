package org.project.wine.gateway_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logout")
public class LogoutController {

    @GetMapping("/success")
    public ResponseEntity<String> logoutSuccess(){
        return new ResponseEntity<>("Forza Lazio Carica La La La", HttpStatus.OK);
    }
}
