package com.codemines.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codemines.entites.UserMaster;

public interface UserMasterRepo extends JpaRepository<UserMaster, Integer> {

	public UserMaster  findByEmail(String email);
	
}
