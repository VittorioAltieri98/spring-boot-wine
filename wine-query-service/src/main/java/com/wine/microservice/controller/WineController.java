package com.wine.microservice.controller;


import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.exception.WineNotFoundException;
import com.wine.microservice.service.WineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wine")
public class WineController {

    @Autowired
    private WineService wineService;


    @GetMapping("/{id}")
    public ResponseEntity<WineDTO> getWineById(@PathVariable Long id) throws WineNotFoundException {
        return new ResponseEntity<>(wineService.getWineById(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public List<WineDTO> getAllWines(){
        return wineService.getAllWines();
    }

    @GetMapping("/search")
    public List<WineDTO> searchWines(@RequestParam(required = false) String wineName,
                                     @RequestParam(required = false) String wineType,
                                     @RequestParam(required = false) String grape,
                                     @RequestParam(required = false) String region,
                                     @RequestParam(required = false) String denomination,
                                     @RequestParam(required = false, defaultValue = "0") int year,
                                     @RequestParam(required = false, defaultValue = "0.0") double alcoholPercentage) {
        return wineService.searchWines(wineName, wineType, grape, region, denomination, year, alcoholPercentage);
    }

}
