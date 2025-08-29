package org.example.backend.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdServiceTest {
    private final IdService idService = new IdService();

    @Test
    void generateId_returnsUniqueString() {
        String id1 = idService.generateId();
        String id2 = idService.generateId();

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
    }

}