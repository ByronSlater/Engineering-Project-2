package com.makersacademy.acebook.repository;

import org.springframework.data.repository.CrudRepository;

import com.makersacademy.acebook.model.FriendStatus;

public interface FriendStatusRepository extends CrudRepository<FriendStatus, Long> {
    public boolean existsByDescription(String description);
    public FriendStatus findByDescription(String description);
}
