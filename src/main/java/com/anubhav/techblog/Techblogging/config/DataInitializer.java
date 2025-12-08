package com.anubhav.techblog.Techblogging.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import com.anubhav.techblog.Techblogging.dao.RoleDao;
import com.anubhav.techblog.Techblogging.entity.Role;

@Configuration
public class DataInitializer implements ApplicationRunner {
	
	private final RoleDao roleDao;

    public DataInitializer(RoleDao roleDao) { this.roleDao = roleDao; }
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		// TODO Auto-generated method stub
		roleDao.findByName("ROLE_USER").orElseGet(() -> roleDao.save(new Role("ROLE_USER")));
		roleDao.findByName("ROLE_ADMIN").orElseGet(() -> roleDao.save(new Role("ROLE_ADMIN")));
	}

}
