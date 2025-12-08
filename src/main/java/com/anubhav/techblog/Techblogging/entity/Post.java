package com.anubhav.techblog.Techblogging.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "posts")
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pid")
	private int pid;
	
	@Column(name = "pTitle")
	@NotBlank(message = "should not be empty")
    private String pTitle;
	
	@Column(name = "pContent")
    private String pContent;
	
	@Column(name = "pCode")
    private String pCode;
	
	@Column(name = "pPic", nullable = false)
    private String pPic;
	
	@PrePersist
    public void ensureDefaults() {
        if (this.pPic == null || this.pPic.trim().isEmpty()) {
            this.pPic = "default_blog.png";
        }
    }
	
	@Column(name = "pDate")
    private Timestamp pDate;
	
	@ManyToOne
	@JoinColumn(name = "userId")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "catId")
    private Category category;

	public Post() {
		super();
	}

	public Post(String pTitle, String pContent, String pCode, String pPic) {
		super();
		this.pTitle = pTitle;
		this.pContent = pContent;
		this.pCode = pCode;
		this.pPic = pPic;
	}

	public Post(String pTitle, String pContent, String pCode, String pPic, User user, Category category) {
		super();
		this.pTitle = pTitle;
		this.pContent = pContent;
		this.pCode = pCode;
		this.pPic = pPic;
		this.user = user;
		this.category = category;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getpTitle() {
		return pTitle;
	}

	public void setpTitle(String pTitle) {
		this.pTitle = pTitle;
	}

	public String getpContent() {
		return pContent;
	}

	public void setpContent(String pContent) {
		this.pContent = pContent;
	}

	public String getpCode() {
		return pCode;
	}

	public void setpCode(String pCode) {
		this.pCode = pCode;
	}

	public String getpPic() {
		return pPic;
	}

	public void setpPic(String pPic) {
		this.pPic = pPic;
	}

	public Timestamp getpDate() {
		return pDate;
	}

	public void setpDate(Timestamp pDate) {
		this.pDate = pDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "Post [pid=" + pid + ", pTitle=" + pTitle + ", pContent=" + pContent + ", pCode=" + pCode + ", pPic="
				+ pPic + ", pDate=" + pDate + "]";
	}    
    
}
