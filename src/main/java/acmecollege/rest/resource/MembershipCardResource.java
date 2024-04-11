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
import acmecollege.entity.MembershipCard;

@Path("membershipcards")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MembershipCardResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    @POST
    @RolesAllowed({"ADMIN_ROLE"})
    public Response addMembershipCard(MembershipCard newMembershipCard) {
        LOG.debug("Adding a new membership card");
        MembershipCard persistedMembershipCard = service.persistMembershipCard(newMembershipCard);
        return Response.ok(persistedMembershipCard).build();
    }

    @GET
    @Path("/{cardId}")
    public Response getMembershipCardById(@PathParam("cardId") int cardId) {
        LOG.debug("Retrieving membership card with id = {}", cardId);
        MembershipCard membershipCard = service.getMembershipCardById(cardId);
        if (membershipCard == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Membership card not found").build();
        }
        return Response.ok(membershipCard).build();
    }

    @DELETE
    @Path("/{cardId}")
    @RolesAllowed({"ADMIN_ROLE"})
    public Response deleteMembershipCard(@PathParam("cardId") int cardId) {
        LOG.debug("Deleting membership card with id = {}", cardId);
        boolean isDeleted = service.deleteMembershipCard(cardId);
        if (!isDeleted) {
            return Response.status(Response.Status.NOT_FOUND).entity("Membership card not found").build();
        }
        return Response.noContent().build();
    }

}
