package com.wine.ai_service.controller;

import com.wine.ai_service.client.WineServiceClient;
import com.wine.ai_service.dto.WineDTO;
import com.wine.ai_service.dto.WineInfo;
import com.wine.ai_service.dto.WinePairingDTO;
import com.wine.ai_service.service.WinePairingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private WinePairingService winePairingService;

    @Autowired
    private WineServiceClient wineServiceClient;

    @GetMapping("/{id}/pairing")
    public WinePairingDTO pairing(@PathVariable Long id) throws Exception {
        WineDTO wineDTO = wineServiceClient.getWineById(id);
        return winePairingService.generatePairing(wineDTO);
    }

    @GetMapping("/winePairings/{wineId}")
    public WinePairingDTO getWinePairingByWineId (@PathVariable Long wineId)  throws Exception{
        return winePairingService.getWinePairingByWineId(wineId);
    }

    @GetMapping("/generate")
    public String generate(@RequestParam(value = "message") String message){
        return winePairingService.generateWinePairingInformation(message);
    }

    @GetMapping("/winePairing/{id}")
    public WinePairingDTO getWinePairingById(@PathVariable Long id) throws Exception {
        return winePairingService.getWinePairingById(id);
    }

    @GetMapping("/generate/with-filter")
    public String generateWineInfoWithFilters(@RequestParam(value = "wineType") String wineType,
                                              @RequestParam(value = "region") String region) {
        return winePairingService.getWineInfoBasedOnFilters(wineType, region);
    }

    @GetMapping("/generateInfo/with-filter")
    public WineInfo obtainWineInfoWithFilters(@RequestParam(value = "wineType") String wineType,
                                              @RequestParam(value = "region") String region) {
        return winePairingService.obtainWineInfoBasedOnFilters(wineType, region);
    }

}
