package com.wine.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WineEvent {

    private String eventType;
    private WineDTO wineDTO;

}
