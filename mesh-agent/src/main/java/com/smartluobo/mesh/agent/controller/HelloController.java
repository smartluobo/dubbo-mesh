package com.smartluobo.mesh.agent.controller;

import com.smartluobo.mesh.agent.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class HelloController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloController.class);

    @Resource
    private HelloService helloService;

    @RequestMapping("")
    public Object invoke(@RequestParam("interface") String interfaceName,
                         @RequestParam("method") String method,
                         @RequestParam("parameterTypesString") String parameterTypesString,
                         @RequestParam("parameter") String parameter) throws Exception{
        long startTime = System.currentTimeMillis();
        Object invoke = helloService.invoke(interfaceName, method, parameterTypesString, parameter);
        long endTime = System.currentTimeMillis();
        LOGGER.info("*********HelloController ...consumer-agent to provider-agent wait time: "+(endTime-startTime)+"ms");
        return invoke;
    }
}
