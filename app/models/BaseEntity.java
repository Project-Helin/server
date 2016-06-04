package models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@MappedSuperclass
public class BaseEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updateAt;


    @PreUpdate
    @PrePersist
    private void onPreInsertOrUpdate() {
        if (getCreatedAt() == null) {
            setCreatedAt(LocalDateTime.now());
        }

        setUpdateAt(LocalDateTime.now());
    }

    public UUID getId() {
        return id;
    }

    public String getIdAsString() {
        return id.toString();
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public BaseEntity setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getCreatedAtDateAndTime(){
        return DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(getCreatedAt());
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public BaseEntity setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
        return this;
    }

    /**
     * Recommended way of implementing equals/hashCode is using identifier equality.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseEntity that = (BaseEntity) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return  getClass() + "{" +
            "id=" + id +
            "object-identity=" + super.toString() +
            '}';
    }
}
