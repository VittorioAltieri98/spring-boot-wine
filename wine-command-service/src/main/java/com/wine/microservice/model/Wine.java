package com.wine.microservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String wineName;
    private String wineType;
    private String grape;
    private String region;
    private String denomination; //es. DOC, DOCG, IGP
    private int year;
    private double alcoholPercentage;
    private String wineDescription;

    private List<String> purchaseLinks = new ArrayList<>();

}

