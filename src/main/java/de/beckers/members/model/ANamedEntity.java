package de.beckers.members.model;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.hibernate.validator.constraints.NotBlank;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class ANamedEntity extends AEntity {
    @NotBlank
    @Size(max = 32)
    private String name;
}
