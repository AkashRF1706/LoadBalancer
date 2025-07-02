package com.akash.loadbalancer.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.akash.loadbalancer.model.ServerInstance;
import com.akash.loadbalancer.repository.ServerRepository;

@Service
public class LoadBalancerRoundRobinService {

	private final ServerRepository serverRepository;
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    private final AtomicInteger requestCounter = new AtomicInteger(0);
    private final int MAX_REQUESTS_PER_SERVER = 5;

    @Autowired
    private CacheManager cacheManager;

    public LoadBalancerRoundRobinService(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @Cacheable("aliveServers")
    public List<ServerInstance> getAliveServers() {
        return serverRepository.findByIsAliveTrue();
    }

    public synchronized String getServer() {
        List<ServerInstance> servers = getAliveServers();
        if (servers == null || servers.isEmpty()) {
            return null;
        }
        
        // Adjust currentIndex if it's out of bounds
        if (currentIndex.get() >= servers.size()) {
            currentIndex.set(0);
            requestCounter.set(0);
        }

        int index = currentIndex.get();
        int counter = requestCounter.incrementAndGet();

        if (counter > MAX_REQUESTS_PER_SERVER) {
            index = (index + 1) % servers.size();
            currentIndex.set(index);
            requestCounter.set(1);
        }

        String selectedUrl = servers.get(index).getUrl();
        try {
            new RestTemplate().getForObject(selectedUrl + "/handle", String.class);
            return selectedUrl;
        } catch (Exception e) {
            serverRepository.findById(selectedUrl).ifPresent(s -> {
                if (s.isAlive()) {
                    s.setAlive(false);
                    serverRepository.save(s);
                    cacheManager.getCache("aliveServers").clear();
                }
            });
            return getServer();
        }
    }
}
