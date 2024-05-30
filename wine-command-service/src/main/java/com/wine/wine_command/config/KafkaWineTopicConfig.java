package com.wine.wine_command.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaWineTopicConfig {

    @Bean
    public NewTopic wineTopic() {
        return TopicBuilder
                .name("wine-topic")
                .build();
    }
}
