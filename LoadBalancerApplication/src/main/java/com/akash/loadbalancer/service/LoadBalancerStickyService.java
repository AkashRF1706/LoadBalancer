package com.akash.loadbalancer.service;

import com.akash.loadbalancer.model.ServerInstance;
import com.akash.loadbalancer.repository.ServerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class LoadBalancerStickyService {

	private final ServerRepository serverRepository;
    private final Map<String, String> clientMap = new HashMap<>();

    @Autowired
    private CacheManager cacheManager;

    public LoadBalancerStickyService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @Cacheable("aliveServers")
    public List<ServerInstance> getAliveServers() {
        return serverRepository.findByIsAliveTrue();
    }

    public synchronized String getServer(String clientId) {
        if (clientMap.containsKey(clientId)) {
            String mappedServer = clientMap.get(clientId);
            try {
                new RestTemplate().getForObject(mappedServer + "/handle", String.class);
                return mappedServer;
            } catch (Exception e) {
                serverRepository.findById(mappedServer).ifPresent(s -> {
                    if (s.isAlive()) {
                        s.setAlive(false);
                        serverRepository.save(s);
                        cacheManager.getCache("aliveServers").clear();
                    }
                });
                clientMap.remove(clientId);
            }
        }

        List<ServerInstance> servers = getAliveServers();
        if (servers.isEmpty()) {
            return null;
        }

        String selected = servers.get(clientId.hashCode() % servers.size()).getUrl();
        clientMap.put(clientId, selected);
        return selected;
    }
}