package com.anubhav.techblog.Techblogging.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.anubhav.techblog.Techblogging.entity.Category;

@Repository
public interface CategoryDao extends JpaRepository<Category, Integer> {

}
