package acmecollege.rest.resource;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.PeerTutorRegistration;

@Path("peertutorregistrations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PeerTutorRegistrationResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    @POST
    @RolesAllowed({"ADMIN_ROLE"})
    public Response addPeerTutorRegistration(PeerTutorRegistration newPeerTutorRegistration) {
        LOG.debug("Adding a new peer tutor registration");
        PeerTutorRegistration persistedPeerTutorRegistration = service.persistPeerTutorRegistration(newPeerTutorRegistration);
        return Response.ok(persistedPeerTutorRegistration).build();
    }

    @GET
    @Path("/{registrationId}")
    public Response getPeerTutorRegistration(@PathParam("registrationId") Long registrationId) {
        LOG.debug("Retrieving peer tutor registration with id = {}", registrationId);
        PeerTutorRegistration peerTutorRegistration = service.getPeerTutorRegistrationById(registrationId);
        if (peerTutorRegistration == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Peer tutor registration not found").build();
        }
        return Response.ok(peerTutorRegistration).build();
    }

    @DELETE
    @Path("/{registrationId}")
    @RolesAllowed({"ADMIN_ROLE"})
    public Response deletePeerTutorRegistration(@PathParam("registrationId") Long registrationId) {
        LOG.debug("Deleting peer tutor registration with id = {}", registrationId);
        boolean isDeleted = service.deletePeerTutorRegistration(registrationId);
        if (!isDeleted) {
            return Response.status(Response.Status.NOT_FOUND).entity("Peer tutor registration not found").build();
        }
        return Response.noContent().build();
    }

    @PUT
    @Path("/{registrationId}")
    @RolesAllowed({"ADMIN_ROLE", "USER_ROLE"})
    public Response updatePeerTutorRegistration(@PathParam("registrationId") Long registrationId, PeerTutorRegistration updatedPeerTutorRegistration) {
        LOG.debug("Updating peer tutor registration with id = {}", registrationId);
        PeerTutorRegistration peerTutorRegistration = service.updatePeerTutorRegistration(registrationId, updatedPeerTutorRegistration);
        if (peerTutorRegistration == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Peer tutor registration not found").build();
        }
        return Response.ok(peerTutorRegistration).build();
    }
}
