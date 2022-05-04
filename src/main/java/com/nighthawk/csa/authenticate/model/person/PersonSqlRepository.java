package com.nighthawk.csa.authenticate.model.person;

import com.nighthawk.csa.authenticate.model.role.Role;
import com.nighthawk.csa.authenticate.model.role.RoleJpaRepository;
import com.nighthawk.csa.authenticate.model.scrum.ScrumSqlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
This class has an instance of Java Persistence API (JPA)
-- @Autowired annotation. Allows Spring to resolve and inject collaborating beans into our bean.
-- Spring Data JPA will generate a proxy instance
-- Below are some CRUD methods that we can use with our database
*/
@Service
@Transactional
public class PersonSqlRepository {

    @Autowired  // Inject PersonJpaRepository
    private PersonJpaRepository jpa;

    @Autowired  // Inject RoleJpaRepository
    private RoleJpaRepository roleJpa;

    @Autowired  // Inject ScrumSqlRepository
    private ScrumSqlRepository scrum_sql;

    @Autowired  // Inject PasswordEncoder
    private PasswordEncoder passwordEncoder;

    public  List<Person>listAll() {
        return jpa.findAll();
    }

    // custom query to find anything containing term in name or email ignoring case
    public  List<Person>listLike(String term) {
        return jpa.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term);
    }

    // custom query to find anything containing term in name or email ignoring case
    public  List<Person>listLikeNative(String term) {
        String like_term = String.format("%%%s%%",term);  // Like required % rappers
        return jpa.findByLikeTermNative(like_term);
    }

    public void save(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        jpa.save(person);
    }

    public Person get(long id) {
        return (jpa.findById(id).isPresent())
                ? jpa.findById(id).get()
                : null;
    }

    public void delete(long id) {
        scrum_sql.member_deleted(id);   // make sure ID is no longer present in SCRUM Teams
        jpa.deleteById(id);
    }

    public Role saveRole(Role role) {
        return roleJpa.save(role);
    }

    public void addRoleToPerson(String email, String roleName) { // by passing in the two strings you are giving the user that certain role
        Person person = jpa.findByEmail(email);
        Role role = roleJpa.findByName(roleName);
        person.getRoles().add(role);
    }
}