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
import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.USER_ROLE;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.Course;

@Path("courses")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @Inject
    protected SecurityContext sc;

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addCourse(Course newCourse) {
        LOG.debug("Adding a new course");
        Course existingCourse = service.findCourseByCode(newCourse.getCourseCode());
        if (existingCourse != null) {
            return Response.status(Response.Status.CONFLICT).entity("Course code already exists").build();
        }
        Course persistedCourse = service.persistCourse(newCourse);
        return Response.ok(persistedCourse).build();
    }

    @GET
    @Path("/{courseId}")
    public Response getCourseById(@PathParam("courseId") Long courseId) {
        LOG.debug("Retrieving course with id = {}", courseId);
        Course course = service.getCourseById(courseId);
        if (course == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
        }
        return Response.ok(course).build();
    }

    @DELETE
    @Path("/{courseId}")
    @RolesAllowed({ADMIN_ROLE})
    public Response deleteCourse(@PathParam("courseId") Long courseId) {
        LOG.debug("Deleting course with id = {}", courseId);
        boolean isDeleted = service.deleteCourse(courseId);
        if (!isDeleted) {
            return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
        }
        return Response.noContent().build();
    }

    @PUT
    @Path("/{courseId}")
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response updateCourse(@PathParam("courseId") Long courseId, Course updatedCourse) {
        LOG.debug("Updating course with id = {}", courseId);
        Course course = service.updateCourse(courseId, updatedCourse);
        if (course == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
        }
        return Response.ok(course).build();
    }
}
