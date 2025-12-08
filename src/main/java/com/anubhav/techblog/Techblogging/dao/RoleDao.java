package com.anubhav.techblog.Techblogging.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anubhav.techblog.Techblogging.entity.Role;

@Repository
public interface RoleDao extends JpaRepository<Role, Integer> {
	Optional<Role> findByName(String name);
	boolean existsByName(String name);
}
