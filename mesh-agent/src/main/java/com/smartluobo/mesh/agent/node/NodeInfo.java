package com.smartluobo.mesh.agent.node;

public class NodeInfo {
    private String host;
    private String port;
    private Integer weight;

    public NodeInfo() {
    }

    public NodeInfo(String host, String port,Integer weight) {
        this.host = host;
        this.port = port;
        this.weight = weight;
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

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "NodeInfo{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", weight='" + weight + '\'' +
                '}';
    }
}
