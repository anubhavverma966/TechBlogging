package com.anubhav.techblog.Techblogging.dto;

import com.anubhav.techblog.Techblogging.entity.Post;

public class PostDto {
	  private Integer id;
	  private String title;
	  private String content;
	  private String code;
	  private Integer categoryId;
	  private String categoryName;
	  private String pic; // filename
	  

	  public static PostDto from(Post p) {
	      PostDto d = new PostDto();
	      d.setId(p.getPid());
	      d.setTitle(p.getpTitle());
	      d.setContent(p.getpContent());
	      d.setCode(p.getpCode());
	      if (p.getCategory() != null) {
	         d.setCategoryId(p.getCategory().getId());
	         d.setCategoryName(p.getCategory().getName());
	      }
	      d.setPic(p.getpPic());
	      return d;
	  }

	  public Integer getId() {
		  return id;
	  }

	  public void setId(Integer id) {
		  this.id = id;
	  }

	  public String getTitle() {
		  return title;
	  }

	  public void setTitle(String title) {
		  this.title = title;
	  }

	  public String getContent() {
		  return content;
	  }

	  public void setContent(String content) {
		  this.content = content;
	  }

	  public Integer getCategoryId() {
		  return categoryId;
	  }

	  public void setCategoryId(Integer categoryId) {
		  this.categoryId = categoryId;
	  }

	  public String getCategoryName() {
		  return categoryName;
	  }

	  public void setCategoryName(String categoryName) {
		  this.categoryName = categoryName;
	  }
	  public String getPic() {
		  return pic;
	  }

	  public void setPic(String pic) {
		  this.pic = pic;
	  }

	  public String getCode() {
		  return code;
	  }

	  public void setCode(String code) {
		  this.code = code;
	  }
	}
