package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.PEER_TUTOR_SUBRESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.PeerTutor;
import acmecollege.entity.PeerTutorRegistration;

@Path(PEER_TUTOR_SUBRESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PeerTutorResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    //Basic CRUD Operations
    @GET
    @RolesAllowed({ADMIN_ROLE})
    public Response getAllPeerTutors() {
        LOG.debug("retrieving all peer tutors ...");
        List<PeerTutor> peerTutors = service.getAllPeerTutors();
        return Response.ok(peerTutors).build();
    }

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getPeerTutorById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve specific peer tutor " + id);
        PeerTutor peerTutor = service.getPeerTutorById(id);
        return Response.status(peerTutor == null ? Status.NOT_FOUND : Status.OK).entity(peerTutor).build();
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addPeerTutor(PeerTutor newPeerTutor) {
        PeerTutor peerTutorWithIdTimestamps = service.persistPeerTutor(newPeerTutor);
        return Response.status(Status.CREATED).entity(peerTutorWithIdTimestamps).build();
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updatePeerTutor(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, PeerTutor updatedPeerTutor) {
        PeerTutor peerTutor = service.updatePeerTutor(id, updatedPeerTutor);
        return Response.ok(peerTutor).build();
    }

    //Relationships
    @PUT
    @Path("/student/{studentId}/course/{courseId}")
    @RolesAllowed({ADMIN_ROLE})
    public Response assignPeerTutorToStudentCourse(@PathParam("studentId") int studentId,
                                                   @PathParam("courseId") int courseId,
                                                   PeerTutor newPeerTutor) {
        LOG.debug("Assigning PeerTutor to StudentCourse - studentId: {}, courseId: {}", studentId, courseId);
        try {
            PeerTutorRegistration registration = service.setPeerTutorForStudentCourse(studentId, courseId, newPeerTutor);
            // Return the registration if successful
            return Response.ok(registration).build();
        } catch (EntityNotFoundException e) {
            // Handle the case where the student or course does not exist
            return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            // Handle other exceptions appropriately
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("An error occurred while assigning the PeerTutor.").build();
        }
    }

}
