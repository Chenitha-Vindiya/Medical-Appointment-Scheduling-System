package com.medischeduler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "index"; // Opens index.jsp
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Opens login.jsp
    }
}