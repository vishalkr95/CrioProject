package com.Crio.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;



import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.Crio.Model.Contact;
import com.Crio.Model.User;
import com.Crio.Model.jsonObject;
import com.Crio.Reposiroty.ContactRepository;
import com.Crio.Reposiroty.UserRepository;
import com.Crio.Reposiroty.jsonObjectRepository;
import com.Crio.helper.Message;
import com.Crio.helper.encryptionDecryption;


@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private jsonObjectRepository jsonRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	// method to adding common data 
	
	@ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String userName = principal.getName();
        User user=new User();
        encryptionDecryption enc=new encryptionDecryption();
        jsonObject jObj=jsonRepository.getUserByUserName(userName);
        String obj=jObj.getJson();
        String ans="";
        try {
        	ans=enc.decrypt(obj, "VishalkumarKey12");
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
        User user1=user.fromJson(ans);
        
        System.out.println(user1);
        System.out.println(jObj);
        
        
        model.addAttribute("user", user1);
    }
	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal princple) {
//		String username=princple.getName();
//	   
//	    User user=this.userRepository.getUserByUserName(username);
//	    System.out.println(user);
//	    model.addAttribute("user", user);
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}
	
	
	
	@GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }
	
	//Processing add contact form
    @PostMapping("/process-contact")
    public String processContact(@Valid @ModelAttribute Contact contact, BindingResult bindingResult,@RequestParam("processImage") MultipartFile multipartFile, 
                                 Model model, Principal principal, HttpSession session) {

        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("contact", contact);
                return "normal/add_contact_form";
            }

            String userName = principal.getName();
            User user = this.userRepository.getUserByUserName(userName);
            jsonObject jObj=jsonRepository.getUserByUserName(userName);
            
            encryptionDecryption enc=new encryptionDecryption();
            
            User user1=new User();
            try {
            	 user1=user.fromJson(enc.decrypt(jObj.getJson(), "VishalkumarKey12"));
            }
            catch(Exception e) {
            	e.printStackTrace();
            }
            
            
            

            // You can use throw exception on alert box
            /*if (3>2){
                throw new  Exception();
                // goto catch block
            }*/

            // processing and uploading file
            if (multipartFile.isEmpty()) {
                //
                System.out.println("File not Uploaded");
                contact.setImageUrl("default.png");
//                model.addAttribute("contact", contact);
//                session.setAttribute("message", new Message("Please Select a Photo", "alert-danger"));
//                return "normal/add_contact_form";

            } else {
                contact.setImageUrl(multipartFile.getOriginalFilename());

                // File save to any folder
                /*String userDirectory = System.getProperty("user.dir");
                String uploadDirectory = userDirectory + "\\uploadImg";
                */
                // image save to static folder
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());
                
                Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("File is Uploaded");
            }
            contact.setUser(user1);
            
            
            user.getContacts().add(contact);
            user1.getContacts().add(contact);
            jsonObject jobj=new jsonObject();
            jobj.setEmail(user1.getEmail());
            jobj.setJson(user.toJson());
            this.userRepository.save(user);
            this.jsonRepository.save(jobj);
            System.out.println("Data: " + contact);
            model.addAttribute("contact", new Contact());

            /*Message Success*/
            session.setAttribute("message", new Message("Contact added Successfully!! ", "alert-success"));

            return "normal/add_contact_form";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("contact", contact);
            /* Message Success */
            session.setAttribute("message", new Message("Something went wrong " + e.getMessage(), "alert-danger"));
        }
         
        return "normal/add_contact_form";

    }
    
    //show contacts handler
    //per page =5[n]
    // current page 0
    
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {

        model.addAttribute("title", "Show user contacts");

        // Get signed user
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);
        jsonObject jObj=jsonRepository.getUserByUserName(userName);
        encryptionDecryption enc=new encryptionDecryption();
        
        User user1=new User();
        try {
        	 user1=user.fromJson(enc.decrypt(jObj.getJson(), "VishalkumarKey12"));
        }
        catch(Exception e) {
        	e.printStackTrace();
        }
        Pageable pageable = PageRequest.of(page, 2);
        Page<Contact> contacts = this.contactRepository.findContactsByUser(user1.getId(),pageable);
        //System.out.println(contacts.getContent().get(0).getImageUrl());
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", contacts.getTotalPages());
        
        return "normal/show_contacts";

    }
    
    @RequestMapping("/{cid}/contact")
    public String showContactDetails(@PathVariable("cid") Integer cid, Model model, Principal principal) {
         System.out.println("Cid:"+cid);
        model.addAttribute("title", "Contact");

        Optional<Contact> contactOptional = this.contactRepository.findById(cid);


        if (contactOptional.isPresent()) {
            Contact contact = contactOptional.get();

            // get current user
            String username = principal.getName();
            User user = this.userRepository.getUserByUserName(username);
            jsonObject jObj=jsonRepository.getUserByUserName(username);
            encryptionDecryption enc=new encryptionDecryption();
            
            User user1=new User();
            try {
            	 user1=user.fromJson(enc.decrypt(jObj.getJson(), "VishalkumarKey12"));
            }
            catch(Exception e) {
            	e.printStackTrace();
            }

            // show contact only current user
            if (user.getId() == contact.getUser().getId()) {
                model.addAttribute("contact", contact);
            }
        }

        return "normal/contact_details";
    }
    
    
 // delete contact handler
    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cid, Model model, Principal principal, HttpSession session) {
        Optional<Contact> contactOptional = this.contactRepository.findById(cid);
        if (contactOptional.isPresent()) {
            Contact contact = contactOptional.get();
            
            // get current user
            String username = principal.getName();
            User user = this.userRepository.getUserByUserName(username);
            jsonObject jObj=jsonRepository.getUserByUserName(username);
            encryptionDecryption enc=new encryptionDecryption();
            
            User user1=new User();
            try {
            	 user1=user.fromJson(enc.decrypt(jObj.getJson(), "VishalkumarKey12"));
            }
            catch(Exception e) {
            	e.printStackTrace();
            }
            
            jsonObject jobj=new jsonObject();
            jobj.setEmail(username);
            jobj.setJson(user1.toJson());

            // delete contact only current user
            if (user.getId() == contact.getUser().getId()) {
            	//contact.setUser(null);
            	
            	// also delete the image from folder 
            	// for delet get image name by contect.getImage();
            	// and make path and delete the image
                this.contactRepository.delete(contact);
                user.getContacts().remove(contact);
                user1.getContacts().remove(contact);
                this.jsonRepository.save(jobj);
                this.userRepository.save(user);

                session.setAttribute("message", new Message("Contact deleted Successfully", "alert-success"));
            }

        }
        return "redirect:/user/show-contacts/0";
    }
    
 // Open update from handler
    @PostMapping("/update-contact/{cid}")
    public String openUpdateForm(@PathVariable("cid") Integer cid, Model model) {

        model.addAttribute("title", "Update Contact");
        Contact contact = this.contactRepository.findById(cid).get();
        model.addAttribute("contact", contact);
        return "normal/update_form";
    }

    // update Contact handler
    @RequestMapping(value = "/process-update", method = RequestMethod.POST)
    public String updateForm(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile multipartFile,
                             Model model, Principal principal, HttpSession session) {
        try {
            // Fetch old contact
            Contact oldContact = this.contactRepository.findById((contact.getcId())).get();
            if (!multipartFile.isEmpty()) {
                // file rewrite
                // At first delete old photo and update photo
                // delete photo
                File deleteFile = new ClassPathResource("static/img/").getFile();
                File oldFile = new File(deleteFile, oldContact.getImageUrl());
                boolean isDelete = oldFile.delete();

                // Update photo
                File saveFile = new ClassPathResource("static/img").getFile();
                // rename file with currentTimeMillis
                String filename = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                filename = System.currentTimeMillis() + filename.toLowerCase().replaceAll(" ", "-");
                Path rootLocation = Paths.get(saveFile + File.separator);

                Files.copy(multipartFile.getInputStream(), rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

                contact.setImageUrl(filename);

            } else {
                contact.setImageUrl(oldContact.getImageUrl());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        User user = this.userRepository.getUserByUserName(principal.getName());
        contact.setUser(user);
        this.contactRepository.save(contact);

        session.setAttribute("message", new Message("Your contact is updated...", "alert-success"));

        // redirect uses for URL not html file
        return "redirect:/user/" + contact.getcId() + "/contact";
    }
	 // Profile handler
	    @GetMapping("/profile")
	    public String yourProfile(Model model) {
	
	        model.addAttribute("title", "Profile");
	
	        return "normal/profile";
	    }


	
}
