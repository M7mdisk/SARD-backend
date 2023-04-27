package com.example.demo.service;

import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Release;
import com.example.demo.model.ReleaseFile;
import com.example.demo.model.repository.FileRepository;
import com.example.demo.model.repository.ReleaseRepository;
import com.example.demo.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReleaseService {
    @Autowired
    ReleaseRepository releaseRepo;

    @Autowired
    FileService fileService;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Release> listReleases(){
        return releaseRepo.findAll();
    }

    @Autowired
    private FileRepository fileRepository;

    public ReleaseFile uploadReleaseFile(MultipartFile f, Release r) throws IOException {
        Release.Platform osType = r.getPlatform();

        String filename = StringUtils.cleanPath(Objects.requireNonNull(f.getOriginalFilename()));
        String[] split = filename.split("\\.");
        String newFilename = split[0] + "_" + Utils.randomString(8) + "." + split[1];
        String finalLocation  = fileService.upload(f,newFilename);

        ReleaseFile releaseFile = ReleaseFile.builder().filename(finalLocation).osType(osType).build();

        return fileRepository.save(releaseFile);
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
