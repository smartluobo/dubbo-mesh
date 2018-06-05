package com.smartluobo.mesh.agent.loadbalance;

import com.smartluobo.mesh.agent.node.NodeInfo;

import java.util.List;

public interface LoadBalance {
    NodeInfo doSelect();
}
