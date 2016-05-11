package dto.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import commons.JsonGeometryDeserializer;
import commons.JsonGeometrySerializer;
import models.ZoneType;
import org.geolatte.geom.Polygon;

import java.util.UUID;

public class ZoneApiDto {
    private UUID id;

    @JsonSerialize(using = JsonGeometrySerializer.class)
    @JsonDeserialize(using = JsonGeometryDeserializer.class)
    private Polygon polygon;

    private Integer height;
    private ZoneType type;
    private String name;

    public ZoneApiDto(UUID id, Polygon polygon, Integer height, ZoneType type, String name) {
        this.id = id;
        this.polygon = polygon;
        this.height = height;
        this.type = type;
        this.name = name;
    }

    public ZoneApiDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public ZoneType getType() {
        return type;
    }

    public void setType(ZoneType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
