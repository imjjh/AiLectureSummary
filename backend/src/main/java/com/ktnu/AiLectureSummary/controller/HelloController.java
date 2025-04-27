package com.ktnu.AiLectureSummary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    /**
     * Handles HTTP GET requests to the "/hello" endpoint and returns a greeting message.
     *
     * @return a plain text greeting indicating the service is running
     */
    @GetMapping("/hello")
    public String hello(){
        return "hello from docker!";
    }

}
