package com.example.demo.controller;

import com.example.demo.service.FileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/file")
public class FileController {

    private final FileService fileService;

    Logger logger = LoggerFactory.getLogger(FileController.class);

    @ResponseBody
    @RequestMapping(value = "/{filename}", method = GET)
    public ResponseEntity<Object> download(@PathVariable("filename") String filename) {
        try {
            Resource resource = fileService.download(filename);

            String contentType = "application/octet-stream";
            String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .build();
        } catch (NoSuchElementException | IOException exception) {
            return new ResponseEntity("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("upload")
    @ResponseBody
    public ResponseEntity<Object> upload(@RequestParam(name = "file") MultipartFile file) {
        try {
            if (file.getSize() == 0 || file.getOriginalFilename() == null) {
                throw new NoSuchElementException("File is missing");
            }

            if (file.getSize() > 15776) {
                throw new IOException("File exceeds max allowed size");
            }

            fileService.upload(file);

            return ResponseEntity.ok("File uploaded successfully: Here is the url?");
        } catch (NoSuchElementException | IOException exception) {
            return new ResponseEntity("File uploaded successfully: Here is the url?",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

