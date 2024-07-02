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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wine-command")
public class WineController {

    @Autowired
    private WineService wineService;

    @PreAuthorize("hasAnyRole('admin')")
    @PostMapping("/create")
    public ResponseEntity<WineDTO> createWine(@RequestBody @Valid WineDTO wineDTO) throws WineAlreadyExistsException {
        return new ResponseEntity<>(wineService.createWine(wineDTO), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('admin')")
    @PutMapping("/{id}/update")
    public ResponseEntity<WineDTO> updateWine(@PathVariable Long id, @RequestBody @Valid WineDTO wineDTO) throws WineNotFoundException {
        return new ResponseEntity<>(wineService.updateWine(id, wineDTO), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('admin')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteWine(@PathVariable Long id) throws WineNotFoundException {
        wineService.deleteWine(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('admin')")
    @PostMapping("/{id}/addLink")
    public ResponseEntity<WineDTO> addLinkToWine(@PathVariable Long id, @RequestBody String link) throws WineNotFoundException, LinkAlreadyExistsException, IllegalArgumentException {
        return new ResponseEntity<>(wineService.addLinkToWine(id, link), HttpStatus.CREATED);
    }
}
