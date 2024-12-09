package com.wine.ai_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.ai_service.dto.WineInfo;
import com.wine.ai_service.service.TerritApiService;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@Service
public class TerritApiServiceImpl implements TerritApiService{

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final VertexAiGeminiChatClient vertexAiGeminiChatClient;

	@Value("${api.regions.url}")
	private String url;

	@Value("${api.key}")
	private String key;

	@Value("${api.key.value}")
	private String key_value;


	@Autowired
	public TerritApiServiceImpl(RestTemplate restTemplate, VertexAiGeminiChatClient vertexAiGeminiChatClient) {
		this.restTemplate = restTemplate;
		this.objectMapper = new ObjectMapper();
		this.vertexAiGeminiChatClient = vertexAiGeminiChatClient;
	}

	public List<String> getRegions() {

		HttpHeaders headers = new HttpHeaders();
		headers.set(key, key_value);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, Object> responseMap;

		try {
			responseMap = objectMapper.readValue(response.getBody(), Map.class);
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse response", e);
		}

		if (responseMap != null && responseMap.containsKey("data")) {
			List<Map<String, String>> data = (List<Map<String, String>>) responseMap.get("data");

			return data.stream()
					.map(regione -> regione.get("denominazione"))
					.collect(toList());
		} else {
			throw new RuntimeException("Response is null or does not contain data");
		}
	}

	public String getTypicalDishes(String region) {

		String userMessage = """
            Generami solo una lista con i nomi dei piatti tipici italiani della regione {region}, uno per riga,
             ma solo se il nome della regione esiste, altrimenti restituisci un messaggio che chiede di inserire una regione esistente
            """;

		PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of("region", region));
		Prompt prompt = promptTemplate.create();

		Generation generation = vertexAiGeminiChatClient.call(prompt).getResult();
		return generation.getOutput().getContent();
	}
}