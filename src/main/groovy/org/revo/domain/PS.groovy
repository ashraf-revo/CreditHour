package org.revo.domain

import com.fasterxml.jackson.annotation.JsonIgnore

import javax.persistence.*
import javax.validation.constraints.Min

/**
 * Created by ashraf on 11/22/2015.
 */
@Entity
class PS {
    @Id
    @GeneratedValue
    Long id
    @Column(length = 3)
    @Min(value = 0L,message = "min value is 0")
    int grade
    @OneToOne
    @JoinColumn
    Subject subject
    @ManyToOne
    @JoinColumn
    @JsonIgnore
    PT pt
    @Column(length = 2)
    @Enumerated
    State state = State.non
}
