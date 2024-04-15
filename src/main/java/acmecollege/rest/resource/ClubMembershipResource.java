/********************************************************************************************************2*4*w*
 * 
 * Updated by:  Group 4
 * 040923145, Liz, Quach (as from ACSIS)
 * 041075438 , Krish Patel (as from ACSIS)
 * 041082119, Emmanuel, Alabi(as from ACSIS)
 * 
 */
package acmecollege.rest.resource;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.ClubMembership;

@Path("clubmemberships")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClubMembershipResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    @RolesAllowed({ADMIN_ROLE})
    @POST
    public Response createClubMembership(ClubMembership newClubMembership) {
        LOG.debug("Adding a new club membership");
        if (service.isClubMembershipDuplicated(newClubMembership)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Entity already exists");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        } else {
            ClubMembership tempClubMembership = service.persistClubMembership(newClubMembership);
            return Response.ok(tempClubMembership).build();
        }
    }

    @GET
    @Path("/{membershipId}")
    public Response getClubMembershipById(@PathParam("membershipId") int membershipId) {
        LOG.debug("Retrieving club membership with id = {}", membershipId);
        ClubMembership clubMembership = service.getClubMembershipById(membershipId);
        if (clubMembership == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(clubMembership).build();
    }

    @RolesAllowed({ADMIN_ROLE})
    @DELETE
    @Path("/{membershipId}")
    public Response deleteClubMembership(@PathParam("membershipId") int membershipId) {
        LOG.debug("Deleting club membership with id = {}", membershipId);
        boolean isDeleted = service.deleteClubMembership(membershipId);
        if (!isDeleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
