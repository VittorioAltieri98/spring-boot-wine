package com.wine.microservice.service;



import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.exception.LinkAlreadyExistsException;
import com.wine.microservice.exception.WineAlreadyExistsException;
import com.wine.microservice.exception.WineNotFoundException;
import com.wine.microservice.model.Wine;

import java.util.List;

public interface WineService {

    public WineDTO createWine(WineDTO wineDTO) throws WineAlreadyExistsException;

    public WineDTO updateWine(Long id, WineDTO wineDTO) throws WineNotFoundException;

    public void deleteWine(Long id) throws WineNotFoundException;

    public WineDTO addLinkToWine(Long id, String link) throws WineNotFoundException, LinkAlreadyExistsException;

    public boolean isValidLink(String link);

    //For Testing
    public WineDTO getWineById(Long id);
}