package com.wine.microservice.service;

import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.dto.WineEvent;
import com.wine.microservice.exception.WineNotFoundException;
import com.wine.microservice.mapper.WineMapper;
import com.wine.microservice.model.Wine;
import com.wine.microservice.repository.WineRepository;
import com.wine.microservice.utils.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WineServiceEventsHandler {

    @Autowired
    private WineRepository wineRepository;

    @Autowired
    private WineMapper wineMapper;

//    @Value("${kafka.wine.topic.name}")
//    private String topicName;

//    @Value("${spring.kafka.consumer.group-id}")
//    private String groupId;


    @KafkaListener(topics = "${kafka.wine.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void processWineEvents(WineEvent wineEvent) {

        if (wineEvent.getEventType() == EventType.CREATE_WINE) {
            processCreateWineEvent(wineEvent);
        }
        if (wineEvent.getEventType().equals(EventType.UPDATE_WINE)) {
            processUpdateWineEvent(wineEvent);
        }
        if (wineEvent.getEventType().equals(EventType.DELETE_WINE)) {
            processDeleteWineEvent(wineEvent);
        }
        if (wineEvent.getEventType().equals(EventType.ADD_LINK_WINE)) {
            processAddLinkToWineEvent(wineEvent);
        }
    }

    public void processCreateWineEvent(WineEvent wineEvent) {
        WineDTO wineDTO = wineEvent.getWineDTO();
        wineRepository.save(wineMapper.wineDTOtoWine(wineDTO));
    }

    public void processUpdateWineEvent(WineEvent wineEvent) {
        WineDTO wineDTO = wineEvent.getWineDTO();
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

    public void processDeleteWineEvent(WineEvent wineEvent) {
        WineDTO wineDTO = wineEvent.getWineDTO();
        Long idWineDTO = wineDTO.getId();
        Wine deletedWine = wineRepository.findById(idWineDTO).orElseThrow(() -> new WineNotFoundException("Vino non trovato con l'id: " + idWineDTO));
        wineRepository.delete(deletedWine);
    }

    public void processAddLinkToWineEvent(WineEvent wineEvent) {
        WineDTO wineDTO = wineEvent.getWineDTO();
        Long idWineDTO = wineDTO.getId();

        Wine wine = wineRepository.findById(idWineDTO).orElseThrow(() -> new WineNotFoundException("Vino non trovato con l'id: " + idWineDTO));
        String trimmedLink = wineEvent.getWineLink().trim();

        List<String> purchaseLinks = wine.getPurchaseLinks();
        purchaseLinks.add(trimmedLink);

        wineRepository.save(wine);
    }
}
