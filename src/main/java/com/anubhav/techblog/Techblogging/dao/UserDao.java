package com.anubhav.techblog.Techblogging.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anubhav.techblog.Techblogging.entity.User;

@Repository
public interface UserDao extends JpaRepository<User, Integer> {

	Optional<User> findByEmailAndPassword(String email, String password);
	Optional<User> findByEmail(String email);
}
