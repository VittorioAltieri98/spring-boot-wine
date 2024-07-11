package com.wine.ai_service.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.wine.ai_service.client.WineServiceClient;
import com.wine.ai_service.dto.*;
import com.wine.ai_service.exception.*;
import com.wine.ai_service.mapper.UserWinePairingMapper;
import com.wine.ai_service.mapper.WinePairingMapper;
import com.wine.ai_service.model.PairingRequest;
import com.wine.ai_service.model.UserWinePairing;
import com.wine.ai_service.model.WinePairing;
import com.wine.ai_service.repository.PairingRequestRepository;
import com.wine.ai_service.repository.UserWinePairingRepository;
import com.wine.ai_service.repository.WinePairingRepository;
import com.wine.ai_service.service.WinePairingService;
import io.grpc.StatusRuntimeException;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WinePairingServiceImpl implements WinePairingService {

    @Autowired
    private WinePairingRepository winePairingRepository;

    @Autowired
    private PairingRequestRepository pairingRequestRepository;

    @Autowired
    private UserWinePairingRepository userWinePairingRepository;

    @Autowired
    private VertexAiGeminiChatClient vertexAiGeminiChatClient;

    @Autowired
    private WineServiceClient wineServiceClient;

    @Autowired
    private WinePairingMapper winePairingMapper;

   @Autowired
   private UserWinePairingMapper userWinePairingMapper;


    public WinePairingDTO generateWinePairing(Long id) {
        WineDTO wineDTO = wineServiceClient.getWineById(id);
        return generatePairing(wineDTO);
    }

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

    public String generatePairingsByFoodMessage(String message){

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
    public String generateInfoWithFilters(String wineType, String region) {
        String userMessage = """
                Generami le informazioni di un vino di colore {wineType} della regione {region} che esiste.
                """;

        PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of("wineType", wineType, "region", region));
        Prompt prompt = promptTemplate.create();
        ChatResponse chatResponse = vertexAiGeminiChatClient.call(prompt);
        return chatResponse.getResult().getOutput().getContent();
    }

    public WineInfo generateWineInfoWithFilters(String wineType, String region) {
        BeanOutputParser<WineInfo> outputParser = new BeanOutputParser<>(WineInfo.class);

        String userMessage = """
                Generami le informazioni in italiano di un vino di colore {wineType} della regione {region} che esiste.
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of("wineType", wineType, "region", region, "format", outputParser.getFormat()));
        Prompt prompt = promptTemplate.create();
        try {
            Generation generation = vertexAiGeminiChatClient.call(prompt).getResult();
            WineInfo wineInfo = outputParser.parse(generation.getOutput().getContent());
            return wineInfo;
        } catch (StatusRuntimeException e) {
            throw new QuotaExceededException("Quota superata, riprovare tra qualche secondo", e);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof JsonParseException) {
                throw new CustomJsonParseException("Si è verificato un errore durante la generazione delle informazioni sul vino, riprovare", e.getCause());
            } else {
                throw new RuntimeException("Errore di processing JSON durante la generazione delle informazioni sul vino", e);
            }
        }

    }



    public WinePairingDTO getWinePairingById(Long id) throws WinePairingNotFoundException {
        Optional<WinePairing> optional = winePairingRepository.findById(id);
        if(optional.isPresent()) {
            WinePairing winePairing = optional.get();
            return winePairingMapper.winePairingToWinePairingDTO(winePairing);
        } else throw new WinePairingNotFoundException("WinePairing not found with id " + id);
    }

    public WinePairingDTO getWinePairingByWineId(Long wineId) throws WinePairingNotFoundException{
        Optional<WinePairing> optional = winePairingRepository.findByWineId(wineId);
        if(optional.isPresent()){
            WinePairing winePairing = optional.get();
            return winePairingMapper.winePairingToWinePairingDTO(winePairing);
        } else throw new WinePairingNotFoundException("WinePairing not found with id " + wineId);
    }

    @Override
    public UserWinePairingDTO createUserWinePairing(String wineType, String region, Jwt jwt) throws UserWinePairingAlreadyExistsException {

        String id = jwt.getSubject();
        WineInfo wineInfo = generateWineInfoWithFilters(wineType, region);

        Optional<UserWinePairing> optionalUserWinePairing = userWinePairingRepository.findByWineNameAndUserId(wineInfo.getWineName(), id);

        if(optionalUserWinePairing.isPresent()) {
            throw new UserWinePairingAlreadyExistsException("Abbinamento già esistente, riprovare.");
        }
            UserWinePairing userWinePairing = UserWinePairing.builder()
                    .wineName(wineInfo.getWineName())
                    .wineType(wineInfo.getWineType())
                    .region(wineInfo.getRegion())
                    .denomination(wineInfo.getDenomination())
                    .wineDescription(wineInfo.getWineDescription())
                    .serviceTemperature(wineInfo.getServiceTemperature())
                    .foodPairings(wineInfo.getFoodPairings())
                    .pairingDate(LocalDateTime.now())
                    .foodsNameAndDescriptionOfWhyThePairingIsRecommended(wineInfo.getFoodsNameAndDescriptionOfWhyThePairingIsRecommended())
                    .userId(id)
                    .build();

        UserWinePairing savedUserWinePairing = userWinePairingRepository.save(userWinePairing);

        savePairingRequest(id, wineType, region);

        return userWinePairingMapper.userWinePairingToUserWinePairingDTO(savedUserWinePairing);
    }

    public void savePairingRequest(String userId, String wineType, String region) {
        PairingRequest pairingRequest = PairingRequest.builder()
                .userId(userId)
                .wineType(wineType)
                .region(region)
                .requestDate(LocalDateTime.now())
                .build();

        pairingRequestRepository.save(pairingRequest);
    }

    public List<PopularPairing> getTopPopularPairings() {
        List<Object[]> results = pairingRequestRepository.findGroupedPairingRequests();

        return results.stream()
                .map(result -> new PopularPairing((String) result[0], (String) result[1], (Long) result[2]))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserWinePairingDTO> getUserWinePairings(Jwt jwt) {

        String id = jwt.getSubject();

        List<UserWinePairing> userWinePairings = userWinePairingRepository.findAllByUserId(id);

        List<UserWinePairingDTO> response = userWinePairings.stream()
                .map(pairing -> userWinePairingMapper.userWinePairingToUserWinePairingDTO(pairing))
                .collect(Collectors.toList());

        return response;
    }

    @Override
    public void deleteUserWinePairing(Long id, String userId) throws UserWinePairingNotFoundException {
        //Ottieni userWinePairing dall'id.
        //Verificare che l'oggetto userWinePairing abbia lo stesso userId dell'utente corrente.
        //Altrimenti lanciare eccezione.

        UserWinePairing userWinePairing = userWinePairingRepository.findById(id).orElseThrow(() -> new UserWinePairingNotFoundException("Abbinamento non trovato"));
        if(userWinePairing.getUserId().equals(userId)) {
            userWinePairingRepository.delete(userWinePairing);
        }
        else throw new UserWinePairingNotFoundException("Abbinamento non trovato");
    }

    @Override
    @Transactional
    public void deleteAllUserWinePairing(String userid) {
        userWinePairingRepository.deleteByUserId(userid);
    }
}
