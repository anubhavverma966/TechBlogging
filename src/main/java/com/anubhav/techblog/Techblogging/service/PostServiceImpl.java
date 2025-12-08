package com.anubhav.techblog.Techblogging.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.anubhav.techblog.Techblogging.dao.CategoryDao;
import com.anubhav.techblog.Techblogging.dao.PostDao;
import com.anubhav.techblog.Techblogging.entity.Category;
import com.anubhav.techblog.Techblogging.entity.Post;
import com.anubhav.techblog.Techblogging.entity.User;

@Service
public class PostServiceImpl implements PostService {
	
	private PostDao postDao;
	private CategoryDao categoryDao;
	
	@Autowired
	public PostServiceImpl(PostDao thePostDao, CategoryDao theCategoryDao) {
		super();
		this.postDao = thePostDao;
		this.categoryDao = theCategoryDao;
	}

	@Override
	public List<Post> getAllPost() {
		// TODO Auto-generated method stub
		return postDao.findAll(Sort.by("pid").descending());
	}

	@Override
	public List<Post> getPostByCategoryId(int id) {
		// TODO Auto-generated method stub
		return postDao.findByCategoryId(id);
	}
	
	@Override
	public Post getPostByPostId(int id) {
		// TODO Auto-generated method stub
		Optional<Post> result = postDao.findById(id);
		
		Post thePost = null;
		
		if (result.isPresent()) {
			thePost = result.get();
		}
		else {
			// we didn't find the user
			throw new RuntimeException("Did not find post with id - " + id);
		}
		
		return thePost;
	}

	@Override
	public ResponseEntity<String> savePost(Post post, int categoryId, MultipartFile file, User currentUser) {
		// TODO Auto-generated method stub
		final String UPLOAD_DIR = "src/main/resources/static/blog_pics/";
		try {
            if (currentUser == null) {
                return ResponseEntity.ok("unauthorized");
            }

            // Validate category
            Category category = categoryDao.findById(categoryId).orElse(null);
            if (category == null) {
                return ResponseEntity.ok("invalid-category");
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
            post.setCategory(category);
            post.setUser(currentUser);
            post.setpPic(fileName);
            post.setpDate(new Timestamp(System.currentTimeMillis()));

            postDao.save(post);
            return ResponseEntity.ok("done");

        } catch (IOException e) {
            return ResponseEntity.ok("file-error");
        } catch (Exception e) {
            return ResponseEntity.ok("error");
        }
    
	}

	@Override
	public String updatePost(Post formPost, MultipartFile file, User currentUser) {
		final String UPLOAD_DIR = "src/main/resources/static/blog_pics/";
	    Post existing = postDao.findById(formPost.getPid()).orElse(null);
	    if (existing == null) return "Not Found";

	    // security check: owner or admin
	    boolean isOwner = (existing.getUser().getId() == currentUser.getId());
	    boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
	    if (!isOwner && !isAdmin) return "Failed";

	    // update fields
	    existing.setpTitle(formPost.getpTitle());
	    existing.setpContent(formPost.getpContent());
	    existing.setpCode(formPost.getpCode());
	    // update category
	    if (formPost.getCategory() != null && formPost.getCategory().getId() > 0) {
	       Category cat = categoryDao.findById(formPost.getCategory().getId()).orElse(null);
	       existing.setCategory(cat);
	    }

	    if (file != null && !file.isEmpty()) {
	        try {
	            // directory path (NOT including filename)
	            Path uploadDir = Paths.get(UPLOAD_DIR);

	            // ensure directory exists
	            Files.createDirectories(uploadDir);

	            // delete old image if present
	            String oldPic = existing.getpPic();
	            if (oldPic != null && !oldPic.isBlank() && !oldPic.equals("default_blog.png")) {
	                Files.deleteIfExists(uploadDir.resolve(oldPic));
	            }

	            // save new image
	            String newFileName = file.getOriginalFilename();
	            Path newFilePath = uploadDir.resolve(newFileName);
	            Files.write(newFilePath, file.getBytes());

	            // update entity with new filename
	            existing.setpPic(newFileName);

	        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.getMessage();
	        }
		}
	    postDao.save(existing);
	    return "Success";
	}

	@Override
	public boolean deletePostById(int id, User currentUser) {
		// TODO Auto-generated method stub
		Post post = postDao.findById(id).orElse(null);
		
		if(post == null)
			return false;
		
		boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
		
		if(!isAdmin)
			return false;
		
		postDao.delete(post);
		
		return true;
	}

}
