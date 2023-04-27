package com.example.demo.model.repository;

import com.example.demo.model.ReleaseFile;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface FileRepository extends MongoRepository<ReleaseFile, ObjectId> {

    @Query(value = "{'filename': ?0}", fields = "{ 'filename': 0}")
    public Optional<ReleaseFile> findByFilename(String filename);
}
