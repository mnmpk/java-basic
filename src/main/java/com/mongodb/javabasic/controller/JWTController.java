package com.mongodb.javabasic.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.javabasic.service.JwtService;

@RestController
public class JWTController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private JwtService jwtService;

    @PostMapping("/genJWT")
    public String genJWT(@RequestBody Map<String, ?> claim) {
        return jwtService.generateToken("test", "devicesync-cxmya", claim);
    }

}
