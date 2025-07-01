package com.akash.loadbalancer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "servers")
public class ServerInstance {
	@Id
	private String url;
	private boolean isAlive;

	public ServerInstance() {
		
	}

	public ServerInstance(String url) {
		this.url = url;
		this.isAlive = true;
	}

	public String getUrl() {
		return url;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

}
