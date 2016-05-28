package commons;

import com.google.common.base.CaseFormat;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * Is used by hibernate to convert camel-case entity fields to db column names with underscore
 */
public class CustomNamingStrategy extends PhysicalNamingStrategyStandardImpl {
    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {

        return Identifier.toIdentifier(CaseFormat.LOWER_CAMEL
                .to(CaseFormat.LOWER_UNDERSCORE, name.toString()));
    }
}