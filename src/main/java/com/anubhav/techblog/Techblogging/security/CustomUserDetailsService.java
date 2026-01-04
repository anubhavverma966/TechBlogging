package com.anubhav.techblog.Techblogging.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.anubhav.techblog.Techblogging.dao.UserDao;
import com.anubhav.techblog.Techblogging.entity.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserDao userDao;
	
	@Autowired
	public CustomUserDetailsService(UserDao theUserDao) {
		this.userDao = theUserDao;
	}
	
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		
		User user = userDao.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("no user with email: "+ email));
		
		
		return new CustomUserDetails(user);
	}

}
