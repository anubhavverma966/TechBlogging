package com.anubhav.techblog.Techblogging.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "categories")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cid")
	private int id;
	
	@Column(name = "cname")
	@NotBlank(message = "should not be empty")
    private String name;
	
	@Column(name="description")
    private String description;
	
	@Column(name="pic")
    private String pic;
	
	@OneToMany(mappedBy = "category")
	private List<Post> posts;
	
	public void addPost(Post thePost) {
		if(posts == null) {
			posts = new ArrayList<Post>();
		}
		posts.add(thePost);
	}

	public Category() {
		super();
	}

	public Category(String name, String description, String pic) {
		super();
		this.name = name;
		this.description = description;
		this.pic = pic;
	}

	public int getId() {
		return id;
	}

	public void setId(int cid) {
		this.id = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	@Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name + ", description=" + description + ", pic=" + pic + "]";
	}	
	
}
