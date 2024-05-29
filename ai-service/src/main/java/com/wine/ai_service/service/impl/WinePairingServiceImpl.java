package com.wine.ai_service.service.impl;

import com.wine.ai_service.dto.WineDTO;
import com.wine.ai_service.dto.WineInfo;
import com.wine.ai_service.dto.WinePairingDTO;
import com.wine.ai_service.mapper.WinePairingMapper;
import com.wine.ai_service.model.WinePairing;
import com.wine.ai_service.repository.WinePairingRepository;
import com.wine.ai_service.service.WinePairingService;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WinePairingServiceImpl implements WinePairingService {

    @Autowired
    private WinePairingRepository winePairingRepository;

    @Autowired
    private VertexAiGeminiChatClient vertexAiGeminiChatClient;

    @Autowired
    private WinePairingMapper winePairingMapper;

    public WinePairingDTO generatePairing(WineDTO wineDTO) {
        BeanOutputParser<WinePairing> outputParser = new BeanOutputParser<>(WinePairing.class);
        String userMessage = """
                Generami il JSON con le informazioni necessarie in italiano del vino {wineDTO} passato come parametro nella richiesta effettuata dall'utente.
                {format}
                """;
        PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of("wineDTO", wineDTO, "format", outputParser.getFormat()));
        Prompt prompt = promptTemplate.create();
        Generation generation = vertexAiGeminiChatClient.call(prompt).getResult();
        WinePairing winePairing = outputParser.parse(generation.getOutput().getContent());

        WinePairing savedWinePairing = winePairingRepository.save(winePairing);
        return winePairingMapper.winePairingToWinePairingDTO(savedWinePairing);
    }

    public String generateWinePairingInformation(String message){

        SystemMessage systemMessage = new SystemMessage("""
                Devi occuparti di rispondere alle richieste che ti vengono fatte riguardo ad abbinamenti di vini ad un cibo.
                A qualsiasi richiesta che non sia un cibo rispondi "La richiesta non è valida, riprovare".
                """);
        UserMessage userMessage = new UserMessage(message);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse chatResponse = vertexAiGeminiChatClient.call(prompt);
        return chatResponse.getResult().getOutput().getContent();
    }

    //Farsi generare dall'IA un vino in base ai parametri scelti dall'utente
    //I parametri possono essere il colore del vino, la regione, etc..
    public String getWineInfoBasedOnFilters(String wineType, String region) {
        String userMessage = """
                Generami le informazioni di un vino di colore {wineType} della regione {region} che esiste.
                """;

        PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of("wineType", wineType, "region", region));
        Prompt prompt = promptTemplate.create();
        ChatResponse chatResponse = vertexAiGeminiChatClient.call(prompt);
        return chatResponse.getResult().getOutput().getContent();
    }

    public WineInfo obtainWineInfoBasedOnFilters(String wineType, String region) {
        BeanOutputParser<WineInfo> outputParser = new BeanOutputParser<>(WineInfo.class);

        String userMessage = """
                Generami le informazioni in italiano di un vino di colore {wineType} della regione {region} che esiste.
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of("wineType", wineType, "region", region, "format", outputParser.getFormat()));
        Prompt prompt = promptTemplate.create();
        Generation generation = vertexAiGeminiChatClient.call(prompt).getResult();
        WineInfo wineInfo = outputParser.parse(generation.getOutput().getContent());
        return wineInfo;
    }

    public WinePairingDTO getWinePairingById(Long id) throws Exception {
        Optional<WinePairing> optional = winePairingRepository.findById(id);
        if(optional.isPresent()) {
            WinePairing winePairing = optional.get();
            return winePairingMapper.winePairingToWinePairingDTO(winePairing);
        }else throw new Exception("WinePairing not found with id " + id);
    }

    public void saveWinePair(WinePairing winePairing) {
        winePairingRepository.save(winePairing);
    }
}
