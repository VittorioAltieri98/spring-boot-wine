package com.wine.wine_query.specification;

import com.wine.wine_query.model.Wine;
import org.springframework.data.jpa.domain.Specification;

public class WineSpecification {


    //I metodi ritornano una istanza di Specification di tipo Wine

    //Quel % ... % vuol dire qualsiasi sequenza di caratteri.
    //Esempio -> vino Brindisi, nella ricerca posso trovarlo anche scrivendo "indi"
    public static Specification<Wine> hasName(String wineName) {
        return (wine, cq, cb) -> cb.like(cb.lower(wine.get("wineName")), "%" + wineName.toLowerCase() + "%");
    }

    public static Specification<Wine> hasType(String wineType) {
        return (wine, cq, cb) -> cb.equal(cb.lower(wine.get("wineType")), wineType.toLowerCase());
    }

    public static Specification<Wine> hasGrape(String grape) {
        return (wine, cq, cb) -> cb.like(cb.lower(wine.get("grape")), "%" + grape.toLowerCase() + "%");
    }
    public static Specification<Wine> hasRegion(String region) {
        return (wine, cq, cb) -> cb.equal(cb.lower(wine.get("region")), region.toLowerCase());
    }

    public static Specification<Wine> hasDenomination(String denomination) {
        return (wine, cq, cb) -> cb.equal(cb.lower(wine.get("denomination")), denomination.toLowerCase());
    }

    public static Specification<Wine> hasYear(int year) {
        return (wine, cq, cb) -> cb.equal(wine.get("year"), year);
    }

    public static Specification<Wine> hasAlcoholPercentage(double alcoholPercentage) {
        return (wine, cq, cb) -> cb.equal(wine.get("alcoholPercentage"), alcoholPercentage);
    }





    public static Specification<Wine> hasMultipleFilters(String wineName,
                                                         String wineType,
                                                         String grape,
                                                         String region,
                                                         String denomination,
                                                         int year,
                                                         double alcoholPercentage) {

        Specification<Wine> spec = Specification.where(null);

        if(wineName != null && !wineName.isEmpty()) {
            spec = spec.and(hasName(wineName));
        }
        if(wineType != null && !wineType.isEmpty()) {
            spec = spec.and(hasType(wineType));
        }
        if(grape != null && !grape.isEmpty()) {
            spec = spec.and(hasGrape(grape));
        }
        if(region != null && !region.isEmpty()) {
            spec = spec.and(hasRegion(region));
        }
        if(denomination != null && !denomination.isEmpty()) {
            spec = spec.and(hasDenomination(denomination));
        }
        if(year != 0) {
            spec = spec.and(hasYear(year));
        }
        if(alcoholPercentage != 0.0) {
            spec = spec.and(hasAlcoholPercentage(alcoholPercentage));
        }

        return spec;
    }
}
