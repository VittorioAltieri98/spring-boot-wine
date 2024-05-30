package com.wine.wine_query.service.impl;

import com.wine.wine_query.dto.WineDTO;
import com.wine.wine_query.dto.WineEvent;
import com.wine.wine_query.exception.WineNotFoundException;
import com.wine.wine_query.mapper.WineMapper;
import com.wine.wine_query.model.Wine;
import com.wine.wine_query.repository.WineRepository;
import com.wine.wine_query.service.WineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.wine.wine_query.specification.WineSpecification.hasMultipleFilters;


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


    @KafkaListener(topics = "wine-topic", groupId = "wineGroup")
    public void processWineEvents(WineEvent wineEvent) {

        WineDTO wineDTO = wineEvent.getWineDTO();
        if (wineEvent.getEventType().equals("CreateWine")){
            wineRepository.save(wineMapper.wineDTOtoWine(wineDTO));
        }
        if(wineEvent.getEventType().equals("UpdateWine")){
            Wine findedWine = wineRepository.findById(wineDTO.getId()).get();

            if(wineDTO.getWineName() != null){
                findedWine.setWineName(wineDTO.getWineName());
            }
            if(wineDTO.getWineType() != null){
                findedWine.setWineType(wineDTO.getWineType());
            }
            if(wineDTO.getGrape() != null){
                findedWine.setGrape(wineDTO.getGrape());
            }
            if(wineDTO.getRegion() != null){
                findedWine.setRegion(wineDTO.getRegion());
            }
            if(wineDTO.getDenomination() != null){
                findedWine.setDenomination(wineDTO.getDenomination());
            }

            findedWine.setYear(wineDTO.getYear());

            findedWine.setAlcoholPercentage(wineDTO.getAlcoholPercentage());

            if(wineDTO.getWineDescription() != null){
                findedWine.setWineDescription(wineDTO.getWineDescription());
            }
            if(wineDTO.getPurchaseLinks() != null){
                findedWine.setPurchaseLinks(wineDTO.getPurchaseLinks());
            }

            wineRepository.save(findedWine);
        }
    }


}
