package acmecollege.rest.serializer;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import acmecollege.entity.AcademicStudentClub;
import acmecollege.entity.NonAcademicStudentClub;
import acmecollege.entity.StudentClub;

public class StudentClubSerializer extends JsonSerializer<StudentClub> implements Serializable {
    private static final long serialVersionUID = 1L;

    public StudentClubSerializer() {
        this(null);
    }

    public StudentClubSerializer(Class<StudentClub> t) {
        super();
    }

    @Override
    public void serialize(
        StudentClub studentClub, 
        JsonGenerator jgen, 
        SerializerProvider provider) throws IOException {
        
        jgen.writeStartObject();
        jgen.writeNumberField("id", studentClub.getId());
        jgen.writeStringField("name", studentClub.getName());
        
        if (studentClub instanceof AcademicStudentClub) {
        	jgen.writeBooleanField("isAcademic", true);
        } else if (studentClub instanceof NonAcademicStudentClub) {
        	jgen.writeBooleanField("isAcademic", false);
        }
        jgen.writeNumberField("membershipCount", studentClub.getClubMemberships().size());
        jgen.writeEndObject();
    }
}
