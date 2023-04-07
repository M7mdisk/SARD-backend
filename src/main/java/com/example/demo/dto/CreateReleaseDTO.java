package com.example.demo.dto;

import com.example.demo.model.Release;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReleaseDTO {

    private String name;

    private String version;

    private String description;

    private String server_ip;

    private String projectId ;

    private Release.Platform platform;
}
