package org.revo.domain

import groovy.transform.Canonical

/**
 * Created by revo on 3/8/16.
 */
@Canonical
class GradeStudents {
    Long TERM_ID
    String STUDENT_EMAIL
    Long SUBJECT_ID
    int GRADE
    State STATE
}
