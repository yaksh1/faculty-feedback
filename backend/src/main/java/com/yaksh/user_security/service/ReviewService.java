package com.yaksh.user_security.service;

import com.yaksh.user_security.utils.ResponseDTO;
import com.yaksh.user_security.entity.Faculty;
import com.yaksh.user_security.entity.Review;
import com.yaksh.user_security.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    private final MongoTemplate mongoTemplate;
    public ResponseDTO createReview(String body, String id){
        try{

            Review review = reviewRepository.insert(new Review(body));
            mongoTemplate.update(Faculty.class)
                    .matching(Criteria.where("facultyId").is(id))
                    .apply(new Update().push("reviewIds").value(review))
                    .first();

            return new ResponseDTO(200,"Review created.",review);
        }catch (Exception e){
            return new ResponseDTO(500,"Review not created",e.getMessage());
        }
    }
}
