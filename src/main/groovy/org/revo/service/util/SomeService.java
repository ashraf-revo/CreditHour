package org.revo.service.util;

import org.revo.domain.Student;
import org.revo.domain.Subject;

import java.util.Set;

/**
 * Created by revo on 04/12/15.
 */
public interface SomeService {
    void init();

    void joinTerm(Long student, Long term, Set<Subject> subject);

    Long hours(Student Student);
}
