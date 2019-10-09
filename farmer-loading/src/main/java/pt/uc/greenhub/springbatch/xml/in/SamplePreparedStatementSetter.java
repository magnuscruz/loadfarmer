package pt.uc.greenhub.springbatch.xml.in;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import pt.uc.greenhub.springbatch.web.controller.SampleDTO;

/**
 * This class is used to set the actual parameter values of a
 * prepared statement.
 */
final class SamplePreparedStatementSetter implements ItemPreparedStatementSetter<SampleDTO> {

    @Override
    public void setValues(SampleDTO student, PreparedStatement preparedStatement) throws SQLException {
//        preparedStatement.setString(1, student.getEmailAddress());
//        preparedStatement.setString(2, student.getName());
//        preparedStatement.setString(3, student.getPurchasedPackage());
    }
}
