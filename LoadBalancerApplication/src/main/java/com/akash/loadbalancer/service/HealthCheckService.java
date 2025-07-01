package com.akash.loadbalancer.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.akash.loadbalancer.repository.ServerRepository;
import com.akash.loadbalancer.model.ServerInstance;

@Service
public class HealthCheckService {
	@Autowired
    private ServerRepository serverRepository;

    @Autowired
    private CacheManager cacheManager;

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate = 5000)
    public void performHealthCheck() {
        List<ServerInstance> servers = serverRepository.findAll();
        boolean statusChanged = false;

        for (ServerInstance server : servers) {
            try {
                restTemplate.getForObject(server.getUrl() + "/handle", String.class);
                if (!server.isAlive()) {
                    server.setAlive(true);
                    statusChanged = true;
                }
            } catch (Exception e) {
                if (server.isAlive()) {
                    server.setAlive(false);
                    statusChanged = true;
                }
            }
        }

        if (statusChanged) {
            serverRepository.saveAll(servers);
            cacheManager.getCache("aliveServers").clear();
        }
    }
}
