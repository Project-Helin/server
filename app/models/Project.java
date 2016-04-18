package models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(name = "projects")
public class Project extends BaseEntity {

    @Column()
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project", orphanRemoval = true)
    private Set<Zone> zones = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public void setZones(Set<Zone> zones) {
        this.zones = zones;
    }

    public Set<Zone> getZones() {
        return zones;
    }
}
