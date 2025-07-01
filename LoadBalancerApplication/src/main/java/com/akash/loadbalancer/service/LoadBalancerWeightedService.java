package com.akash.loadbalancer.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.*;

import com.akash.loadbalancer.model.ServerInstance;
import com.akash.loadbalancer.repository.ServerRepository;


@Service
public class LoadBalancerWeightedService {
	
	private final ServerRepository serverRepository;
	private List<ServerInstance> weightedServers = new ArrayList<>();
	private int currentIndex = 0;
	
	
	public LoadBalancerWeightedService(ServerRepository serverRepository) {
		this.serverRepository = serverRepository;
		buildWeightedServerList();
		
	}

	@Scheduled(fixedRate = 10000)
	@CacheEvict(value = "aliveServers", allEntries = true)
    public void refreshServerList() {
        buildWeightedServerList();
    }

	 private synchronized void buildWeightedServerList() {
	        List<ServerInstance> allServers = getAliveServers();
	        weightedServers.clear();

	        for (ServerInstance server : allServers) {
	            for (int i = 0; i < server.getWeight(); i++) {
	                weightedServers.add(server);
	            }
	        }
	        currentIndex = 0;
	    }

    @Cacheable("aliveServers")
    public List<ServerInstance> getAliveServers() {
        return serverRepository.findByIsAliveTrue();
    }
    
	public synchronized String getServer() {
		if (weightedServers.isEmpty()) {
            return null;
        }

        if (currentIndex >= weightedServers.size()) {
            currentIndex = 0;
        }

        ServerInstance selected = weightedServers.get(currentIndex);
        currentIndex++;
        return selected.getUrl();
	}

}
