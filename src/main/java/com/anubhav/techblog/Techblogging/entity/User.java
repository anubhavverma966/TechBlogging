package com.anubhav.techblog.Techblogging.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.anubhav.techblog.Techblogging.security.AuthProviderPolicy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="user")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "name")
	@NotNull(message = "is required")
	private String name;
	
	@Column(name = "email", unique = true)
	@Email(message = "Enter a valid Email")
	@NotNull(message = "is required")
	private String email;
	
	@Column(name = "password")
	private String password;

	@Column(name = "gender")
	private String gender;
	
	@Column(name = "rdate", nullable = false, updatable = false, insertable = false)
	private Timestamp dateTime;
	
	@Column(name = "about", nullable = false)
	private String about;
	
	@Column(name = "profile", nullable = false)
	private String profile;
	
	@Column(name = "provider", nullable = false)
    private String provider;   // LOCAL / AZURE

    @Column(name = "provider_id")
    private String providerId; // sub claim
	
	@PrePersist
    public void ensureDefaults() {
        if (this.profile == null || this.profile.trim().isEmpty()) {
            this.profile = "default.png";
        }
        if (this.about == null || this.about.trim().isEmpty()) {
        	this.about = "Hey there! I m Using Tech Bloging";
        }
        
    }
	
	@Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
	private boolean enabled = true;
	
	@Column(name = "oauth_linked")
	private boolean oauthLinked = false;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "auth_provider_policy", nullable = false)
	private AuthProviderPolicy authProviderPolicy;

	
	@OneToMany(mappedBy = "user")
	private List<Post> posts;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_role",
			   joinColumns = @JoinColumn(name = "user_id"),
			   inverseJoinColumns = @JoinColumn(name = "role_id")
			)
	private Set<Role> roles = new HashSet<>();
	
	public void addRole(Role role) {
		this.roles.add(role);
	}
	
	public void addPost(Post thePost) {
		if(posts == null) {
			posts = new ArrayList<Post>();
		}
		posts.add(thePost);
	}
    
	public User() {
		super();
	}

	public User(String name, String email, String password, String gender, String about) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
		this.gender = gender;
		this.about = about;
	}

	public User(String name, String email, String password, String gender, String about, Set<Role> roles) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
		this.gender = gender;
		this.about = about;
		this.roles = roles;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Timestamp getDateTime() {
		return dateTime;
	}

	public void setDateTime(Timestamp dateTime) {
		this.dateTime = dateTime;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public boolean isOauthLinked() {
		return oauthLinked;
	}

	public void setOauthLinked(boolean oauthLinked) {
		this.oauthLinked = oauthLinked;
	}	

	public AuthProviderPolicy getAuthProviderPolicy() {
		return authProviderPolicy;
	}

	public void setAuthProviderPolicy(AuthProviderPolicy authProviderPolicy) {
		this.authProviderPolicy = authProviderPolicy;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", gender="
				+ gender + ", dateTime=" + dateTime + ", about=" + about + ", profile=" + profile + "]";
	}
	    
}
