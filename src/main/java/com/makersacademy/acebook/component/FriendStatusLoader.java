package com.makersacademy.acebook.component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.makersacademy.acebook.model.FriendStatus;
import com.makersacademy.acebook.repository.FriendStatusRepository;

import jakarta.transaction.Transactional;

@Component
public class FriendStatusLoader {
    private final FriendStatusRepository friendStatusRepository;
    private final Map<Long, String> descriptionsById = new ConcurrentHashMap<>();

    public FriendStatusLoader(FriendStatusRepository friendStatusRepository) {
        this.friendStatusRepository = friendStatusRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    @SuppressWarnings("null")
    public void loadDefaults() {
        List<FriendStatus> all = Arrays.stream(DefaultFriendStatuses.values())
            .map(fs -> {
                var n = new FriendStatus();
                n.setDescription(fs.name().toLowerCase());
                return n; })
            .filter(fs -> !friendStatusRepository.existsByDescription(fs.getDescription()))
            .toList();

        friendStatusRepository.saveAll(all);
        friendStatusRepository.findAll().forEach(status ->
            descriptionsById.put(status.getId(), status.getDescription()));
    }

    public String getDescription(Long statusId) {
        return descriptionsById.get(statusId);
    }

    public enum DefaultFriendStatuses {
        PENDING,
        ACCEPTED,
        DECLINED
    }
}
