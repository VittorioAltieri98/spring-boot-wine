package com.wine.microservice.service.impl;

import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.dto.WineEvent;
import com.wine.microservice.exception.LinkAlreadyExistsException;
import com.wine.microservice.exception.WineNotFoundException;
import com.wine.microservice.mapper.WineMapper;
import com.wine.microservice.model.Wine;
import com.wine.microservice.repository.WineRepository;
import com.wine.microservice.service.WineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.wine.microservice.specification.WineSpecification.hasMultipleFilters;


@Service
public class WineServiceImpl implements WineService {

    @Autowired
    private WineRepository wineRepository;

    @Autowired
    private WineMapper wineMapper;


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

    //Definire una classe WineEventsServiceHandler
    //Implementare il metodo processWineEvents al suo interno

    @KafkaListener(topics = "wine-topic", groupId = "wineGroup")
    public void processWineEvents(WineEvent wineEvent) {

        WineDTO wineDTO = wineEvent.getWineDTO();

        if(wineEvent.getEventType().equals("DeleteWine")) {
            Long idWineDTO = wineDTO.getId();
            Wine deletedWine = wineRepository.findById(idWineDTO).orElseThrow(() -> new WineNotFoundException("Vino non trovato con l'id: " + idWineDTO));
            wineRepository.delete(deletedWine);
        }
        if(wineEvent.getEventType().equals("AddLinkWine")) {
            Long idWineDTO = wineDTO.getId();
            Wine wine = wineRepository.findById(idWineDTO).orElseThrow(() -> new WineNotFoundException("Vino non trovato con l'id: " + idWineDTO));
            String trimmedLink = wineEvent.getWineLink().trim();

            List<String> purchaseLinks = wine.getPurchaseLinks();

            purchaseLinks.add(trimmedLink);
            wineRepository.save(wine);
        }
        if (wineEvent.getEventType().equals("CreateWine")){
            wineRepository.save(wineMapper.wineDTOtoWine(wineDTO));
        }
        if(wineEvent.getEventType().equals("UpdateWine")){
            Long idWineDTO = wineDTO.getId();
            Wine findedWine = wineRepository.findById(idWineDTO).orElseThrow(() -> new WineNotFoundException("Vino non trovato con l'id: " + idWineDTO));

            findedWine.setWineName(wineDTO.getWineName());
            findedWine.setWineType(wineDTO.getWineType());
            findedWine.setGrape(wineDTO.getGrape());
            findedWine.setRegion(wineDTO.getRegion());
            findedWine.setDenomination(wineDTO.getDenomination());
            findedWine.setYear(wineDTO.getYear());
            findedWine.setAlcoholPercentage(wineDTO.getAlcoholPercentage());
            findedWine.setWineDescription(wineDTO.getWineDescription());
            findedWine.setPurchaseLinks(wineDTO.getPurchaseLinks());

            wineRepository.save(findedWine);
        }
    }
}
