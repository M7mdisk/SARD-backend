package com.example.demo.model.repository;

import com.example.demo.model.Release;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReleaseRepository extends MongoRepository<Release,String> {
}
