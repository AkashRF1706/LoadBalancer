package com.akash.loadbalancer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.akash.loadbalancer.model.ServerInstance;

public interface ServerRepository extends JpaRepository<ServerInstance, String> {

	List<ServerInstance> findByIsAliveTrue();
}
