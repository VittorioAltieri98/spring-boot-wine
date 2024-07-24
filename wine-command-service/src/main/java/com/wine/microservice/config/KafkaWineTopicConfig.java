package com.wine.microservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaWineTopicConfig {

    @Value("${kafka.wine.topic.name}")
    private String wineTopicName;

    @Bean
    public NewTopic wineTopic() {
        return TopicBuilder
                .name(wineTopicName)
                .build();
    }
}
