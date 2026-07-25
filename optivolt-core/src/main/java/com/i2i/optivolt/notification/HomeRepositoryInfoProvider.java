package com.i2i.optivolt.notification;

import com.i2i.optivolt.home.repository.HomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HomeRepositoryInfoProvider implements HomeInfoProvider {

    private final HomeRepository homeRepository;

    @Override
    public HomeContact getHomeContact(Long homeId) {
        return homeRepository.findById(homeId)
                .map(home -> new HomeContact(home.getId(), "Home #" + home.getId(), home.getContactEmail()))
                .orElseGet(() -> new HomeContact(homeId, "Home #" + homeId, "unknown@example.com"));
    }
}