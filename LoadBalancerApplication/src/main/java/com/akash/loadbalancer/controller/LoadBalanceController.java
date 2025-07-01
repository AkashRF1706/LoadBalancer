package com.akash.loadbalancer.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akash.loadbalancer.service.LoadBalancerRoundRobinService;
import com.akash.loadbalancer.service.LoadBalancerWeightedService;

@RestController
public class LoadBalanceController {
	@Autowired
	private LoadBalancerRoundRobinService loadBalancerRoundRobinService;
	@Autowired
	private LoadBalancerWeightedService loadBalancerWeightedService;
	
	@GetMapping("/redirect")
	public ResponseEntity<String> redirectRequest(@RequestParam String clientId,
												  @RequestParam(defaultValue = "roundrobin") String strategy) throws IOException {
		
		String serverUrl;
		if("weighted".equalsIgnoreCase(strategy)) {
			serverUrl = loadBalancerWeightedService.getServer(clientId);
		} else {
			serverUrl = loadBalancerRoundRobinService.getServer(clientId);
		}
		
		if(serverUrl == null) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("No Available servers");
		}
		
		try {
			URL url = new URL(serverUrl + "/handle");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(1000);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;
			
			while((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
			
			return ResponseEntity.ok(response.toString());
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server call failed");
		}
	}

}
