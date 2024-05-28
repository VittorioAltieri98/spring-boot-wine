package com.wine.wine_service.service.impl;


import com.wine.wine_service.dto.WineDTO;
import com.wine.wine_service.exception.LinkAlreadyExistsException;
import com.wine.wine_service.exception.WineAlreadyExistsException;
import com.wine.wine_service.exception.WineNotFoundException;
import com.wine.wine_service.mapper.WineMapper;
import com.wine.wine_service.model.Wine;
import com.wine.wine_service.repository.WineRepository;
import com.wine.wine_service.service.WineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.wine.wine_service.specification.WineSpecification.hasMultipleFilters;


@Service
public class WineServiceImpl implements WineService {

    @Autowired
    private WineRepository wineRepository;

    @Autowired
    private WineMapper wineMapper;

    public WineDTO createWine(WineDTO wineDTO) throws WineAlreadyExistsException {

        Optional<Wine> optionalWine = wineRepository.findByWineName(wineDTO.getWineName());
        if(optionalWine.isPresent()){
            throw new WineAlreadyExistsException("Il vino " + wineDTO.getWineName() + " è già esistente.");
        }

        Wine wine = Wine.builder()
                .wineName(wineDTO.getWineName())
                .wineType(wineDTO.getWineType())
                .grape(wineDTO.getGrape())
                .region(wineDTO.getRegion())
                .denomination(wineDTO.getDenomination())
                .year(wineDTO.getYear())
                .alcoholPercentage(wineDTO.getAlcoholPercentage())
                .wineDescription(wineDTO.getWineDescription())
                .build();

        Wine savedWine = wineRepository.save(wine);
        return wineMapper.wineToWineDTO(savedWine);
    }

    public WineDTO getWineById(Long id) throws WineNotFoundException {
        Optional<Wine> optional = wineRepository.findById(id);
        if (optional.isPresent()) {
            Wine wine = optional.get();
            return wineMapper.wineToWineDTO(wine);
        } else throw new WineNotFoundException("Vino non trovato con l'id: " + id);
    }

    public List<WineDTO> getAllWines() {
        List<Wine> allWines = wineRepository.findAll();
        List<WineDTO> allWineDTO = new ArrayList<>();
        for (Wine wine : allWines){
            WineDTO wineDTO = wineMapper.wineToWineDTO(wine);
            allWineDTO.add(wineDTO);
        }
        return allWineDTO;
    }

    public WineDTO updateWine(Long id, WineDTO wineDTO) throws WineNotFoundException {
        Wine findWine = wineRepository.findById(id).orElseThrow(() -> new WineNotFoundException("Vino non trovato con l'id: " + id));

        //TODO
        if(wineDTO.getWineName() != null){
            findWine.setWineName(wineDTO.getWineName());
        }
        if(wineDTO.getWineType() != null){
            findWine.setWineType(wineDTO.getWineType());
        }
        if(wineDTO.getGrape() != null){
            findWine.setGrape(wineDTO.getGrape());
        }
        if(wineDTO.getRegion() != null){
            findWine.setRegion(wineDTO.getRegion());
        }
        if(wineDTO.getDenomination() != null){
            findWine.setDenomination(wineDTO.getDenomination());
        }
        findWine.setYear(wineDTO.getYear());
        findWine.setAlcoholPercentage(wineDTO.getAlcoholPercentage());
        if(wineDTO.getWineDescription() != null){
            findWine.setWineDescription(wineDTO.getWineDescription());
        }
        if(wineDTO.getPurchaseLinks() != null){
            findWine.setPurchaseLinks(wineDTO.getPurchaseLinks());
        }

        Wine updatedWine = wineRepository.save(findWine);

        return wineMapper.wineToWineDTO(updatedWine);
    }

    public void deleteWine(Long id) throws WineNotFoundException {
        Wine deletedWine = wineRepository.findById(id).orElseThrow(() -> new WineNotFoundException("Vino non trovato con l'id: " + id));
        wineRepository.delete(deletedWine);
    }

    public List<WineDTO> searchWines(String wineName,
                                     String wineType,
                                     String grape,
                                     String region,
                                     String denomination,
                                     int year,
                                     double alcoholPercentage) {
        Specification<Wine> spec = hasMultipleFilters(wineName, wineType, grape, region, denomination, year, alcoholPercentage);
        List<Wine> filteredWines = wineRepository.findAll(spec);
        List<WineDTO> filteredWineDTO = new ArrayList<>();
        for (Wine wine : filteredWines){
            WineDTO wineDTO = wineMapper.wineToWineDTO(wine);
            filteredWineDTO.add(wineDTO);
        }
        return filteredWineDTO;
    }

    public WineDTO addLinkToWine(Long id, String link) throws WineNotFoundException, LinkAlreadyExistsException {
        Wine wine = wineRepository.findById(id).orElseThrow(() -> new WineNotFoundException("Vino non trovato con l'id:" + id));
        String trimmedLink = link.trim();

        if (!isValidLink(trimmedLink)) {
            throw new IllegalArgumentException("Invalid link format: " + trimmedLink);
        }

        List<String> purchaseLinks = wine.getPurchaseLinks();

        if (purchaseLinks.contains(trimmedLink)) {
            throw new LinkAlreadyExistsException("Link already exists: " + trimmedLink);
        }
        purchaseLinks.add(trimmedLink);
        Wine savedWined = wineRepository.save(wine);
        return wineMapper.wineToWineDTO(savedWined);
    }

    public boolean isValidLink(String link) {
        return link.startsWith("https://") || link.startsWith("http://");
    }
}
