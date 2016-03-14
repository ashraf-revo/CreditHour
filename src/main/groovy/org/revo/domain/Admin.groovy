package org.revo.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.NotBlank
import org.revo.service.impl.Util

import javax.persistence.*
import javax.validation.constraints.Min
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/**
 * Created by ashraf on 12/4/2015.
 */
@Entity
class Admin {
    @Id
    @GeneratedValue
    Long id
    @Column(length = 40, unique = true)
    @NotBlank(message = "should not be empty")
    @Size(min = 5, max = 40)
    @Pattern(regexp = "^[A-Za-z0-9_.-]{5,40}\$", message = "please change your name")
    String name
    @Column(length = 40, unique = true)
    @NotBlank(message = "should not be empty")
    @Email()
    @Size(min = 5, max = 40)
    String email
    @Column(length = 60)
    @NotBlank(message = "should not be empty")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 5, max = 60, message = "size between 5 and 60")
    String password
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "admin")
    Set<Student> student
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "admin")
    Set<Term> term
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "admin")
    Set<Subject> subject
    @JsonIgnore
    boolean accountNonExpire = false
    @JsonIgnore
    boolean accountNonLocked = false
    @JsonIgnore
    boolean credentialsNonExpired = false
    @JsonIgnore
    boolean enabled = false
    @JsonIgnore
    @Column(length = 36)
    String activationKey
    @Temporal(TemporalType.DATE)
    Date lastPayment = Util.AddToDate(Util.AddToDate(new Date(), Calendar.YEAR, -1), Calendar.MONTH, +1)
    @Min(value = 1L, message = "min value is 1")
    int plane = 1
    @Temporal(TemporalType.DATE)
    Date createdDate = new Date()

}
