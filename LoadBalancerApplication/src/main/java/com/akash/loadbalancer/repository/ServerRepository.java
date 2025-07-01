package com.akash.loadbalancer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akash.loadbalancer.model.ServerInstance;

public interface ServerRepository extends JpaRepository<ServerInstance, String> {

}
