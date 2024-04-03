package acmecollege.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2024-04-02T00:55:09.882-0400")
@StaticMetamodel(SecurityRole.class)
public class SecurityRole_ extends PojoBase_ {
	public static volatile SingularAttribute<SecurityRole, String> roleName;
	public static volatile SetAttribute<SecurityRole, SecurityUser> users;
}
