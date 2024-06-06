package com.wine.microservice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
//@TestContainer
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WineRepositoryTest {

    @Test
    void findByWineName() {
    }
}