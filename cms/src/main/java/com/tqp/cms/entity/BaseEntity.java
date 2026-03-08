package com.tqp.cms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@MappedSuperclass
@Getter
@Setter
@FilterDef(name = "activeFilter", parameters = @ParamDef(name = "active", type = Boolean.class))
@Filter(name = "activeFilter", condition = "active = :active")
public abstract class BaseEntity {
    @Column(nullable = false,name = "active")
    private boolean active = true;

    public boolean isActive() {
        return active;
    }

}