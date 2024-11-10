package com.yaksh.user_security.repository;

import com.yaksh.user_security.entity.Faculty;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacultyRepository extends MongoRepository<Faculty, ObjectId> {
    public Faculty findByFacultyId(String id);
}
