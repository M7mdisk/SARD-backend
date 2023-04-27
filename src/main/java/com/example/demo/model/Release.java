package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
@Data
public class Release {

    public enum Platform {
        ANDROID,
        IOS
    }

    @Id
    private String id;

    @CreatedDate
    private Date uploaded_at;

    private String name;

    private String version;

    private String description;

    private String server_ip;

    @DocumentReference
    private Project project;


    private Platform platform;

    // TODO: This may not be included in the final version
    public enum Status {
        UPLOADED,
        UNDER_TESTING,
        APPROVED
    }

    private List<ReleaseFile> releaseFilesList;

    private Status status = Status.UPLOADED;
}
