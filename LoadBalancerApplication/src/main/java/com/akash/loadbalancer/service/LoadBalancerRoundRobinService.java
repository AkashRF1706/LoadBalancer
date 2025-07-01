package com.akash.loadbalancer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.akash.loadbalancer.model.ServerInstance;
import com.akash.loadbalancer.repository.ServerRepository;

@Service
public class LoadBalancerRoundRobinService {

	private final ServerRepository serverRepository;
	private final Map<String, Integer> requestCounter = new HashMap<>();
	private int serverIndex = 0;
	private static final int MAX_REQUESTS_PER_SERVER = 5;

	public LoadBalancerRoundRobinService(ServerRepository serverRepository) {
		this.serverRepository = serverRepository;
	}

	public synchronized String getServer(String key) {
		List<ServerInstance> allServers = serverRepository.findAll();
		List<ServerInstance> liveServers = new ArrayList<>();

		for (ServerInstance server : allServers) {
			if (server.isAlive()) {
				liveServers.add(server);
			}
		}

		if (liveServers.isEmpty()) {
			return null;
		}

		// Ensure serverIndex is in bounds after filtering
        if (serverIndex >= liveServers.size()) {
            serverIndex = 0;
        }

        // Skip dead server if it became unhealthy between checks
        ServerInstance currentServer = liveServers.get(serverIndex);
        if (!currentServer.isAlive()) {
            serverIndex = (serverIndex + 1) % liveServers.size();
            requestCounter.clear();
            currentServer = liveServers.get(serverIndex);
        }

        String currentKey = "server-" + serverIndex;
        int count = requestCounter.getOrDefault(currentKey, 0);

        if (count >= MAX_REQUESTS_PER_SERVER) {
            serverIndex = (serverIndex + 1) % liveServers.size();
            currentKey = "server-" + serverIndex;
            count = 0;
        }

        requestCounter.put(currentKey, count + 1);
        return liveServers.get(serverIndex).getUrl();
	}
}
