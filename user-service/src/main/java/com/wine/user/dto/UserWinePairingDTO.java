package com.wine.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserWinePairingDTO {

    private Long id;
    private String wineName;
    private String wineType;
    private String region;
    private String denomination;
    private String wineDescription;
    private String serviceTemperature;
    private List<String> foodPairings;

    private Map<String, String> foodsNameAndDescriptionOfWhyThePairingIsRecommended;

}
