package com.yaksh.user_security.controller;

import com.yaksh.user_security.utils.ResponseDTO;
import com.yaksh.user_security.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    @PostMapping
    public ResponseEntity<ResponseDTO> createReview(@RequestParam String reviewBody, @RequestParam String facultyId){
        ResponseDTO responseDTO = reviewService.createReview(reviewBody,facultyId);
        if(responseDTO.getStatus()==200){
            return ResponseEntity.ok(responseDTO);
        }
        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
