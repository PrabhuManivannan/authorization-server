package com.myorg.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.myorg.model.User;

@Repository
public interface UserDetailRepository extends JpaRepository<User,Integer> {


    Optional<User> findByName(String name);

}