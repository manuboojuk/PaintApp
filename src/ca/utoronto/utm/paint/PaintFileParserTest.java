package ca.utoronto.utm.paint;
import static org.junit.Assert.*;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.utoronto.utm.paint.PaintFileParser;

public class PaintFileParserTest {

	private Pattern pErrorMessage = Pattern.compile("^Error in line\\s+(\\d+)\\s+");
	private void doParserTestCase(String fileName, String message, String expectedErrorMessage) throws IOException {
		BufferedReader lineInput = new BufferedReader(new FileReader(fileName));
		PaintFileParser parser = new PaintFileParser();
		
		PaintModel paintModel = new PaintModel();
		boolean retVal = parser.parse(lineInput, paintModel);
		String errorMessage = parser.getErrorMessage();
		// System.out.println(fileName+" "+ errorMessage);

		if (expectedErrorMessage.equals("")) {
			assertTrue(fileName + ": Returns true for basic file with no spaces", retVal);
			assertEquals(fileName + ": No error message", "", errorMessage);
		} else {
			String reportedLine = "", expectedLine = "";

			assertFalse(fileName + ": Returns false for basic incorrect file format", retVal);
			// System.out.println(errorMessage);
			Matcher m;
			m = pErrorMessage.matcher(expectedErrorMessage);
			if (m.find())
				expectedLine = m.group(1);

			m = pErrorMessage.matcher(errorMessage);
			if (m.find())
				reportedLine = m.group(1);
			// System.out.println(expectedLine+":"+reportedLine);
			assertEquals(fileName + ": Error Message", expectedLine, reportedLine);
		}
		lineInput.close();
	}
}

