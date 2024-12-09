package com.wine.ai_service.controller;

import com.wine.ai_service.client.WineServiceClient;
import com.wine.ai_service.dto.*;
import com.wine.ai_service.exception.UserWinePairingAlreadyExistsException;
import com.wine.ai_service.exception.UserWinePairingNotFoundException;
import com.wine.ai_service.exception.WinePairingNotFoundException;
import com.wine.ai_service.model.UserWinePairing;
import com.wine.ai_service.model.WinePairing;
import com.wine.ai_service.repository.UserWinePairingRepository;
import com.wine.ai_service.service.TerritApiService;
import com.wine.ai_service.service.WinePairingService;
import com.wine.ai_service.service.impl.TerritApiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/ai")
public class AiController {


    private final WinePairingService winePairingService;
    private final TerritApiService territApiService;

    public AiController(
            WinePairingService winePairingService,
            TerritApiService territApiService
    ) {
        this.winePairingService = winePairingService;
        this.territApiService = territApiService;
    }


    @PreAuthorize("hasAnyRole('admin')")
    @GetMapping("/{id}/pairing")
    public ResponseEntity<WinePairingDTO> pairing(@PathVariable Long id) {
        return new ResponseEntity<>(winePairingService.generateWinePairing(id), OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/winePairing/by-wine-id/{wineId}")
    public ResponseEntity<WinePairingDTO> getWinePairingByWineId(@PathVariable Long wineId) throws WinePairingNotFoundException {
        return new ResponseEntity<>(winePairingService.getWinePairingByWineId(wineId), OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/generate/food-message")
    public ResponseEntity<String> generatePairingsByFoodMessage(@RequestParam(value = "message") String message){
        return new ResponseEntity<>(winePairingService.generatePairingsByFoodMessage(message), OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/winePairing/by-id/{id}")
    public ResponseEntity<WinePairingDTO> getWinePairingById(@PathVariable Long id) throws WinePairingNotFoundException {
        return new ResponseEntity<>(winePairingService.getWinePairingById(id), OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/generate/info/with-filter")
    public ResponseEntity<String> generateInfoWithFilters(@RequestParam(value = "wineType") String wineType,
                                              @RequestParam(value = "region") String region) {
        return new ResponseEntity<>(winePairingService.generateInfoWithFilters(wineType, region), OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/generate/wine-info/with-filter")
    public ResponseEntity<WineInfo> generateWineInfoWithFilters(@RequestParam(value = "wineType") String wineType,
                                              @RequestParam(value = "region") String region) {
        return new ResponseEntity<>(winePairingService.generateWineInfoWithFilters(wineType, region), OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/user/create/pairing")
    public ResponseEntity<UserWinePairingDTO> createUserWinePairing(@RequestParam(value = "wineType") String wineType,
                                                                    @RequestParam(value = "region") String region,
                                                                    @AuthenticationPrincipal Jwt jwt) throws UserWinePairingAlreadyExistsException {
        return new ResponseEntity<>(winePairingService.createUserWinePairing(wineType, region, jwt), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/user/my-pairings")
    public ResponseEntity<List<UserWinePairingDTO>> getUserWinePairings(@AuthenticationPrincipal Jwt jwt){
        return new ResponseEntity<>(winePairingService.getUserWinePairings(jwt), OK);
    }


    @PreAuthorize("hasAnyRole('user', 'admin')")
    @DeleteMapping("/user/pairing/{id}/delete")
    public ResponseEntity<Void> deleteUserWinePairing(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) throws UserWinePairingNotFoundException {
        winePairingService.deleteUserWinePairing(id, jwt.getSubject());
        return new ResponseEntity<>(OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @DeleteMapping("/user/{userId}/pairings/delete")
    public ResponseEntity<Void> deleteAllUserWinePairing(@PathVariable String userId){
        winePairingService.deleteAllUserWinePairing(userId);
        return new ResponseEntity<>(OK);
    }


    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/pairings/popular")
    public ResponseEntity<List<PopularPairing>> getPopularPairings() {
        return new ResponseEntity<>(winePairingService.getTopPopularPairings(), OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/regions")
    public ResponseEntity<List<String>> getRegions() {
        return new ResponseEntity<>(territApiService.getRegions(), OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/typical-dishes/{region}")
    public ResponseEntity<String> getTypicalDishes(@PathVariable String region) {
        return new ResponseEntity<>(territApiService.getTypicalDishes(region), OK);
    }

}
