package com.wine.microservice.controller;



import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.exception.LinkAlreadyExistsException;
import com.wine.microservice.exception.WineAlreadyExistsException;
import com.wine.microservice.exception.WineNotFoundException;
import com.wine.microservice.service.WineService;
import jakarta.validation.Valid;
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


    @PostMapping("/create")
    public ResponseEntity<WineDTO> createWine(@RequestBody @Valid WineDTO wineDTO) throws WineAlreadyExistsException {
        return new ResponseEntity<>(wineService.createWine(wineDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<WineDTO> updateWine(@PathVariable Long id, @RequestBody @Valid WineDTO wineDTO) throws WineNotFoundException {
        return new ResponseEntity<>(wineService.updateWine(id, wineDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteWine(@PathVariable Long id) throws WineNotFoundException {
        wineService.deleteWine(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{id}/addLink")
    public ResponseEntity<WineDTO> addLinkToWine(@PathVariable Long id, @RequestBody String link) throws WineNotFoundException, LinkAlreadyExistsException {
        return new ResponseEntity<>(wineService.addLinkToWine(id, link), HttpStatus.CREATED);
    }

    //For Testing

    @GetMapping("/{id}")
    public ResponseEntity<WineDTO> getWineById(@PathVariable Long id) throws WineNotFoundException {
        return new ResponseEntity<>(wineService.getWineById(id), HttpStatus.OK);
    }
}
