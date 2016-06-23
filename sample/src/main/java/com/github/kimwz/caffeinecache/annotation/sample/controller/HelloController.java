package com.github.kimwz.caffeinecache.annotation.sample.controller;

import com.github.kimwz.caffeinecache.annotation.sample.service.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private TimeService timeService;

    @RequestMapping("/")
    public String index(@RequestParam(defaultValue = "karl") String name) throws Exception {
        try {
            return timeService.getNow(name);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
