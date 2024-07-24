package com.wine.ai_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PopularPairing {

    private String wineType;
    private String region;
    private Long requestCount;
}
