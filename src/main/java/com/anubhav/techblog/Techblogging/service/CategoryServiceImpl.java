package com.anubhav.techblog.Techblogging.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.anubhav.techblog.Techblogging.dao.CategoryDao;
import com.anubhav.techblog.Techblogging.entity.Category;
import com.anubhav.techblog.Techblogging.entity.Role;
import com.anubhav.techblog.Techblogging.entity.User;

@Service
public class CategoryServiceImpl implements CategoryService {
	
	private CategoryDao categoryDao;
	
	@Autowired
	public CategoryServiceImpl(CategoryDao theCategoryDao) {
		super();
		this.categoryDao = theCategoryDao;
	}

	@Override
	public List<Category> getAllCategories() {
		// TODO Auto-generated method stub
		return categoryDao.findAll();
	}

	@Override
	public ResponseEntity<String> saveCategory(Category category, MultipartFile file, User currentUser) {
		// TODO Auto-generated method stub
		final String UPLOAD_DIR = "src/main/resources/static/pics/";
		try {
            if (!currentUser.getRoles().contains(new Role("ROLE_ADMIN"))) {
                return ResponseEntity.ok("unauthorized");
            }

            // Save file (if provided)
            String fileName = null;
            if (file != null && !file.isEmpty()) {
                fileName = file.getOriginalFilename();
                Path uploadPath = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(uploadPath.getParent());
                Files.write(uploadPath, file.getBytes());
            }

            // Populate post fields
            category.setPic(fileName);

            categoryDao.save(category);
            return ResponseEntity.ok("done");

        } catch (IOException e) {
            return ResponseEntity.ok("file-error");
        } catch (Exception e) {
            return ResponseEntity.ok("error");
        }

	}
	
}
