package com.smartluobo.mesh.agent.node;

public class NodeInfo {
    private String host;
    private String port;

    public NodeInfo() {
    }

    public NodeInfo(String host, String port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "NodeInfo{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
