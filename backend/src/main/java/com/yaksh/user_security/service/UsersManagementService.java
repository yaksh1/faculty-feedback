package com.yaksh.user_security.service;

import com.yaksh.user_security.Exception.*;
import com.yaksh.user_security.dto.ReqRes;
import com.yaksh.user_security.entity.ChangePassword;
import com.yaksh.user_security.entity.OurUsers;
import com.yaksh.user_security.entity.VerifyUser;
import com.yaksh.user_security.repository.UsersRepo;
import com.yaksh.user_security.utils.JWTUtils;
import com.yaksh.user_security.utils.ValidationChecks;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UsersManagementService {

    private final ValidationChecks validationChecks;
    @Autowired
    private UsersRepo usersRepo;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;


    public ReqRes register(ReqRes registrationRequest){
        ReqRes resp = new ReqRes();
        try {
//            if(!validationChecks.isValidEmail(registrationRequest.getEmail())){
//                throw new CustomValidationException(
//                        registrationRequest.getEmail()+" is an invalid email, you must be a registered student with MITWPU to enter the site."
//                        , ErrorCode.INVALID_EMAIL);
//            }
            if(validationChecks.isUserPresent(registrationRequest.getEmail())){
                throw new CustomValidationException("User already exists with email: " + registrationRequest.getEmail()
                        , ErrorCode.USER_ALREADY_EXISTS);
            }
            if(!validationChecks.isValidPassword(registrationRequest.getPassword())){
                throw new CustomValidationException(
                        "Password must be at least 8 characters long and contain an uppercase letter, lowercase letter, a digit, and a special character."
                        , ErrorCode.INVALID_PASSWORD);
            }
            OurUsers ourUser = new OurUsers();
            // generate verification token for user
            ourUser.setVerificationCode(generateVerificationCode());
            // set expiry of token to 15 minutes
            ourUser.setVerificationExpiresAt(LocalDateTime.now().plusMinutes(15));
            // account is not enabled yet
            ourUser.setEnabled(false);
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setRole(registrationRequest.getRole());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            OurUsers ourUsersResult = usersRepo.save(ourUser);
            sendVerificationEmail(ourUser);
            if (ourUsersResult.getId()>0) {
                resp.setOurUsers((ourUsersResult));
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }
        }
        catch (CustomValidationException e){
            throw e;
        }
        catch (Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public ReqRes verifyUser(VerifyUser verifyUser){
        ReqRes response = new ReqRes();
        Optional<OurUsers> optionalUser = usersRepo.findByEmail(verifyUser.getEmail());
        if(optionalUser.isPresent()){
            OurUsers user = optionalUser.get();
            if(validationChecks.isOtpExpired(user.getVerificationExpiresAt())){
                throw new CustomValidationException("Your code is expired, please request for a new code",ErrorCode.OTP_EXPIRED);
            }
            if(user.getVerificationCode().equals(verifyUser.getOtp())){
                user.setEnabled(true);
                user.setVerificationExpiresAt(null);
                user.setVerificationCode(null);
                usersRepo.save(user);
                response.setMessage("Account verified.");
                response.setStatusCode(200);

                return response;
            }else{
                throw new CustomValidationException("Invalid Verification Code,please try again.",ErrorCode.INVALID_OTP);
            }
        }else{
            throw new CustomValidationException("This account does not exists.",ErrorCode.USER_NOT_FOUND);
        }
    }

    public ReqRes resendVerification(String email){
        ReqRes resp = new ReqRes();
        Optional<OurUsers> optionalUser = usersRepo.findByEmail(email);
        if(optionalUser.isPresent()){
            OurUsers user = optionalUser.get();
            if(user.isEnabled()){
                throw new CustomValidationException("User is Already Verified",ErrorCode.USER_ALREADY_VERIFIED);
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationExpiresAt(LocalDateTime.now().plusMinutes(15));
            sendVerificationEmail(user);
            OurUsers OurUser = usersRepo.save(user);
            if(OurUser.getId()>0){
                resp.setOurUsers(OurUser);
                resp.setMessage("Email Verification code sent to email id: "+ email);
                resp.setStatusCode(200);
            }
            return resp;

        }else{
            throw new CustomValidationException("This account does not exists.",ErrorCode.USER_NOT_FOUND);
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000)+100000;
        return String.valueOf(code);
    }

    private void sendVerificationEmail(OurUsers user) {
        String subject = "Account verification code";
        String otp = user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + otp + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendEmailVerification(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }

    public ReqRes login(ReqRes loginRequest){
        ReqRes response = new ReqRes();
        try {

            if(!validationChecks.isUserPresent(loginRequest.getEmail())){
                throw new CustomValidationException("User Not Found. Please check your email.",ErrorCode.USER_NOT_FOUND);
            }
            OurUsers users = usersRepo.findByEmail(loginRequest.getEmail()).orElse(null);
            if(!users.isEnabled()){
                throw new CustomValidationException("Account is not verified,please verify your account.",ErrorCode.ACCOUNT_NOT_VERIFIED);
            }
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));

            var user = usersRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

        }
        catch (CustomValidationException e){
            throw e;
        }
        catch (AuthenticationException e){
            throw e;
        }
        catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public ReqRes forgotPassword(ChangePassword body) {
        ReqRes response = new ReqRes();

        try{
            if(!validationChecks.isUserPresent(body.getEmail())){
                throw new CustomValidationException(
                        "Account does not exists.",
                        ErrorCode.USER_NOT_FOUND);
            }
//            if(!validationChecks.isValidEmail(body.getEmail())){
//                throw new CustomValidationException(
//                        "Email is not valid, please check your email.",
//                        ErrorCode.INVALID_EMAIL);
//            }
            if(!validationChecks.isValidPassword(body.getPassword())){
                throw new CustomValidationException("Password must be at least 8 characters long and contain an uppercase letter, lowercase letter, a digit, and a special character."
                        , ErrorCode.INVALID_PASSWORD);
            }
            OurUsers user = usersRepo.findByEmail(body.getEmail()).orElse(null);
            if(!user.isEnabled()){
                throw new CustomValidationException("Account is not verified,please verify your account.",ErrorCode.USER_ALREADY_VERIFIED);
            }
            String encoded_password=passwordEncoder.encode(body.getPassword());
            user.setPassword(encoded_password);
            usersRepo.save(user);
            response.setStatusCode(200);
            response.setMessage("Password Successfully changed");
            return response;
        }catch (CustomValidationException e){
            throw e;
        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage("error: "+e.getMessage());
            return response;
        }
    }


    public ReqRes refreshToken(ReqRes refreshTokenReqiest){
        ReqRes response = new ReqRes();
        try{
            String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
            OurUsers users = usersRepo.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenReqiest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
            response.setStatusCode(200);
            return response;

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }


    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();

        try {
            List<OurUsers> result = usersRepo.findAll();
            if (!result.isEmpty()) {
                reqRes.setOurUsersList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }


    public ReqRes getUsersById(Integer id) {
        ReqRes reqRes = new ReqRes();
        try {
            OurUsers usersById = usersRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            reqRes.setOurUsers(usersById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("Users with id '" + id + "' found successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }


    public ReqRes deleteUser(String email) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findByEmail(email);
            if (userOptional.isPresent()) {
                usersRepo.deleteById(userOptional.get().getId());
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes updateUser(Integer userId, OurUsers updatedUser) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                OurUsers existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setRole(updatedUser.getRole());

                // Check if password is present in the request
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    // Encode the password and update it
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                OurUsers savedUser = usersRepo.save(existingUser);
                reqRes.setOurUsers(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return reqRes;
    }


    public ReqRes getMyInfo(String email){
        ReqRes reqRes = new ReqRes();
        try {
            Optional<OurUsers> userOptional = usersRepo.findByEmail(email);
            if (userOptional.isPresent()) {
                reqRes.setOurUsers(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }

        }catch (Exception e){
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return reqRes;

    }


}
