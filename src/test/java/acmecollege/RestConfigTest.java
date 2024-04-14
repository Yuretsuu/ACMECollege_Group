package acmecollege;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import org.junit.jupiter.api.Test;

import acmecollege.rest.RestConfig;
import acmecollege.utility.MyConstants;

public class RestConfigTest {

    private final RestConfig restConfig = new RestConfig();

    @Test
    public void testApplicationPathAnnotation() {
        // Get the value of the ApplicationPath annotation from the RestConfig class
        ApplicationPath annotation = RestConfig.class.getAnnotation(ApplicationPath.class);
        
        // Verify that the value of the annotation matches the expected API version
        assertEquals(MyConstants.APPLICATION_API_VERSION, annotation.value());
    }
    
    @Test
    public void testDeclaredRolesAnnotation() {
        // Get the value of the DeclareRoles annotation from the RestConfig class
        DeclareRoles annotation = RestConfig.class.getAnnotation(DeclareRoles.class);
        
        // Verify that the roles declared in the annotation match the expected roles
        assertArrayEquals(new String[]{MyConstants.USER_ROLE, MyConstants.ADMIN_ROLE}, annotation.value());
    }

   
}