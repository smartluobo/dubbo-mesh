package com.smartluobo.mesh.agent.protocol;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

public class AgentProtocolRequest implements Serializable{

    private static AtomicLong atomicLong = new AtomicLong();
    private long requestId;

    private String path;
    private String method;
    private String paramTypes;
    private String parameter;

    public AgentProtocolRequest(){
        this.requestId = atomicLong.incrementAndGet();
    }

    public AgentProtocolRequest path(String path){
        this.path=path;
        return this;
    }

    public AgentProtocolRequest method(String method){
        this.method=method;
        return this;
    }

    public AgentProtocolRequest paramTypes(String paramTypes){
        this.paramTypes=paramTypes;
        return this;
    }

    public AgentProtocolRequest parameter(String parameter){
        this.parameter=parameter;
        return this;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public String getParamTypes() {
        return paramTypes;
    }

    public String getParameter() {
        return parameter;
    }

    public long getRequestId() {

        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }
}
