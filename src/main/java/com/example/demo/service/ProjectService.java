package com.example.demo.service;

import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Project;
import com.example.demo.model.User;
import com.example.demo.model.repository.ProjectRepository;
import com.example.demo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    @Autowired
    ProjectRepository projectRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Project> listProjects(){
        return projectRepo.findAll();
    }

    public Project createProject(Project proj){
        return projectRepo.save(proj);
    }

    public Project getProjectById(String id){
        Optional<Project> u =  projectRepo.findById(id);
        if (u.isPresent()) return u.get();
        throw new NotFoundException();
    }


    public Project updateProject(Project r,String userId){
        Project u = getProjectById(userId);
        Utils.copyNonEmptyProperties(r,u,new String[]{"id"});
        return projectRepo.save(u);

    }

    public boolean deleteProject(String usrId){
        Project u = getProjectById(usrId);
        projectRepo.delete(u);
        return true;
    }

    public List<Project> listProjectsforUser(User usr) {
        Query query = new Query();
        query.addCriteria(Criteria.where("participants").is(usr));
        query.fields().exclude("participants");
        return mongoTemplate.find(query,Project.class);
    }
}
