/********************************************************************************************************2*4*w*
 * 
 * Updated by:  Group 4
 * 040923145, Liz, Quach (as from ACSIS)
 * 041075438 , Krish Patel (as from ACSIS)
 * 041082119, Emmanuel, Alabi(as from ACSIS)
 * 
 */package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.util.List;

import static acmecollege.utility.MyConstants.PEER_TUTOR_REGISTRATION_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.PeerTutor;
import acmecollege.entity.PeerTutorRegistration;
import acmecollege.entity.PeerTutorRegistrationPK;

@Path(PEER_TUTOR_REGISTRATION_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PeerTutorRegistrationResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;
    
    @PersistenceContext
    private EntityManager em;

    @GET
    @Path("/{studentId}/{courseId}")
    @RolesAllowed({ADMIN_ROLE})
    public Response getRegistration(@PathParam("studentId") int studentId, @PathParam("courseId") int courseId) {
        PeerTutorRegistrationPK pk = new PeerTutorRegistrationPK(studentId, courseId);
        PeerTutorRegistration registration = em.find(PeerTutorRegistration.class, pk);
        if (registration == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(registration).build();
    }
    
    @GET
    @Path("/student/{studentId}")
    @RolesAllowed({ADMIN_ROLE})
    public Response getRegistrationsByStudent(@PathParam("studentId") int studentId) {
        List<PeerTutorRegistration> registrations = service.getRegistrationsByStudentId(studentId);
        if (registrations == null || registrations.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No registrations found for student ID: " + studentId).build();
        }
        return Response.ok(registrations).build();
    }
}
