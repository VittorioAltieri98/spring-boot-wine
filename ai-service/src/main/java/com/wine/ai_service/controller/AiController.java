package com.wine.ai_service.controller;

import com.wine.ai_service.client.WineServiceClient;
import com.wine.ai_service.dto.WineDTO;
import com.wine.ai_service.dto.WineInfo;
import com.wine.ai_service.dto.WinePairingDTO;
import com.wine.ai_service.exception.WinePairingNotFoundException;
import com.wine.ai_service.service.WinePairingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private WinePairingService winePairingService;


    @PreAuthorize("hasAnyRole('admin')")
    @GetMapping("/{id}/pairing")
    public ResponseEntity<WinePairingDTO> pairing(@PathVariable Long id) {
        return new ResponseEntity<>(winePairingService.generateWinePairing(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/winePairing/by-wine-id/{wineId}")
    public ResponseEntity<WinePairingDTO> getWinePairingByWineId(@PathVariable Long wineId) throws WinePairingNotFoundException {
        return new ResponseEntity<>(winePairingService.getWinePairingByWineId(wineId), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/generate/food-message")
    public ResponseEntity<String> generatePairingsByFoodMessage(@RequestParam(value = "message") String message){
        return new ResponseEntity<>(winePairingService.generatePairingsByFoodMessage(message), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/winePairing/by-id/{id}")
    public ResponseEntity<WinePairingDTO> getWinePairingById(@PathVariable Long id) throws WinePairingNotFoundException {
        return new ResponseEntity<>(winePairingService.getWinePairingById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/generate/info/with-filter")
    public ResponseEntity<String> generateInfoWithFilters(@RequestParam(value = "wineType") String wineType,
                                              @RequestParam(value = "region") String region) {
        return new ResponseEntity<>(winePairingService.generateInfoWithFilters(wineType, region), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/generate/wine-info/with-filter")
    public ResponseEntity<WineInfo> generateWineInfoWithFilters(@RequestParam(value = "wineType") String wineType,
                                              @RequestParam(value = "region") String region) {
        return new ResponseEntity<>(winePairingService.generateWineInfoWithFilters(wineType, region), HttpStatus.OK);
    }

}
