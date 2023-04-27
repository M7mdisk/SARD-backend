package com.example.demo.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Document("release_files")
public class ReleaseFile {
    @Id
    ObjectId id;

    Release.Platform osType;

    @Indexed(unique = true)
    String filename;
}

