package acmecollege.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2024-04-02T00:57:00.986-0400")
@StaticMetamodel(SecurityUser.class)
public class SecurityUser_ extends PojoBase_ {
	public static volatile SingularAttribute<SecurityUser, String> username;
	public static volatile SingularAttribute<SecurityUser, String> pwHash;
	public static volatile SingularAttribute<SecurityUser, Student> student;
	public static volatile SetAttribute<SecurityUser, SecurityRole> roles;
}
