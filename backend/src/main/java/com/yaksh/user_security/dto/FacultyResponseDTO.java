package com.yaksh.user_security.dto;

import com.yaksh.user_security.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyResponseDTO {
    private ObjectId id;
    private String name;
    private String position;
    private String image;
    private List<Review> reviewIds;
}
