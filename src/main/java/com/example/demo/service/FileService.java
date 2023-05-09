package com.example.demo.service;

import com.example.demo.model.Release;
import com.example.demo.model.ReleaseFile;
import com.example.demo.model.repository.FileRepository;
import com.example.demo.model.repository.ReleaseRepository;
import com.example.demo.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService {

    private final ReleaseRepository releaseRepository;
    private final Environment environment;

    private Path foundFile;

    // Returns the path to the uploaded file, starting from /uploads
    public String upload(MultipartFile file,String filename) throws IOException {
        String fileDir = environment.getProperty("release.upload.dir","./uploads/");
        String finalLocation =Paths.get(fileDir,filename).toAbsolutePath().normalize().toString();
        file.transferTo(new File(finalLocation));
        return Paths.get(fileDir,filename).toString();
    }

    public String upload(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileDir = environment.getProperty("release.upload.dir","./uploads/");
        String finalLocation =Paths.get(fileDir,filename).toAbsolutePath().normalize().toString();
        file.transferTo(new File(finalLocation));

        return finalLocation;
    }

    public Resource download(String filename) throws IOException {
        foundFile = null;

        String defaultUploadDir = environment.getProperty("release.upload.dir","./uploads/");
        Path uploadDir = Paths.get(Objects.requireNonNull(defaultUploadDir));

        Files.list(uploadDir).forEach(file -> {
            if (file.getFileName().toString().startsWith(filename)) {
                foundFile = file;
                return;
            }
        });

        if (foundFile != null) {
            return new UrlResource(foundFile.toUri());
        }

        return null;
    }
}
