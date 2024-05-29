package com.wine.wine_query.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WineDTO {

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

}
