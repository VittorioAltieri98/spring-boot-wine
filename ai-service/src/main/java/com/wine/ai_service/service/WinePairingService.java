package com.wine.ai_service.service;


import com.wine.ai_service.dto.UserWinePairingDTO;
import com.wine.ai_service.dto.WineDTO;
import com.wine.ai_service.dto.WineInfo;
import com.wine.ai_service.dto.WinePairingDTO;
import com.wine.ai_service.exception.UserWinePairingAlreadyExistsException;
import com.wine.ai_service.exception.WinePairingNotFoundException;
import com.wine.ai_service.model.UserWinePairing;
import com.wine.ai_service.model.WinePairing;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public interface WinePairingService {

    public WinePairingDTO generateWinePairing(Long id);
    public WinePairingDTO generatePairing(WineDTO wineDTO);
    public String generatePairingsByFoodMessage(String message);
    public String generateInfoWithFilters(String wineType, String region);
    public WineInfo generateWineInfoWithFilters(String wineType, String region);
    public WinePairingDTO getWinePairingById(Long id) throws WinePairingNotFoundException;
    public WinePairingDTO getWinePairingByWineId(Long wineId) throws WinePairingNotFoundException;
    public UserWinePairingDTO createUserWinePairing(String wineType, String region, Jwt jwt)throws UserWinePairingAlreadyExistsException;
    public List<UserWinePairingDTO> getUserWinePairings(Jwt jwt);
    public void deleteUserWinePairing(Long id, String userId);
    public void deleteAllUserWinePairing(String userid);
}
