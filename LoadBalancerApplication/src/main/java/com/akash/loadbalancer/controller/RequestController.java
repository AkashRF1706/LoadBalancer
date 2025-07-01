package com.akash.loadbalancer.controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.akash.loadbalancer.service.LoadBalancerService;

@RestController
public class RequestController {
	@Autowired
	private LoadBalancerService loadBalancerService;
	
	@GetMapping("/redirect")
	public ResponseEntity<String> redirectRequest(@RequestParam String clientId) throws IOException {
		String server = loadBalancerService.getServer(clientId);
		if(server == null) {
			return ResponseEntity.status(503).body("No available servers!");
		}
		
		URL url = new URL(server + "/handle");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		
		return ResponseEntity.status(connection.getResponseCode()).body("Routed to: " + server);
	}

}
