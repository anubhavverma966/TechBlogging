package com.anubhav.techblog.Techblogging.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anubhav.techblog.Techblogging.entity.Post;

@Repository
public interface PostDao extends JpaRepository<Post, Integer> {

	List<Post> findByCategoryId(int id);
}
