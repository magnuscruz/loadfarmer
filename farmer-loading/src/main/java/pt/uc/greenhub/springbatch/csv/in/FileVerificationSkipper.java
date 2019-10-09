package pt.uc.greenhub.springbatch.csv.in;

import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.dao.DataAccessException;

/**
 * The Class FileVerificationSkipper.
 *
 * @author ashraf
 */
public class FileVerificationSkipper implements SkipPolicy {
	
	private static final Logger logger = LoggerFactory.getLogger("badRecordLogger");

	@Override
	public boolean shouldSkip(Throwable exception, int skipCount) throws SkipLimitExceededException {
		if (exception instanceof FileNotFoundException) {
			return false;
		} else if (exception instanceof FlatFileParseException && skipCount <= 10000000) {
			FlatFileParseException ffpe = (FlatFileParseException) exception;
			StringBuilder errorMessage = new StringBuilder();
			errorMessage.append("An error occured while processing the " + ffpe.getLineNumber()
					+ " line of the file. Below was the faulty " + "input.\n");
			errorMessage.append(ffpe.getInput() + "\n");
			logger.error("{}", ffpe);
			return true;
		} else if (exception instanceof DataAccessException && skipCount <= 10000000) {
			DataAccessException ffpe = (DataAccessException) exception;
			StringBuilder errorMessage = new StringBuilder();
			errorMessage.append("An error occured while processing the " + ffpe.getMessage()
					+ " line of the file. Below was the faulty " + "input.\n");
//			errorMessage.append(ffpe.getInput() + "\n");
			logger.error("{}", errorMessage.toString());
			return true;
		} else {
			return false;
		}
	}

}
