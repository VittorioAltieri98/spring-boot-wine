package com.wine.microservice.service.impl;


import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.dto.WineEvent;
import com.wine.microservice.exception.LinkAlreadyExistsException;
import com.wine.microservice.exception.WineAlreadyExistsException;
import com.wine.microservice.exception.WineNotFoundException;
import com.wine.microservice.mapper.WineMapper;
import com.wine.microservice.model.Wine;
import com.wine.microservice.repository.WineRepository;
import com.wine.microservice.service.WineService;
import com.wine.microservice.utils.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class WineServiceImpl implements WineService {

    @Autowired
    private WineRepository wineRepository;

    @Autowired
    private WineMapper wineMapper;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.wine.topic.name}")
    private String wineTopicName;

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

        WineEvent wineEvent = new WineEvent();
        wineEvent.setEventType(EventType.CREATE_WINE);
        wineEvent.setWineDTO(wineMapper.wineToWineDTO(savedWine));

        kafkaTemplate.send(wineTopicName, wineEvent);

        return wineMapper.wineToWineDTO(savedWine);
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

        WineEvent wineEvent = new WineEvent();
        wineEvent.setEventType(EventType.UPDATE_WINE);
        wineEvent.setWineDTO(wineMapper.wineToWineDTO(updatedWine));

        kafkaTemplate.send(wineTopicName, wineEvent);

        return wineMapper.wineToWineDTO(updatedWine);
    }

    public void deleteWine(Long id) throws WineNotFoundException {
        Wine deletedWine = wineRepository.findById(id).orElseThrow(() -> new WineNotFoundException("Vino non trovato con l'id: " + id));

        WineEvent wineEvent = new WineEvent();
        wineEvent.setEventType(EventType.DELETE_WINE);
        wineEvent.setWineDTO(wineMapper.wineToWineDTO(deletedWine));

        kafkaTemplate.send(wineTopicName, wineEvent);

        wineRepository.delete(deletedWine);
    }

    public WineDTO addLinkToWine(Long id, String link) throws WineNotFoundException, LinkAlreadyExistsException {
        Wine wine = wineRepository.findById(id).orElseThrow(() -> new WineNotFoundException("Vino non trovato con l'id:" + id));
        String trimmedLink = link.trim();

        if (!isValidLink(trimmedLink)) {
            throw new IllegalArgumentException("Invalid link format: " + trimmedLink);
        }
        List<String> purchaseLinks = wine.getPurchaseLinks();

        if (purchaseLinks == null) {
            purchaseLinks = new ArrayList<>();
            wine.setPurchaseLinks(purchaseLinks);
        }

        if (purchaseLinks.contains(trimmedLink)) {
            throw new LinkAlreadyExistsException("Link already exists: " + trimmedLink);
        }
        purchaseLinks.add(trimmedLink);
        Wine savedWined = wineRepository.save(wine);

        WineEvent wineEvent = new WineEvent();
        wineEvent.setEventType(EventType.ADD_LINK_WINE);
        wineEvent.setWineDTO(wineMapper.wineToWineDTO(savedWined));
        wineEvent.setWineLink(link);

        kafkaTemplate.send(wineTopicName, wineEvent);

        return wineMapper.wineToWineDTO(savedWined);
    }

    public boolean isValidLink(String link) {
        return link.startsWith("https://") || link.startsWith("http://");
    }
}
