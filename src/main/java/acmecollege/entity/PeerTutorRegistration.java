/********************************************************************************************************2*4*w*
 * File:  PeerTutorRegistration.java Course materials CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 * Updated by:  Group NN
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *   studentId, firstName, lastName (as from ACSIS)
 *   
 */
package acmecollege.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

@SuppressWarnings("unused")
/**
 * The persistent class for the peer_tutor_registration database table.
 */
@Entity
@Table(name = "peer_tutor_registration")
@Access(AccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = PeerTutorRegistration.FIND_ALL,
                query = "SELECT ptr FROM PeerTutorRegistration ptr JOIN FETCH ptr.student JOIN FETCH ptr.course"
        ),
    @NamedQuery(
    	    name = PeerTutorRegistration.FIND_BY_STUDENT,
    	    query = "SELECT ptr FROM PeerTutorRegistration ptr JOIN FETCH ptr.peerTutor WHERE ptr.id.studentId = :param1"
    	)
})
public class PeerTutorRegistration extends PojoBaseCompositeKey<PeerTutorRegistrationPK> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String FIND_ALL = "PeerTutorRegistration.findAll";
	public static final String FIND_BY_STUDENT = "PeerTutorRegistration.findByStudent";

	// Hint - What annotation is used for a composite primary key type?
	@EmbeddedId
	private PeerTutorRegistrationPK id;

	// @MapsId is used to map a part of composite key to an entity.
	@MapsId("studentId")
	@ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
	private Student student;

	@MapsId("courseId")
	@ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", referencedColumnName = "course_id", nullable = false)
	private Course course;

	@ManyToOne(cascade = CascadeType.ALL, optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "peer_tutor_id", referencedColumnName = "peer_tutor_id", nullable = true)
	@JsonBackReference
	private PeerTutor peerTutor;

	@Column(name = "numeric_grade")
	private int numericGrade;

	@Column(length = 3, name = "letter_grade")
	private String letterGrade;


	public PeerTutorRegistration() {
		id = new PeerTutorRegistrationPK();
	}

	@Override
	public PeerTutorRegistrationPK getId() {
		return id;
	}

	@Override
	public void setId(PeerTutorRegistrationPK id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		id.setStudentId(student.id);
		this.student = student;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		id.setCourseId(course.id);
		this.course = course;
	}

	public PeerTutor getPeerTutor() {
		return peerTutor;
	}

	public void setPeerTutor(PeerTutor peerTutor) {
		this.peerTutor = peerTutor;
	}

	public int getNumericGrade() {
		return numericGrade;
	}
	
	public void setNumericGrade(int numericGrade) {
		this.numericGrade = numericGrade;
	}

	public String getLetterGrade() {
		return letterGrade;
	}

	public void setLetterGrade(String letterGrade) {
		this.letterGrade = letterGrade;
	}

	//Inherited hashCode/equals is sufficient for this entity class

}