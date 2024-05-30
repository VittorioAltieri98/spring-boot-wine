package com.wine.wine_command.dto;

import com.wine.wine_command.model.Wine;
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
