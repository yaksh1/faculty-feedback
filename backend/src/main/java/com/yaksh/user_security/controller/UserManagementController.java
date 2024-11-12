package com.yaksh.user_security.controller;

import com.yaksh.user_security.dto.ReqRes;
import com.yaksh.user_security.entity.ChangePassword;
import com.yaksh.user_security.entity.OurUsers;
import com.yaksh.user_security.entity.VerifyUser;
import com.yaksh.user_security.service.UsersManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserManagementController {
    @Autowired
    private UsersManagementService usersManagementService;

    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> register(@RequestBody ReqRes reg){
        if(reg.getRole()==null||reg.getRole().isBlank()){
            reg.setRole("USER");
        }
        return ResponseEntity.ok(usersManagementService.register(reg));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes req){
        return ResponseEntity.ok(usersManagementService.login(req));
    }

    @PostMapping("/auth/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestBody ChangePassword body){
        return ResponseEntity.ok(usersManagementService.forgotPassword(body));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes req){
        return ResponseEntity.ok(usersManagementService.refreshToken(req));
    }

    @PostMapping("/auth/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUser user){
        return ResponseEntity.ok(usersManagementService.verifyUser(user));
    }

    @PostMapping("/auth/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email){
        return ResponseEntity.ok(usersManagementService.resendVerification(email));
    }

    @GetMapping("/user/get-all-users")
    public ResponseEntity<ReqRes> getAllUsers(){
        return ResponseEntity.ok(usersManagementService.getAllUsers());

    }

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<ReqRes> getUSerByID(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.getUsersById(userId));

    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Integer userId, @RequestBody OurUsers reqres){
        return ResponseEntity.ok(usersManagementService.updateUser(userId, reqres));
    }

    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<ReqRes> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        ReqRes response = usersManagementService.getMyInfo(email);
        return  ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete")
    public ResponseEntity<ReqRes> deleteUSer(@RequestParam String email){
        return ResponseEntity.ok(usersManagementService.deleteUser(email));
    }


}
