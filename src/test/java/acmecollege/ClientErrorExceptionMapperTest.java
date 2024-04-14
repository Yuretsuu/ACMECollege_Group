package acmecollege;

import static org.junit.jupiter.api.Assertions.assertEquals;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import acmecollege.rest.ClientErrorExceptionMapper;
import acmecollege.rest.resource.HttpErrorResponse;

public class ClientErrorExceptionMapperTest {

    private final ClientErrorExceptionMapper exceptionMapper = new ClientErrorExceptionMapper();

    @Test
    public void testToResponse_SuccessfulMapping() {
        // Create a ClientErrorException with a specific status code
        int statusCode = 404;
        ClientErrorException exception = new ClientErrorException(statusCode);
        
        // Call the toResponse method of the exception mapper
        Response response = exceptionMapper.toResponse(exception);
        
        // Verify that the response has the correct status code
        assertEquals(statusCode, response.getStatus());
    }
    
    @Test
    public void testToResponse_CustomErrorMessage() {
        // Create a ClientErrorException with a specific status code and custom message
        int statusCode = 404;
        String customMessage = "Not Found";
        ClientErrorException exception = new ClientErrorException(customMessage, statusCode);
        
        // Call the toResponse method of the exception mapper
        Response response = exceptionMapper.toResponse(exception);
        
        // Extract the custom error message from the HttpErrorResponse
        HttpErrorResponse errorResponse = (HttpErrorResponse) response.getEntity();
        String errorMessage = errorResponse.getReasonPhrase();
        
        // Verify that the response has the correct status code and custom error message
        assertEquals(statusCode, response.getStatus());
        assertEquals(customMessage, errorMessage);
    }
}
   

