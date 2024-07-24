package com.wine.microservice.controller;


import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.dto.WineResponseDTO;
import com.wine.microservice.exception.WineNotFoundException;
import com.wine.microservice.service.WineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wine")
public class WineController {

    @Autowired
    private WineService wineService;


    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/{id}")
    public ResponseEntity<WineDTO> getWineById(@PathVariable Long id) throws WineNotFoundException {
        return new ResponseEntity<>(wineService.getWineById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/all")
    public ResponseEntity<List<WineDTO>> getAllWines(){
        return new ResponseEntity<>(wineService.getAllWines(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/search")
    public ResponseEntity<List<WineDTO>> searchWines(@RequestParam(required = false) String wineName,
                                     @RequestParam(required = false) String wineType,
                                     @RequestParam(required = false) String grape,
                                     @RequestParam(required = false) String region,
                                     @RequestParam(required = false) String denomination,
                                     @RequestParam(required = false, defaultValue = "0") int year,
                                     @RequestParam(required = false, defaultValue = "0.0") double alcoholPercentage) {
        List<WineDTO> response = wineService.searchWines(wineName, wineType, grape, region, denomination, year, alcoholPercentage);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('user', 'admin')")
    @GetMapping("/{id}/pairings")
    public ResponseEntity<WineResponseDTO> getWineDetailsWithPairings(@PathVariable Long id){
        return new ResponseEntity<>(wineService.getWineDetailsWithPairings(id), HttpStatus.OK);
    }

}
