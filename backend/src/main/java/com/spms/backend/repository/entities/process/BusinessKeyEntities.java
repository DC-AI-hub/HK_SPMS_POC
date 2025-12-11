package com.spms.backend.repository.entities.process;


import com.spms.backend.repository.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "spms_business_key",
        uniqueConstraints =  {
         @UniqueConstraint(name = "spms_uq_prefix_seq",columnNames = { "prefix","seq"})
        })

public class BusinessKeyEntities extends BaseEntity {

    @Column
    private String prefix;

    @Column
    private long seq;

    @Column
    private String occupiedBy;

    @Column
    public Long occupiedDate;

    @Column
    private String target;

}
