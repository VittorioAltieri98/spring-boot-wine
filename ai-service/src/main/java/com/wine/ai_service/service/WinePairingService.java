package com.wine.ai_service.service;


import com.wine.ai_service.dto.WineDTO;
import com.wine.ai_service.dto.WineInfo;
import com.wine.ai_service.dto.WinePairingDTO;
import com.wine.ai_service.exception.WinePairingNotFoundException;
import com.wine.ai_service.model.WinePairing;

public interface WinePairingService {

    public WinePairingDTO generateWinePairing(Long id);
    public WinePairingDTO generatePairing(WineDTO wineDTO);
    public String generatePairingsByFoodMessage(String message);
    public String generateInfoWithFilters(String wineType, String region);
    public WineInfo generateWineInfoWithFilters(String wineType, String region);
    public WinePairingDTO getWinePairingById(Long id) throws WinePairingNotFoundException;
    public WinePairingDTO getWinePairingByWineId(Long wineId) throws WinePairingNotFoundException;
    public void saveWinePair(WinePairing winePairing);
}
