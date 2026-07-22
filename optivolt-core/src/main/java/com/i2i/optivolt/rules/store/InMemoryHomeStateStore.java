package com.i2i.optivolt.rules.store;

import com.i2i.optivolt.rules.model.HomeState;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnMissingBean(HomeStateStore.class)

public class InMemoryHomeStateStore implements HomeStateStore {

    private final Map<Long, HomeState> homes = new ConcurrentHashMap<>();

    @Override
    public Optional<HomeState> findByHomeId(Long homeId) {
        return Optional.ofNullable(homes.get(homeId));
    }

    @Override
    public void save(HomeState state) {
        homes.put(state.getHomeId(), state);
    }

    @Override
    public HomeState findOrCreate(Long homeId, HomeState defaultState) {
        return homes.computeIfAbsent(homeId, id -> defaultState);
    }
}
