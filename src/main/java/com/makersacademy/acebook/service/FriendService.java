package com.makersacademy.acebook.service;

import org.springframework.stereotype.Service;

import com.makersacademy.acebook.component.FriendStatusLoader;
import com.makersacademy.acebook.model.Friend;
import com.makersacademy.acebook.model.FriendStatus;
import com.makersacademy.acebook.model.User;
import com.makersacademy.acebook.repository.FriendRepository;
import com.makersacademy.acebook.repository.FriendStatusRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FriendService {
    private FriendStatusLoader friendStatusLoader;
    private FriendRepository friendRepository;
    private FriendStatusRepository friendStatusRepository;

    public FriendService(
        FriendStatusLoader friendStatusLoader,
        FriendRepository friendRepository,
        FriendStatusRepository friendStatusRepository
    ) {
        this.friendStatusLoader = friendStatusLoader;
        this.friendRepository = friendRepository;
        this.friendStatusRepository = friendStatusRepository;
    }

    public String getStatusDescription(Friend friend) {
        return friendStatusLoader.getDescription(friend.getStatusId());
    }

    public void sendFriendshipRequest(User sender, User receiver) {
        Friend f = new Friend();
        f.setSender(sender);
        f.setReceiver(receiver);

        FriendStatus fs = friendStatusRepository.findByDescription("pending");

        f.setStatus(fs);

        friendRepository.save(f);
    }

    public void acceptFriendshipRequest(User sender, User receiver) {
        Friend f = friendRepository.findBySenderAndReceiver(sender, receiver);
        FriendStatus fs = friendStatusRepository.findByDescription("accepted");

        f.setStatus(fs);
        friendRepository.save(f);
    }

    public void declineFriendshipRequest(User sender, User receiver) {
        Friend f = friendRepository.findBySenderAndReceiver(sender, receiver);
        FriendStatus fs = friendStatusRepository.findByDescription("declined");

        f.setStatus(fs);
        friendRepository.save(f);
    }
}
