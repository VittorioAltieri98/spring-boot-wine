package com.wine.microservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WineResponseDTO {

    private Long id;

    private String wineName;

    private String wineType;

    private String grape;

    private String region;

    private String denomination;

    private int year;

    private double alcoholPercentage;

    private String wineDescription;

    private List<String> purchaseLinks = new ArrayList<>();

    private List<String> foodPairings;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> foodsNameAndDescriptionOfWhyThePairingIsRecommended;
}
