package kr.kimwz.sample.controller;

import kr.kimwz.sample.service.TimeService;
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
        return timeService.getNow(name);
    }
}
