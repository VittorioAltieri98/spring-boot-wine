package com.wine.wine_command.service;



import com.wine.wine_command.dto.WineDTO;
import com.wine.wine_command.exception.LinkAlreadyExistsException;
import com.wine.wine_command.exception.WineAlreadyExistsException;
import com.wine.wine_command.exception.WineNotFoundException;

import java.util.List;

public interface WineService {

    public WineDTO createWine(WineDTO wineDTO) throws WineAlreadyExistsException;

    public WineDTO updateWine(Long id, WineDTO wineDTO) throws WineNotFoundException;

    public void deleteWine(Long id) throws WineNotFoundException;

    public WineDTO addLinkToWine(Long id, String link) throws WineNotFoundException, LinkAlreadyExistsException;

    public boolean isValidLink(String link);
}