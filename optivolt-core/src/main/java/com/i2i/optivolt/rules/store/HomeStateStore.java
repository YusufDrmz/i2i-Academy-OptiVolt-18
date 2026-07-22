package com.i2i.optivolt.rules.store;

import com.i2i.optivolt.rules.model.HomeState;
import java.util.Optional;

public interface HomeStateStore {
    Optional<HomeState> findByHomeId(Long homeId);

    void save(HomeState state);

    HomeState findOrCreate(Long homeId, HomeState defaultState);
}