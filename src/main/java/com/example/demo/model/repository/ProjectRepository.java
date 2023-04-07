package com.example.demo.model.repository;

import com.example.demo.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProjectRepository extends MongoRepository<Project,String> {
}
