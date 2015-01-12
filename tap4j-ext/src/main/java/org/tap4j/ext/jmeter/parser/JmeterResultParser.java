package org.tap4j.ext.jmeter.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.tap4j.ext.jmeter.model.AbstractSample;
import org.tap4j.ext.jmeter.model.AssertionResult;
import org.tap4j.ext.jmeter.model.HttpSample;
import org.tap4j.ext.jmeter.model.ObjectFactory;
import org.tap4j.ext.jmeter.model.TestResults;
import org.tap4j.model.Header;
import org.tap4j.model.Plan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.parser.ParserException;
import org.tap4j.producer.Producer;
import org.tap4j.producer.TapProducerFactory;
import org.tap4j.representer.DumperOptions;
import org.tap4j.util.StatusValues;

/**
 * Parses Jmeter xml
 * 
 * @author s2o
 */
public class JmeterResultParser {

	private static final Integer TAP_VERSION = 13;
	private static final Integer INITIAL_TEST_STEP = 1;
	private static final CharSequence XML_EXT = ".xml";
	private static final CharSequence TAP_EXT = ".tap";
	private static final String FAILURE_MESSAGE = "- FailureMessage:";
	private static final String FAIL_ASSERT = "failAssert ";
	private static final String ERROR = "error ";
	private static final String DUMP = "dump";
	private static final String SEVERITY = "severity";
	private static final String MESSAGE = "message";
	public static final String VALUE_SPLIT = " - ";
	private Charset charset;

	public JmeterResultParser() {
		charset = Charset.defaultCharset();
	}

	public JmeterResultParser(Charset pCharset) {
		charset = pCharset;
	}

	/**
	 * Parses jMeter result file into TestSet and optionally generates a Tap file with the same name of the parsed file
	 * 
	 * @param file
	 * @param generateTapFile
	 * @return
	 */
	public TestSet parseFile(File file, boolean generateTapFile) {

		TestSet testSet = new TestSet();
		final Header header = new Header(TAP_VERSION);
		testSet.setHeader(header);

		List<AbstractSample> sampleResultList = getResultList(file);
		Plan plan = new Plan(INITIAL_TEST_STEP, sampleResultList.size());
		testSet.setPlan(plan);

		for (AbstractSample httpSample : sampleResultList) {
			List<AssertionResult> assetionResultList = httpSample.getAssertionResult();
			boolean resultError = false;
			String failitureMessage = "";
			String severity = "";
			// Searching an assertion failed
			for (AssertionResult assertionResult : assetionResultList) {
				resultError = (assertionResult.isFailure() || assertionResult.isError());
				if (resultError) {
					failitureMessage += FAILURE_MESSAGE + assertionResult.getFailureMessage();

					// Log the type of fail
					if (assertionResult.isFailure()) {
						severity = FAIL_ASSERT;
					}
					if (assertionResult.isError()) {
						severity += ERROR;
					}
				}
			}

			TestResult testResult = new TestResult();
			testResult.setDescription(httpSample.getLb());
			StatusValues status = StatusValues.OK;
			if (resultError) {
				final Map<String, Object> yamlish = testResult.getDiagnostic();
				createYAMLishMessage(yamlish, httpSample, failitureMessage);
				createYAMLishSeverity(yamlish, severity);
				createYAMLishDump(yamlish, httpSample);
				status = StatusValues.NOT_OK;
			}
			testResult.setStatus(status);
			testSet.addTestResult(testResult);
		}

		if (generateTapFile) {
			generateTapFile(file, testSet);
		}

		return testSet;
	}

	/**
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws JAXBException
	 * @throws IOException
	 */
	private List<AbstractSample> getResultList(File file) {
		TestResults results = new TestResults();
		InputStream inputStream = null;
		Reader reader = null;
		try {
			inputStream = new FileInputStream(file);
			reader = new InputStreamReader(inputStream, charset);

			JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			results = (TestResults) unmarshaller.unmarshal(reader);

		} catch (JAXBException jAXBException) {
			throw new ParserException("Exception on parse xml", jAXBException);
		} catch (FileNotFoundException fileNotFoundException) {
			throw new ParserException("XML file not found: " + file, fileNotFoundException);
		}

		try {
			if (reader != null) {
				reader.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			throw new ParserException("Error IOException: " + e.getMessage(), e);
		}

		List<AbstractSample> sampleResultList = null;
		if (results == null) {
			sampleResultList = new ArrayList<AbstractSample>();
		} else {
			sampleResultList = results.getHttpSampleOrSample();
		}

		return sampleResultList;
	}

	/**
	 * @param file
	 * @param testSet
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void generateTapFile(File file, TestSet testSet) {
		String fileName = file.getAbsolutePath();
		String tapFileName = fileName.replace(XML_EXT, TAP_EXT);

		DumperOptions options = new DumperOptions();
		options.setPrintDiagnostics(true);
		options.setCharset(charset.name());
		Producer tapProducer = TapProducerFactory.makeTap13YamlProducer();
		Writer out;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tapFileName), charset));
			tapProducer.dump(testSet, out);
			out.close();
		} catch (FileNotFoundException fileNotFoundException) {
			throw new ParserException("TAP file not found: " + tapFileName, fileNotFoundException);
		} catch (IOException e) {
			throw new ParserException("Error IOException: " + e.getMessage(), e);
		}

	}

	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	private void createYAMLishMessage(Map<String, Object> yamlish, AbstractSample httpSample, String failitureMessage) {
		yamlish.put(MESSAGE, httpSample.getRc() + VALUE_SPLIT + httpSample.getRm() + failitureMessage);
	}

	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	private void createYAMLishSeverity(Map<String, Object> yamlish, String severity) {
		yamlish.put(SEVERITY, severity);
	}

	/**
	 * @param yamlish
	 * @param testNgTestResult
	 */
	private void createYAMLishDump(Map<String, Object> yamlish, AbstractSample httpSample) {
		String dump = "";
		if (httpSample instanceof HttpSample) {
			dump = ((HttpSample) httpSample).getResponseData().getValue();
		}

		yamlish.put(DUMP, dump);
	}
}
