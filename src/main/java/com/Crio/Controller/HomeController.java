package com.Crio.Controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Crio.Model.User;
import com.Crio.Model.jsonObject;
import com.Crio.Reposiroty.*;
import com.Crio.helper.Message;
import com.Crio.helper.encryptionDecryption;



@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private jsonObjectRepository jsonRepository;
	
	@RequestMapping("/")
    private String home(Model model){
		
        model.addAttribute("title", "Home - Smart Contact Manager");
        System.out.println("jjkcasasda");
        return "home";
    }

    @RequestMapping("/about")
    private String about(Model model){
        model.addAttribute("title", "About - Smart Contact Manager");
        return "about";
    }

    @RequestMapping("/signUp")
    private String signUp(Model model){
        model.addAttribute("title", "SignUp - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signUp";
    }
   
    @GetMapping("/signIn")
    public String customLogin(Model model) {
    	 model.addAttribute("title", "Login Page");
    	return "login";
    }
   
	 
    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,
                               @RequestParam(value = "agreement", defaultValue = "false") Boolean agreement,
                               Model model,
                               HttpSession session) {

        try {

            if (!agreement) {
                System.out.println("You have not agreed the terms & conditions");
                throw new Exception("You have not agreed the terms & conditions");
            }
            if (bindingResult.hasErrors()){
                model.addAttribute("user", user);
                return "signUp";
            }
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            System.out.println("USER: " + user.toString());
            System.out.println("AGREEMENT: " + agreement);
            String jsonObj=user.toJson();
            System.out.println(jsonObj);
            
            User savedUser = this.userRepository.save(user);
            jsonObject js=new jsonObject();
            
            encryptionDecryption enc=new encryptionDecryption();
            String ans=enc.encrypt(jsonObj, "VishalkumarKey12");
            
            js.setJson(ans);
            js.setEmail(user.getEmail());
            
            
            jsonObject obj=this.jsonRepository.save(js);
            System.out.println(obj);
           
            model.addAttribute("user", new User());

            session.setAttribute("message",new Message("Successfully Registered!! ", "alert-success"));

            return "signUp";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            session.setAttribute(
                    "message",
                    new Message("Something went wrong !! " + e.getMessage(), "alert-danger")
            );
            return "signUp";
        }
		
        
		 
	 }

}

