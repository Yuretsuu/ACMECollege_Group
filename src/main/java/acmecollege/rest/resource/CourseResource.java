/********************************************************************************************************2*4*w*
 * 
 * Updated by:  Group 4
 * 040923145, Liz, Quach (as from ACSIS)
 * 041075438 , Krish Patel (as from ACSIS)
 * 041082119, Emmanuel, Alabi(as from ACSIS)
 * 
 */
package acmecollege.rest.resource;

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.util.List;

import static acmecollege.utility.MyConstants.COURSE_RESOURCE_NAME;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.ejb.ACMECollegeService;
import acmecollege.entity.Course;

@Path(COURSE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {

    private static final Logger LOG = LogManager.getLogger();

    @EJB
    protected ACMECollegeService service;

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    public Response getAllCourses() {
        LOG.debug("retrieving all courses ...");
        List<Course> courses = service.getAllCourses();
        return Response.ok(courses).build();
    }

    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getCourseById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("try to retrieve specific course {}", id);
        Course course = service.getCourseById(id);
        if (course == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
        }
        return Response.ok(course).build();
    }

    @POST
    @RolesAllowed({ADMIN_ROLE})
    public Response addCourse(Course newCourse) {
        LOG.debug("Adding a new course {}", newCourse);
        Course courseWithIdTimestamps = service.persistCourse(newCourse);
        return Response.status(Response.Status.CREATED).entity(courseWithIdTimestamps).build();
    }

    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, Course updatedCourse) {
        LOG.debug("Updating course with id = {}", id);
        Course course = service.updateCourse(id, updatedCourse);
        return Response.ok(course).build();
    }

    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteCourse(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        LOG.debug("Deleting course with id = {}", id);
        boolean isDeleted = service.deleteCourse(id);
        if (!isDeleted) {
            return Response.status(Response.Status.NOT_FOUND).entity("Course not found").build();
        }
        return Response.ok().build();
    }
}
