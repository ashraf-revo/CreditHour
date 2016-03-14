package org.revo.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.validator.constraints.NotBlank

import javax.persistence.*
import javax.validation.constraints.Min

/**
 * Created by ashraf on 12/3/2015.
 */
@Entity
class Term {
    @Id
    @GeneratedValue
    Long id
    @Column(length = 40)
    @NotBlank(message = "should not be empty")
    String name
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "term")
    @JsonIgnore
    Set<PT> pt = new HashSet<>()
    boolean enabled = true
    @Column(length = 2)
    @Min(value = 1L, message = "min value is 1")
    int maxHour=20
    @Column(length = 2)
    int defaultHour
    @Column(length = 2)
    @Min(value = 1L, message = "min value is 1")
    int minHour=15
    @Temporal(TemporalType.DATE)
    Date CreatedDate = new Date();
    @ManyToOne
    @JoinColumn
    @JsonIgnore
    Admin admin
}
