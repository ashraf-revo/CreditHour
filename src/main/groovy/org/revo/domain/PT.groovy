package org.revo.domain

import com.fasterxml.jackson.annotation.JsonIgnore

import javax.persistence.*

/**
 * Created by ashraf on 12/3/2015.
 */
@Entity
class PT {
    @Id
    @GeneratedValue
    Long id
    @ManyToOne
    @JoinColumn
    Term term
    @ManyToOne
    @JoinColumn
    @JsonIgnore
    Student student
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "pt")
    Set<PS> ps = new HashSet<>()
}
