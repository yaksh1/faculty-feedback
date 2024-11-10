package com.yaksh.user_security.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yaksh.user_security.entity.Faculty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacultyResponse {
    private int status;
    private String message;
   private String error;
    private List<Faculty> faculties;
    private Faculty faculty;

    public FacultyResponse(int status, String message, Faculty faculty) {
        this.status = status;
        this.message = message;
        this.faculty = faculty;
    }

    public FacultyResponse(int status, String message, List<Faculty> faculties) {
        this.status = status;
        this.message = message;
        this.faculties = faculties;
    }

    public FacultyResponse(int status, String message, String error) {
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public FacultyResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
