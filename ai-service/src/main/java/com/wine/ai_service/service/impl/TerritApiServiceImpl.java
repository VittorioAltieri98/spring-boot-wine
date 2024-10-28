package com.wine.ai_service.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.ai_service.exception.CustomJsonParseException;
import com.wine.ai_service.exception.QuotaExceededException;
import com.wine.ai_service.service.TerritApiService;
import io.grpc.StatusRuntimeException;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;

@Service
public class TerritApiServiceImpl implements TerritApiService{

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	private final VertexAiGeminiChatClient vertexAiGeminiChatClient;

	@Autowired
	public TerritApiServiceImpl(RestTemplate restTemplate, VertexAiGeminiChatClient vertexAiGeminiChatClient) {
		this.restTemplate = restTemplate;
		this.objectMapper = new ObjectMapper();
		this.vertexAiGeminiChatClient = vertexAiGeminiChatClient;
	}

	public List<String> getRegions() {
		String url = "https://api.territ.it/regioni";

		HttpHeaders headers = new HttpHeaders();
		headers.set("x-api-key", "d859dca139d935a530b95fece2ffd1ac5df8bdd4dc40a7ea077248f9f44a9869");

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

	public List<String> getTypicalDishes(String region) {

		String userMessage = """
            Generami una lista di nomi di piatti tipici italiani della regione {region}, separati da una virgola
            """;

		PromptTemplate promptTemplate = new PromptTemplate(userMessage, Map.of("region", region));
		Prompt prompt = promptTemplate.create();

		try {
			Generation generation = vertexAiGeminiChatClient.call(prompt).getResult();
			String output = generation.getOutput().getContent();

			return stream(output.split(","))
					.map(String::trim)
					.collect(toList());
		} catch (StatusRuntimeException e) {
			throw new QuotaExceededException("Quota superata, riprovare tra qualche secondo", e);
		} catch (RuntimeException e) {
			throw new RuntimeException("Errore durante la generazione dei piatti tipici", e);
		}
	}
}