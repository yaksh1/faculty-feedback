package com.yaksh.user_security.service;

import com.yaksh.user_security.Exception.CustomValidationException;
import com.yaksh.user_security.Exception.ErrorCode;
import com.yaksh.user_security.entity.OurUsers;
import com.yaksh.user_security.google.PerspectiveApiService;
import com.yaksh.user_security.repository.UsersRepo;
import com.yaksh.user_security.utils.ResponseDTO;
import com.yaksh.user_security.entity.Faculty;
import com.yaksh.user_security.entity.Review;
import com.yaksh.user_security.repository.ReviewRepository;
import com.yaksh.user_security.utils.ValidationChecks;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final PerspectiveApiService perspectiveApiService;
    private final UsersRepo usersRepo;
    private final MongoTemplate mongoTemplate;

    private final ValidationChecks validationChecks;
    public ResponseDTO createReview(String body, String id, String email){
        try{
            OurUsers user = usersRepo.findByEmail(email).orElse(null);
            if(user.getBannedTillDate()!=null && LocalDateTime.now().isBefore(user.getBannedTillDate())){
                // Define the formatter with date and time in AM/PM format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
                throw new CustomValidationException("Your account is banned till "+ user.getBannedTillDate().format(formatter) +".",
                        ErrorCode.ACCOUNT_BANNED);
            }
            boolean isInAppropriate = perspectiveApiService.isInappropriateReview(body);
            if(isInAppropriate){

                if(user!=null){
                    user.setStrikes(user.getStrikes()+1);
                    usersRepo.save(user);

                    if(user.getStrikes()%3==0){
                        if(user.getBannedTillDate()==null){
                            user.setBannedDays(1);
                            user.setBannedTillDate(LocalDateTime.now().plusDays(user.getBannedDays()));
                        }else {
                            int oldBannedDays = user.getBannedDays();
                            int newBannedDays = oldBannedDays * 2;
                            user.setBannedTillDate(LocalDateTime.now().plusDays(newBannedDays));
                            user.setBannedDays(newBannedDays);
                        }
                        usersRepo.save(user);
                        throw new CustomValidationException(
                                "Your Account is banned for "+user.getBannedDays() +" days. Try submitting reviews after your ban period",
                                ErrorCode.ACCOUNT_BANNED);
                    }
                }
               throw new CustomValidationException(
                       "Your review is in appropriate, please change it. " +
                               "After 3 such reviews you will be banned for few days.",
                       ErrorCode.INAPPROPRIATE_REVIEW);
            }
            if(!validationChecks.isReviewLongEnough(body)){
                throw new CustomValidationException("Review is too short, please edit.",ErrorCode.REVIEW_TOO_SHORT);
            }
            Review review = reviewRepository.insert(new Review(body));
            mongoTemplate.update(Faculty.class)
                    .matching(Criteria.where("facultyId").is(id))
                    .apply(new Update().push("reviewIds").value(review))
                    .first();

            return new ResponseDTO(200,"Review created.",review);
        }
        catch (CustomValidationException e){
            throw e;
        }
        catch (Exception e){
            return new ResponseDTO(500,"Review not created",e.getMessage());
        }
    }
}
