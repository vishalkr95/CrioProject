package com.Crio.Reposiroty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Crio.Model.User;
import com.Crio.Model.jsonObject;

public interface jsonObjectRepository extends JpaRepository<jsonObject, Integer> {

	
	@Query("SELECT u FROM jsonObject u WHERE u.email = :email")
	jsonObject getUserByUserName(@Param("email") String email);
}
