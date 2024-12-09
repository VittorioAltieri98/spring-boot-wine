package com.wine.microservice.functions;

import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.mapper.WineMapper;
import com.wine.microservice.repository.WineRepository;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@Data
public class WineFunctions {

    private final WineRepository wineRepository;

    private final WineMapper wineMapper;

    public WineFunctions(WineRepository wineRepository, WineMapper wineMapper) {
        this.wineRepository = wineRepository;
        this.wineMapper = wineMapper;
    }

    @Bean
    public Supplier<List<WineDTO>> getAllWines() {
        System.out.println("Registering function: getAllWines");
        return () -> wineRepository.findAll()
                .stream()
                .map(wineMapper::wineToWineDTO)
                .collect(Collectors.toList());
    }
}
