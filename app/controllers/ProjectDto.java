package controllers;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import commons.JsonPointDeserializer;
import commons.JsonPointSerializer;
import models.Project;
import org.geolatte.geom.Point;

import java.util.UUID;


/**
 * We cannot pass the Project because that's is a hibernate
 * Entity ( which might contain lazy init fields )
 */
public class ProjectDto {
    private UUID id;
    private String name;

    // TODO add zones here

    public ProjectDto(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProjectDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
