package com.wine.microservice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wine.microservice.utils.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WineEvent {

    private EventType eventType;
    private WineDTO wineDTO;
    private String wineLink;

}
