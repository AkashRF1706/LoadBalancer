package com.akash.loadbalancer.controller;

import com.akash.loadbalancer.model.ServerInstance;
import com.akash.loadbalancer.repository.ServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/servers")
public class ServerRegistrationController {

    @Autowired
    private ServerRepository serverRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerServer(@RequestBody ServerInstance newServer) {
        Optional<ServerInstance> existing = serverRepository.findById(newServer.getUrl());
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("Server already exists with URL: " + newServer.getUrl());
        }
        serverRepository.save(newServer);
        return ResponseEntity.ok("Server registered: " + newServer.getUrl());
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> deregisterServer(@RequestBody ServerInstance toRemove) {
        Optional<ServerInstance> existing = serverRepository.findById(toRemove.getUrl());
        if (existing.isEmpty()) {
            return ResponseEntity.badRequest().body("Server not found: " + toRemove.getUrl());
        }
        serverRepository.deleteById(toRemove.getUrl());
        return ResponseEntity.ok("Server removed: " + toRemove.getUrl());
    }
}
