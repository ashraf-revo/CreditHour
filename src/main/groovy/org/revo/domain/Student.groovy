package org.revo.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.NotBlank

import javax.persistence.*
import javax.validation.constraints.Pattern

/**
 * Created by ashraf on 12/3/2015.
 */
@Entity
class Student {
    @Id
    @GeneratedValue
    Long id
    @Column(length = 40)
    @NotBlank(message = "should not be empty")
    String name
    @Column(length = 40, unique = true)
    @NotBlank(message = "should not be empty")
    @Pattern(regexp = "^[A-Za-z0-9_.-]{5,40}\$", message = "please change your email email must be between 5 and 40 email should not contain @")
    String email
    @Column(length = 60)
    @NotBlank(message = "should not be empty")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    String password
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "student")
    Set<PT> pt = new HashSet<>()
    @ManyToOne
    @JoinColumn
    @JsonIgnore
    Admin admin
}
