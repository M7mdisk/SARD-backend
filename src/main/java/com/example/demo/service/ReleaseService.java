package com.example.demo.service;

import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Release;
import com.example.demo.model.repository.ReleaseRepository;
import com.example.demo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReleaseService {
    @Autowired
    ReleaseRepository releaseRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Release> listReleases(){
        return releaseRepo.findAll();
    }

    public List<Release> listReleasesForProject(String projectId){
        // TODO: Filter releases by the given project
        Query query = new Query();
        query.addCriteria(Criteria.where("project.id").is(projectId));
        List<Release> r = mongoTemplate.find(query, Release.class);
        return r;
    }

    public Release createRelease(Release release){
        release.setUploaded_at(new Date());
        return releaseRepo.save(release);
    }

    public Release getReleaseById(String id){
        Optional<Release> u =  releaseRepo.findById(id);
        if (u.isPresent()) return u.get();
        throw new NotFoundException();
    }


    public Release updateRelease(Release r,String userId){
        Release u = getReleaseById(userId);
        Utils.copyNonEmptyProperties(r,u,new String[]{"id"});
        return releaseRepo.save(u);

    }

    public boolean deleteRelease(String usrId){
        Release u = getReleaseById(usrId);
        releaseRepo.delete(u);
        return true;
    }
}
