package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/data")
public class DataController {

    @RequestMapping(method = RequestMethod.GET, value = "/principal")
    public ResponseEntity a(OAuth2Authentication authentication) {
        Map<String, Object> details = (Map<String, Object>) authentication.getUserAuthentication().getDetails();
        return ResponseEntity.ok(details);
    }
}
