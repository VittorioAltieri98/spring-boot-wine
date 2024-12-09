package com.wine.ai_service.service;

import java.util.List;

public interface TerritApiService {

	public List<String> getRegions();
	public String getTypicalDishes(String region);
}
