package com.aquatrack.feeding.service;

import com.aquatrack.feeding.entity.AutoFeedingState;
import com.aquatrack.feeding.repository.AutoFeedingStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedingStateService {

    private final AutoFeedingStateRepository repository;

    public void setAutoFeedingEnabled(Long aquariumId, boolean enabled) {
        AutoFeedingState state = repository.findById(aquariumId)
                .orElse(new AutoFeedingState(aquariumId, true));
        state.setAutoFeedingEnabled(enabled);
        repository.save(state);
    }

    public boolean isAutoFeedingEnabled(Long aquariumId) {
        return repository.findById(aquariumId)
                .map(AutoFeedingState::isAutoFeedingEnabled)
                .orElse(true);
    }
}
