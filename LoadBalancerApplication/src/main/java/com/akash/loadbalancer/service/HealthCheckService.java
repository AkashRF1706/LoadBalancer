package com.akash.loadbalancer.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.akash.loadbalancer.repository.ServerRepository;
import com.akash.loadbalancer.model.ServerInstance;

@Service
public class HealthCheckService {
	private final ServerRepository serverRepository;

	public HealthCheckService(ServerRepository serverRepository) {
		this.serverRepository = serverRepository;
	}

	@Scheduled(fixedRate = 5000)
	public void checkServers() {
		List<ServerInstance> servers = serverRepository.findAll();

		for (ServerInstance server : servers) {
            boolean previouslyAlive = server.isAlive();
            boolean currentlyAlive = false;
            try {
                URL url = new URL(server.getUrl() + "/handle");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(1000);
                conn.connect();
                currentlyAlive = conn.getResponseCode() == 200;
            } catch (IOException e) {
                currentlyAlive = false;
            }

            if (previouslyAlive != currentlyAlive) {
                server.setAlive(currentlyAlive);
                serverRepository.save(server);
            }
        }
	}
}
