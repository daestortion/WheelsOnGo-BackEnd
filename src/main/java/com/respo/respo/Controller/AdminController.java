package com.respo.respo.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.respo.respo.Entity.AdminEntity;
import com.respo.respo.Service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	AdminService aserv;
	
	// Create
	@PostMapping("/insertAdmin")
	public AdminEntity insertAdmin(@RequestBody AdminEntity admin) {
		return aserv.insertAdmin(admin);
	}
	
	// Read
	@GetMapping("/getAllAdmins")
	public List<AdminEntity> getAllAdmins() {
		return aserv.getAllAdmins();
	}
	
	// U - Update 
	@PutMapping("/updateAdmin")
    public AdminEntity updateAdmin(@RequestParam int adminId, 
                                   @RequestParam(required = false) String username,
                                   @RequestParam(required = false) String pWord) {
        AdminEntity admin = aserv.getAdminById(adminId);
        if (username != null && !username.isEmpty()) admin.setUsername(username);
        if (pWord != null && !pWord.isEmpty()) admin.setpWord(pWord);

        return aserv.updateAdmin(adminId, admin);
    }
	
	// D - Delete 
	@DeleteMapping("/deleteAdmin/{adminId}")
	public String deleteAdmin(@PathVariable int adminId) {
		return aserv.deleteAdmin(adminId);
	}

	    // Login
        @PostMapping("/login")
        public Map<String, Object> loginAdmin(@RequestBody Map<String, String> loginData) {
            String username = loginData.get("username");
            String password = loginData.get("password");
            int loginResult = aserv.loginAdmin(username, password);
        
            Map<String, Object> response = new HashMap<>();
            if (loginResult == 1) {
                AdminEntity admin = aserv.getAdminByIdentifier(username); // Fetch admin details
                response.put("message", "Login successful");
                response.put("status", "success");
                response.put("adminId", admin.getAdminId()); // Include adminId in the response
            } else {
                response.put("message", "Invalid username or password");
                response.put("status", "failure");
            }
        
            return response;
        }        
}
