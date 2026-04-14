package com.budgetwise.gateway;

import java.util.List;

public class IpHashLoadBalancer {
    private List<String> servers = List.of("ServerA", "ServerB","ServerC");

    public String route(String ip){
        int ipHashCode = ip.hashCode();
        int index = Math.abs(ip.hashCode() % servers.size());
        return servers.get(index);
    }

    public static void main(String[] args) {
        IpHashLoadBalancer ipHashLoadBalancer = new IpHashLoadBalancer();
        System.out.println(ipHashLoadBalancer.route("127.0.0.1"));
        System.out.println(ipHashLoadBalancer.route("127.0.0.1"));
        System.out.println(ipHashLoadBalancer.route("127.0.0.1"));
    }
}
