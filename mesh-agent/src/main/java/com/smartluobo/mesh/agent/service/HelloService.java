package com.smartluobo.mesh.agent.service;

public interface HelloService {
    Object invoke(String interfaceName,String method,String parameterTypesString,String parameter);
}
