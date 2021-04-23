package net.ranzer.grimco.rafflebot.database.types;

import net.dv8tion.jda.api.entities.Role;
import net.ranzer.grimco.rafflebot.GrimcoRaffleBot;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

//TODO, Register this in the bootstrap.... when i do the boot strap.

/* references:
 * https://docs.jboss.org/hibernate/stable/orm/userguide/html_single/Hibernate_User_Guide.html#basic-custom-type
 * https://www.baeldung.com/hibernate-custom-types
 */
public class RoleType extends AbstractSingleColumnStandardBasicType<Role> implements DiscriminatorType<Role> {

	public RoleType(){
		super(VarcharTypeDescriptor.INSTANCE, RoleTypeDescriptor.INSTANCE);
	}

	@Override
	public String getName() {
		return "role";
	}

	@Override
	public Role stringToObject(String xml) throws Exception {
		return fromString(xml);
	}

	@Override
	public String objectToSQLString(Role value, Dialect dialect) throws Exception {
		return toString(value);
	}

	private static class RoleTypeDescriptor extends AbstractTypeDescriptor<Role> {

		public static final RoleTypeDescriptor INSTANCE = new RoleTypeDescriptor();

		public RoleTypeDescriptor(){
			super(Role.class, ImmutableMutabilityPlan.INSTANCE);
		}

		@Override
		public String toString(Role value) {
			return value.getId();
		}

		@Override
		public Role fromString(String string) {
			return GrimcoRaffleBot.getJDA().getRoleById(string);
		}

		@Override
		@SuppressWarnings({"unchecked"})
		public <X> X unwrap(Role value, Class<X> type, WrapperOptions options) {
			if (value==null)
				return null;
			if (Role.class.isAssignableFrom(type))
				return (X) value;
			if (String.class.isAssignableFrom(type))
				return (X) toString(value);
			throw unknownUnwrap(type);
		}

		@Override
		public <X> Role wrap(X value, WrapperOptions options) {
			if (value==null)
				return null;
			if (value instanceof String)
				return fromString((String) value);
			if (value instanceof Role)
				return (Role) value;
			throw unknownWrap(value.getClass());
		}
	}
}
