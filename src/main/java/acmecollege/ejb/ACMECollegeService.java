/********************************************************************************************************2*4*w*
 * File:  ACMEColegeService.java
 * Course materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * Updated by:  Group NN
 *   studentId, firstName, lastName (as from ACSIS)
 *   041075438, Krish, Patel (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *
 */
package acmecollege.ejb;

import static acmecollege.entity.StudentClub.ALL_STUDENT_CLUBS_QUERY_NAME;
import static acmecollege.entity.StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_NAME;
import static acmecollege.entity.StudentClub.IS_DUPLICATE_QUERY_NAME;
import static acmecollege.entity.Student.ALL_STUDENTS_QUERY_NAME;
import static acmecollege.utility.MyConstants.DEFAULT_KEY_SIZE;
import static acmecollege.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static acmecollege.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static acmecollege.utility.MyConstants.DEFAULT_SALT_SIZE;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PREFIX;
import static acmecollege.utility.MyConstants.PARAM1;
import static acmecollege.utility.MyConstants.PROPERTY_ALGORITHM;
import static acmecollege.utility.MyConstants.PROPERTY_ITERATIONS;
import static acmecollege.utility.MyConstants.PROPERTY_KEY_SIZE;
import static acmecollege.utility.MyConstants.PROPERTY_SALT_SIZE;
import static acmecollege.utility.MyConstants.PU_NAME;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.entity.ClubMembership;
import acmecollege.entity.Course;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.PeerTutor;
import acmecollege.entity.PeerTutorRegistration;
import acmecollege.entity.SecurityRole;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;

@SuppressWarnings("unused")

/**
 * Stateless Singleton EJB Bean - ACMECollegeService
 */
@Singleton
public class ACMECollegeService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;

    //Student
    public List<Student> getAllStudents() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Student> cq = cb.createQuery(Student.class);
        cq.select(cq.from(Student.class));
        return em.createQuery(cq).getResultList();
    }
    
    public Student getStudentById(int id) {
        return em.find(Student.class, id);
    }

    @Transactional
    public Student persistStudent(Student newStudent) {
        em.persist(newStudent);
        return newStudent;
    }

    @Transactional
    public void buildUserForNewStudent(Student newStudent) {
        SecurityUser userForNewStudent = new SecurityUser();
        userForNewStudent.setUsername(
            DEFAULT_USER_PREFIX + "_" + newStudent.getFirstName() + "." + newStudent.getLastName());
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALT_SIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEY_SIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewStudent.setPwHash(pwHash);
        userForNewStudent.setStudent(newStudent);
        SecurityRole userRole = em.createNamedQuery("SecurityRole.findByRoleName", SecurityRole.class)
                .setParameter(PARAM1, USER_ROLE)
                .getSingleResult(); 
        /* TODO ACMECS01 - Use NamedQuery on SecurityRole to find USER_ROLE */
        userForNewStudent.getRoles().add(userRole);
        userRole.getUsers().add(userForNewStudent);
        em.persist(userForNewStudent);
    }
    
    /**
     * To update a student
     * 
     * @param id - id of entity to update
     * @param studentWithUpdates - entity with updated information
     * @return Entity with updated information
     */
    @Transactional
    public Student updateStudentById(int id, Student studentWithUpdates) {
        Student studentToBeUpdated = getStudentById(id);
        if (studentToBeUpdated != null) {
            em.refresh(studentToBeUpdated);
            em.merge(studentWithUpdates);
            em.flush();
        }
        return studentToBeUpdated;
    }

    /**
     * To delete a student by id
     * 
     * @param id - student id to delete
     */
    @Transactional
    public void deleteStudentById(int id) {
        Student student = getStudentById(id);
        if (student != null) {
            em.refresh(student);
            
            try {
            TypedQuery<SecurityUser> findUser = 
            		em.createNamedQuery("SecurityRole.findUsersByStudentId", SecurityUser.class)
                    .setParameter(PARAM1, id);
            SecurityUser sUser = findUser.getSingleResult();
            
                /* TODO ACMECS02 - Use NamedQuery on SecurityRole to find this related Student
                   so that when we remove it, the relationship from SECURITY_USER table
                   is not dangling
                */
            // Disassociate the SecurityUser from their SecurityRoles
            Set<SecurityRole> roles = new HashSet<>(sUser.getRoles()); // Make a copy to avoid ConcurrentModificationException
            for (SecurityRole role : roles) {
                sUser.getRoles().remove(role);
                role.getUsers().remove(sUser);
                // Depending on your persistence context, you may need to merge these changes
                em.merge(role);
            }
            
            em.remove(sUser);
            
            } catch (NoResultException e) {
                // Handle the case where no SecurityUser is found. 
            	LOG.error("No SecurityUser found for Student with ID: " + id, e);
            } catch (NonUniqueResultException e) {
                // Handle the case where more than one SecurityUser is found. 
            	LOG.error("Multiple SecurityUser instances found for Student with ID: " + id, e);
            }

            em.remove(student);
        }
    }
    
    //PeerTutor
    public List<PeerTutor> getAllPeerTutors() {
        return em.createNamedQuery("PeerTutor.findAll", PeerTutor.class).getResultList();
    }
    
    @Transactional
    public PeerTutor persistPeerTutor(PeerTutor newPeerTutor) {
        em.persist(newPeerTutor);
        em.flush();
        return newPeerTutor; 
    }
    
    public PeerTutor getPeerTutorById(int id) {
        return em.find(PeerTutor.class, id);
    }

    @Transactional
    public PeerTutor updatePeerTutor(int id, PeerTutor updatedPeerTutor) {
        PeerTutor peerTutor = em.find(PeerTutor.class, id);
        if (peerTutor != null) {
            peerTutor.setFirstName(updatedPeerTutor.getFirstName());
            peerTutor.setLastName(updatedPeerTutor.getLastName());
            peerTutor.setProgram(updatedPeerTutor.getProgram());
            em.merge(peerTutor);
        }
        return peerTutor;
    }
    
    @Transactional
    public void deletePeerTutor(int id) {
        PeerTutor peerTutor = em.find(PeerTutor.class, id);
        if (peerTutor != null) {
            em.remove(peerTutor);
        }
    }

    public boolean isDuplicatePeerTutor(String firstName, String lastName, String program) {
        Long count = em.createNamedQuery("PeerTutor.isDuplicate", Long.class)
                       .setParameter("param1", firstName)
                       .setParameter("param2", lastName)
                       .setParameter("param3", program)
                       .getSingleResult();
        return count > 0;
    }

    public PeerTutor findPeerTutorByNameAndProgram(String firstName, String lastName, String program) {
        try {
            return em.createNamedQuery("PeerTutor.findByNameProgram", PeerTutor.class)
                     .setParameter("param1", firstName)
                     .setParameter("param2", lastName)
                     .setParameter("param3", program)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    @Transactional
    public PeerTutorRegistration setPeerTutorForStudentCourse(int studentId, int courseId, PeerTutor newPeerTutor) {
        // Find the student and the course
        Student student = em.find(Student.class, studentId);
        Course course = em.find(Course.class, courseId);

        if (student == null || course == null) {
            throw new EntityNotFoundException("Student or Course not found");
        }

        // Find or create a new PeerTutorRegistration
        PeerTutorRegistration registration = findPeerTutorRegistration(studentId, courseId);
        if (registration == null) {
            registration = new PeerTutorRegistration();
            registration.setStudent(student);
            registration.setCourse(course);
            registration.setPeerTutor(newPeerTutor);  // Assuming newPeerTutor is managed or to be persisted
            em.persist(registration);
        } else {
            // If a peer tutor is already assigned, update the peer tutor details
            if (registration.getPeerTutor() != null) {
                PeerTutor existingPeer = registration.getPeerTutor();
                existingPeer.setFirstName(newPeerTutor.getFirstName());
                existingPeer.setLastName(newPeerTutor.getLastName());
                existingPeer.setProgram(newPeerTutor.getProgram());
                em.merge(existingPeer);
            } else {
                // Assign new peer tutor
                registration.setPeerTutor(newPeerTutor); // Assuming newPeerTutor is managed or to be persisted
                em.merge(registration);
            }
        }
        return registration;
    }

    private PeerTutorRegistration findPeerTutorRegistration(int studentId, int courseId) {
        // Assuming there is a named query in PeerTutorRegistration entity for this
        try {
            return em.createNamedQuery("PeerTutorRegistration.findByStudentAndCourse", PeerTutorRegistration.class)
                     .setParameter("studentId", studentId)
                     .setParameter("courseId", courseId)
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }



//    @Transactional
//    public PeerTutor setPeerTutorForStudentCourse(int studentId, int courseId, PeerTutor newPeerTutor) {
//    	Student studentToBeUpdated = em.find(Student.class, studentId);
//        if (studentToBeUpdated != null) { // Student exists
//            Set<PeerTutorRegistration> peerTutorRegistrations = studentToBeUpdated.getPeerTutorRegistrations();
//            peerTutorRegistrations.forEach(pt -> {
//                if (pt.getCourse().getId() == courseId) {
//                    if (pt.getPeerTutor() != null) { // PeerTutor exists
//                        PeerTutor peer = em.find(PeerTutor.class, pt.getPeerTutor().getId());
//                        peer.setPeerTutor(newPeerTutor.getFirstName(),
//                        				  newPeerTutor.getLastName(),
//                        				  newPeerTutor.getProgram());
//                        em.merge(peer);
//                    }
//                    else { // PeerTutor does not exist
//                        pt.setPeerTutor(newPeerTutor);
//                        em.merge(studentToBeUpdated);
//                    }
//                }
//            });
//            return newPeerTutor;
//        }
//        else return null;  // Student doesn't exists
//    }
    
    //Student Club
    public List<StudentClub> getAllStudentClubs() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StudentClub> cq = cb.createQuery(StudentClub.class);
        cq.select(cq.from(StudentClub.class));
        return em.createQuery(cq).getResultList();
    }

    // Why not use the build-in em.find?  The named query SPECIFIC_STUDENT_CLUB_QUERY_NAME
    // includes JOIN FETCH that we cannot add to the above API
    public StudentClub getStudentClubById(int id) {
        TypedQuery<StudentClub> specificStudentClubQuery = em.createNamedQuery(SPECIFIC_STUDENT_CLUB_QUERY_NAME, StudentClub.class);
        specificStudentClubQuery.setParameter(PARAM1, id);
        return specificStudentClubQuery.getSingleResult();
    }

    @Transactional
    public StudentClub deleteStudentClub(int id) {
        //StudentClub sc = getStudentClubById(id);
    	StudentClub sc = getById(StudentClub.class, StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_NAME, id);
        if (sc != null) {
            Set<ClubMembership> memberships = sc.getClubMemberships();
            List<ClubMembership> list = new LinkedList<>();
            memberships.forEach(list::add);
            list.forEach(m -> {
                if (m.getCard() != null) {
                    MembershipCard mc = getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME, m.getCard().getId());
                    mc.setClubMembership(null);
                }
                m.setCard(null);
                em.merge(m);
            });
            em.remove(sc);
            return sc;
        }
        return null;
    }
    
    // Please study & use the methods below in your test suites
    public boolean isDuplicated(StudentClub newStudentClub) {
        TypedQuery<Long> allStudentClubsQuery = em.createNamedQuery(IS_DUPLICATE_QUERY_NAME, Long.class);
        allStudentClubsQuery.setParameter(PARAM1, newStudentClub.getName());
        return (allStudentClubsQuery.getSingleResult() >= 1);
    }

    @Transactional
    public StudentClub persistStudentClub(StudentClub newStudentClub) {
        em.persist(newStudentClub);
        return newStudentClub;
    }

    @Transactional
    public StudentClub updateStudentClub(int id, StudentClub updatingStudentClub) {
    	StudentClub studentClubToBeUpdated = getStudentClubById(id);
        if (studentClubToBeUpdated != null) {
            em.refresh(studentClubToBeUpdated);
            studentClubToBeUpdated.setName(updatingStudentClub.getName());
            em.merge(studentClubToBeUpdated);
            em.flush();
        }
        return studentClubToBeUpdated;
    }
    
    //Membership Card
    
    public List<MembershipCard> getAllMembershipCards() {
        LOG.debug("Fetching all membership cards from the database.");
        return em.createQuery("SELECT m FROM MembershipCard m", MembershipCard.class).getResultList();
    }
  
    @Transactional
    public MembershipCard persistMembershipCard(MembershipCard newMembershipCard) {
        em.persist(newMembershipCard);
        return newMembershipCard;
    }
    public MembershipCard getMembershipCardById(int cardId) {
        return em.find(MembershipCard.class, cardId);
    }
    @Transactional
    public boolean deleteMembershipCard(int cardId) {
        MembershipCard card = em.find(MembershipCard.class, cardId);
        if (card != null) {
            em.remove(card);
            return true;
        }
        return false;
    }

    //Courses
    @Transactional
    public Course findCourseByCode(String courseCode) {
        try {
            TypedQuery<Course> query = em.createNamedQuery("Course.findByCode", Course.class);
            query.setParameter("courseCode", courseCode);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    @Transactional
    public Course persistCourse(Course newCourse) {
        em.persist(newCourse);
        return newCourse;
    }
    public Course getCourseById(int courseId) {
        return em.find(Course.class, courseId);
    }

    @Transactional
    public Course updateCourse(int id, Course updatedCourse) {
        Course course = em.find(Course.class, id);
        if (course != null) {
            course.setCourseCode(updatedCourse.getCourseCode());
            course.setCourseTitle(updatedCourse.getCourseTitle());
            course.setCreditUnits(updatedCourse.getCreditUnits());
            course.setYear(updatedCourse.getYear());
            course.setSemester(updatedCourse.getSemester());
            em.merge(course);
            return course;
        }
        return null;
    }
    @Transactional
    public boolean deleteCourse(int courseId) {
        Course course = em.find(Course.class, courseId);
        if (course != null) {
            em.remove(course);
            return true; // Successful deletion
        }
        return false; // Course not found
    }

    //PeerTutor Registrations
    @Transactional
    public PeerTutorRegistration persistPeerTutorRegistration(PeerTutorRegistration newPeerTutorRegistration) {
        em.persist(newPeerTutorRegistration);
        em.flush();  // Ensure changes are committed immediately
        return newPeerTutorRegistration;
    }
    public PeerTutorRegistration getPeerTutorRegistrationById(Long registrationId) {
        return em.find(PeerTutorRegistration.class, registrationId);
    }
    @Transactional
    public boolean deletePeerTutorRegistration(Long registrationId) {
        PeerTutorRegistration registration = em.find(PeerTutorRegistration.class, registrationId);
        if (registration != null) {
            em.remove(registration);
            return true;
        }
        return false;
    }
    @Transactional
    public PeerTutorRegistration updatePeerTutorRegistration(Long registrationId, PeerTutorRegistration updatedPeerTutorRegistration) {
        PeerTutorRegistration existingRegistration = em.find(PeerTutorRegistration.class, registrationId);
        if (existingRegistration != null) {
            existingRegistration.setPeerTutor(updatedPeerTutorRegistration.getPeerTutor());
            existingRegistration.setStudent(updatedPeerTutorRegistration.getStudent());
            existingRegistration.setCourse(updatedPeerTutorRegistration.getCourse());
            em.merge(existingRegistration);
            return existingRegistration;
        }
        return null;
    }

    //Clubmemberships   
    @Transactional
    public ClubMembership persistClubMembership(ClubMembership newClubMembership) {
        em.persist(newClubMembership);
        return newClubMembership;
    }

    public ClubMembership getClubMembershipById(int cmId) {
        TypedQuery<ClubMembership> allClubMembershipQuery = em.createNamedQuery(ClubMembership.FIND_BY_ID, ClubMembership.class);
        allClubMembershipQuery.setParameter(PARAM1, cmId);
        return allClubMembershipQuery.getSingleResult();
    }

    @Transactional
    public ClubMembership updateClubMembership(int id, ClubMembership clubMembershipWithUpdates) {
    	ClubMembership clubMembershipToBeUpdated = getClubMembershipById(id);
        if (clubMembershipToBeUpdated != null) {
            em.refresh(clubMembershipToBeUpdated);
            em.merge(clubMembershipWithUpdates);
            em.flush();
        }
        return clubMembershipToBeUpdated;
    }

    public boolean isClubMembershipDuplicated(ClubMembership clubMembership) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(cm) FROM ClubMembership cm WHERE cm.club.id = :clubId AND cm.durationAndStatus.startDate = :startDate", Long.class);
        query.setParameter("clubId", clubMembership.getStudentClub().getId());
        query.setParameter("startDate", clubMembership.getDurationAndStatus().getStartDate());  // assuming startDate is a field
        return query.getSingleResult() > 0;
    }


    @Transactional
    public boolean deleteClubMembership(int membershipId) {
        ClubMembership clubMembership = em.find(ClubMembership.class, membershipId);
        if (clubMembership != null) {
            em.remove(clubMembership);
            return true;  // Deletion successful
        }
        return false;  // Membership not found
    }
    
    // These methods are more generic.

    public <T> List<T> getAll(Class<T> entity, String namedQuery) {
        TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
        return allQuery.getResultList();
    }
    
    public <T> T getById(Class<T> entity, String namedQuery, int id) {
        TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
        allQuery.setParameter(PARAM1, id);
        return allQuery.getSingleResult();
    }

    @Transactional
    public List<Course> getAllCourses() {
        return em.createNamedQuery("Course.findAll", Course.class).getResultList();
    }

    @Transactional
    public List<PeerTutorRegistration> getRegistrationsByStudentId(int studentId) {
        TypedQuery<PeerTutorRegistration> query = em.createQuery(
            "SELECT r FROM PeerTutorRegistration r WHERE r.id.studentId = :studentId", PeerTutorRegistration.class);
        query.setParameter("studentId", studentId);
        return query.getResultList();
    }


}