package com.i2i.optivolt.notification;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnMissingBean(HomeInfoProvider.class)

public class InMemoryHomeInfoProvider implements HomeInfoProvider {

    private final Map<Long, HomeContact> contacts = new ConcurrentHashMap<>();

    public InMemoryHomeInfoProvider() {
        // Matches the sample row already seeded in docker/init.sql
        contacts.put(1L, new HomeContact(1L, "Daire 12 - Yılmaz Ailesi", "test@example.com"));
    }

    @Override
    public HomeContact getHomeContact(Long homeId) {
        return contacts.getOrDefault(homeId,
                new HomeContact(homeId, "Home #" + homeId, "unknown@example.com"));
    }
}