package pt.uc.greenhub.springbatch.csv.in;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;

public class BeanWrapperFieldSetMapperCustom<T> extends BeanWrapperFieldSetMapper<T> {
	private String dateFormat = null;
	
	public BeanWrapperFieldSetMapperCustom(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	
    @Override
    protected void initBinder(DataBinder binder) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (!StringUtils.isEmpty(text)) {
                    setValue(LocalDate.parse(text, formatter));
                } else {
                    setValue(null);
                }
            }

            @Override
            public String getAsText() throws IllegalArgumentException {
                Object date = getValue();
                if (date != null) {
                    return formatter.format((LocalDate) date);
                } else {
                    return "";
                }
            }
        });
    }
}