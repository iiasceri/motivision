package com.inther.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class TestController {


    /*
     * Test request
     * Used to ping connection
     * @return status - success if got connection
     * */
    @RequestMapping("/test")
    public Map<String, String> testConnection(){
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("status", "success");
        return map;
    }
}
