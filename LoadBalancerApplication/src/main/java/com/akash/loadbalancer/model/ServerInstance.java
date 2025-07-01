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
	private int weight;

	public ServerInstance() {

	}

	public ServerInstance(String url, boolean isAlive, int weight) {
		super();
		this.url = url;
		this.isAlive = isAlive;
		this.weight = weight;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

}
