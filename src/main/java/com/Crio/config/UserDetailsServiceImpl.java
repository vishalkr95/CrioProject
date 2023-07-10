
 package com.Crio.config;
 
 
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.security.core.userdetails.UserDetails;
 import org.springframework.security.core.userdetails.UserDetailsService;
 import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.Crio.Model.User;
import com.Crio.Model.jsonObject;
import com.Crio.Reposiroty.UserRepository;
import com.Crio.Reposiroty.jsonObjectRepository;



 public class UserDetailsServiceImpl implements UserDetailsService {

     @Autowired
     private UserRepository userRepository;
     
     @Autowired
     private jsonObjectRepository jsonRepository;
     @Override
     public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

         User user = userRepository.getUserByUserName(email);
         System.out.println("hello");
         jsonObject obj=jsonRepository.getUserByUserName(email);
         System.out.println(obj);
         
         if (user ==null){
             throw new UsernameNotFoundException("User couldn't found !!");
         }

         CustomUserDetails customUserDetails = new CustomUserDetails(user);
         return customUserDetails;
     }
 }