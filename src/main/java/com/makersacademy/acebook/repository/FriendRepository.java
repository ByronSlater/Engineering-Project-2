package com.makersacademy.acebook.repository;

import org.springframework.data.repository.CrudRepository;

import com.makersacademy.acebook.model.Friend;
import com.makersacademy.acebook.model.User;

public interface FriendRepository extends CrudRepository<Friend, Long> {
    public Friend findBySenderAndReceiver(User sender, User receiver);
}
