package com.enterprise.eakip.core.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "publishers")
@SQLDelete(sql = "UPDATE publishers SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Publisher extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 150)
    private String name;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private boolean deleted = false;
}
