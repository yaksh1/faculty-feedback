package com.yaksh.user_security.controller;

import com.yaksh.user_security.dto.FacultyResponse;
import com.yaksh.user_security.utils.ResponseDTO;
import com.yaksh.user_security.service.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/faculties")
@RequiredArgsConstructor
public class FacultyController {
    private final FacultyService facultyService;

    @GetMapping
    public ResponseEntity<FacultyResponse> getAllFaculties(){
        FacultyResponse responseDTO = facultyService.getAllFaculties();
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacultyResponse> getFacultyById(@PathVariable String id){
        FacultyResponse responseDTO = facultyService.getById(id);
        return ResponseEntity.ok(responseDTO);
    }
}
