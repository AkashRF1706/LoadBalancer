package com.akash.loadbalancer.service;

import com.akash.loadbalancer.model.ServerInstance;
import com.akash.loadbalancer.repository.ServerRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LoadBalancerStickyService {

    private final ServerRepository serverRepository;
    private final Map<String, String> clientMap = new HashMap<>();
    private int index = 0;

    public LoadBalancerStickyService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    public synchronized String getServer(String clientId) {
        List<ServerInstance> servers = serverRepository.findAll();
        List<ServerInstance> aliveServers = new ArrayList<>();

        for (ServerInstance server : servers) {
            if (server.isAlive()) {
                aliveServers.add(server);
            }
        }

        if (aliveServers.isEmpty()) {
            return null;
        }

        // if clientId is already mapped and server is alive, return it
        if (clientMap.containsKey(clientId)) {
            String assignedUrl = clientMap.get(clientId);
            for (ServerInstance server : aliveServers) {
                if (server.getUrl().equals(assignedUrl)) {
                    return assignedUrl;
                }
            }
        }

        // assign a new server (round robin)
        if (index >= aliveServers.size()) {
            index = 0;
        }

        String selectedUrl = aliveServers.get(index).getUrl();
        clientMap.put(clientId, selectedUrl);
        index++;

        return selectedUrl;
    }
}