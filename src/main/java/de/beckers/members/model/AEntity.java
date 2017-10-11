package de.beckers.members.model;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@MappedSuperclass
@Data
public abstract class AEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Size(max = 36)
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date createTS;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date updateTS;
    
    @PrePersist
    public void prePersist() {
        createTS = new Date();
        updateTS = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        updateTS = new Date();
    }
}
