package ch.heigvd.amt.jpa.mappings;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.*;
import java.util.Arrays;

public class StringArrayType implements UserType<String[]> {

    @Override
    public int getSqlType() {
        return Types.ARRAY;
    }

    @Override
    public Class<String[]> returnedClass() {
        return String[].class;
    }

    @Override
    public boolean equals(String[] strings, String[] j1) {
        return Arrays.equals(strings, j1);
    }

    @Override
    public int hashCode(String[] strings) {
        return Arrays.hashCode(strings);
    }

    @Override
    public String[] nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session,
                                Object owner) throws SQLException {
        Array array = rs.getArray(position);
        return array != null ? (String[]) array.getArray() : null;
    }

    @Override
    public void nullSafeSet(PreparedStatement st, String[] value, int index,
                            SharedSessionContractImplementor session) throws SQLException {
       if (st != null) {
           if (value != null) {
               try (Connection connection = session.getJdbcConnectionAccess().obtainConnection()) {
                   Array array = connection.createArrayOf("text", value);
                   st.setArray(index, array);
               }
           } else {
              st.setNull(index, Types.ARRAY);
           }
        }
     }

    @Override
    public String[] deepCopy(String[] strings) {
        // As we are working with a string array, the values within are immutable.
        // So deepCopy == clone;
        if (strings == null) {
            return null;
        }
        return strings.clone();
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(String[] strings) {
        return deepCopy(strings);
    }

    @Override
    public String[] assemble(Serializable serializable, Object o) {
        return deepCopy((String[]) serializable);
    }


}
