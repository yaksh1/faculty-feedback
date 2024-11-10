package com.yaksh.user_security.utils;

import com.yaksh.user_security.entity.OurUsers;
import com.yaksh.user_security.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ValidationChecks {
    @Autowired
    private UsersRepo usersRepo;


    public boolean isUserPresent(String email){
        OurUsers user = usersRepo.findByEmail(email).orElse(null);
        return user!=null;
    }

    public boolean isValidEmail(String email) {
        String requiredDomain = "@mitwpu.edu.in"; // only mitwpu students/faculties allowed

        // Check if the email ends with the required domain
        return email != null && email.endsWith(requiredDomain);
    }

    public boolean isValidPassword(String password) {
        int minLength = 8; // minimum length

        return password != null
                && password.length() >= minLength
                && password.matches(".*[A-Z].*") // at least one uppercase letter
                && password.matches(".*[a-z].*") // at least one lowercase letter
                && password.matches(".*\\d.*")   // at least one digit
                && password.matches(".*[!@#$%^&*()-+].*"); // at least one special character
    }

    public boolean isOtpExpired(LocalDateTime userOtpExpiryTime){
        if(userOtpExpiryTime.isBefore(LocalDateTime.now())){
            return true;
        }
        return false;
    }



}
