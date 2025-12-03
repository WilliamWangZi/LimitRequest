package com.example.limit.controller;

import com.example.limit.annotation.Limit;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 限流测试
 */
@RestController
public class LimitTestController {

    @PostMapping(value = "/login")
    @Limit
    public String login(
            HttpServletRequest request,
            @RequestParam(value="user")String user
    ){
        request.getSession().setAttribute("user", user);
        return user;
    }
    @Limit
    @GetMapping(value = "/index")
    public String testLimitRequest(){
        return "request success";
    }
}
