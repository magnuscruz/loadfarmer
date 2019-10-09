package pt.uc.greenhub.springbatch.excel.in;

import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;

import pt.uc.greenhub.springbatch.web.controller.SampleDTO;

/**
 * This class demonstrates how we can implement a row mapper that maps
 * a row found from an Excel document into a {@code SampleDTO} object. If
 * the Excel document has no header, we have to use this method for transforming
 * the input data into {@code SampleDTO} objects.
 *
 * @author Petri Kainulainen
 */
public class SampleExcelRowMapper implements RowMapper<SampleDTO> {

    @Override
    public SampleDTO mapRow(RowSet rowSet) throws Exception {
        SampleDTO sample = new SampleDTO();

//        sample.setName(rowSet.getColumnValue(0));
//        sample.setEmailAddress(rowSet.getColumnValue(1));
//        sample.setPurchasedPackage(rowSet.getColumnValue(2));

        return sample;
    }
}
