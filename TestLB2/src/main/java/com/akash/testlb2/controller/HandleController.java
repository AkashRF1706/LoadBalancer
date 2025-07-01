package com.akash.testlb2.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HandleController {
	
	@Value("${server.port}")
    private String serverPort;
	
	@GetMapping("/handle")
	public String handleRequest() {
		return "Request handled by backend on port: " + serverPort;
	}
}