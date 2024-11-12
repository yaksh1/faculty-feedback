package com.yaksh.user_security.service;

import com.yaksh.user_security.Exception.CustomValidationException;
import com.yaksh.user_security.Exception.ErrorCode;
import com.yaksh.user_security.dto.FacultyResponse;
import com.yaksh.user_security.utils.ResponseDTO;
import com.yaksh.user_security.entity.Faculty;
import com.yaksh.user_security.repository.FacultyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacultyService {
    private final FacultyRepository facultyRepository;
    public FacultyResponse getAllFaculties(){
        FacultyResponse facultyResponse = new FacultyResponse();
        try{
            List<Faculty> faculties= facultyRepository.findAll();

            if(faculties.isEmpty()){
                throw new CustomValidationException("Data is empty", ErrorCode.DATA_NOT_FOUND);
            }
            facultyResponse.setFaculties(faculties);
            facultyResponse.setMessage("Data fetched successfully");
            facultyResponse.setStatus(200);
            return facultyResponse;
        }
        catch (CustomValidationException e){
            throw e;
        }
        catch (Exception e){
            return new FacultyResponse(500,"Data fetching failed",e.getMessage());
        }
    }

    public FacultyResponse getById(String id) {
        try{
            Faculty faculty= facultyRepository.findByFacultyId(id);
            if(faculty==null){
                throw new CustomValidationException("Faculty not found with id: "+ id,ErrorCode.DATA_NOT_FOUND);
            }
            return new FacultyResponse(200,"Data fetched.",faculty);
        }
        catch (CustomValidationException e){
            throw e;
        }
        catch (Exception e){
            return new FacultyResponse(500,"Data fetching failed",e.getMessage());
        }
    }
}
