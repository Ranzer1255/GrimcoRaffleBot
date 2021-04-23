package net.ranzer.grimco.rafflebot.database.types;

import net.dv8tion.jda.api.entities.Role;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.StringType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/*TODO, i have no frelling clue what i'm doing right now
 * filling out RoleType methods.... i'm still not even sure this is going to get used but i
 * *think* i'm on the right track here... i hope..
 * refferences:
 * https://docs.jboss.org/hibernate/stable/orm/userguide/html_single/Hibernate_User_Guide.html#basic-custom-type
 * https://www.baeldung.com/hibernate-persisting-maps
 * https://www.baeldung.com/hibernate-custom-types
 */
public class RoleType implements UserType {
	@Override
	public int[] sqlTypes() {
		return new int[] {StringType.INSTANCE.sqlType()};
	}

	@Override
	public Class returnedClass() {
		return Role.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return Objects.equals(x,y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return Objects.hashCode(x);
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
		return null;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {

	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return null;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return null;
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return null;
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return null;
	}
}
