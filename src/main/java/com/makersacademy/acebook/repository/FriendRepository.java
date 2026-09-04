package com.makersacademy.acebook.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.makersacademy.acebook.model.Friend;
import com.makersacademy.acebook.model.User;

public interface FriendRepository extends CrudRepository<Friend, Long> {
    public Optional<Friend> findBySenderAndReceiver(User sender, User receiver);
}
