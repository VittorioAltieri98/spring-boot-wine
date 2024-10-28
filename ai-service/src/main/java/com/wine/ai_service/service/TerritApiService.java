package com.wine.ai_service.service;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface TerritApiService {

	public List<String> getRegions();
	public List<String> getTypicalDishes(String region);
}
