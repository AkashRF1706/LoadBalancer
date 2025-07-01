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
import org.springframework.web.client.RestTemplate;

import com.akash.loadbalancer.service.LoadBalancerRoundRobinService;
import com.akash.loadbalancer.service.LoadBalancerStickyService;
import com.akash.loadbalancer.service.LoadBalancerWeightedService;

@RestController
public class LoadBalanceController {
	@Autowired
	private LoadBalancerRoundRobinService loadBalancerRoundRobinService;
	@Autowired
	private LoadBalancerWeightedService loadBalancerWeightedService;
	@Autowired
	private LoadBalancerStickyService stickyService;
	@Autowired
	private RestTemplate restTemplate;

	
	@GetMapping("/redirect")
	public ResponseEntity<String> redirectRequest(@RequestParam String clientId,
												  @RequestParam(defaultValue = "roundrobin") String strategy) throws IOException {
		
		String targetUrl = null;

	    switch (strategy.toLowerCase()) {
	        case "roundrobin":
	            targetUrl = loadBalancerRoundRobinService.getServer();
	            break;
	        case "weighted":
	            targetUrl = loadBalancerWeightedService.getServer();
	            break;
	        case "sticky":
	            targetUrl = stickyService.getServer(clientId);
	            break;
	        default:
	            return ResponseEntity.badRequest().body("Invalid strategy provided.");
	    }
		
	    if (targetUrl == null) {
	        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("No servers available");
	    }

	    try {
	        String response = restTemplate.getForObject(targetUrl + "/handle", String.class);
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error forwarding to backend: " + e.getMessage());
	    }
	}

}
