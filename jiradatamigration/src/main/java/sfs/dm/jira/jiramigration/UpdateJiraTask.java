package sfs.dm.jira.jiramigration;

import java.util.HashMap;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;

//import io.atlassian.util.concurrent.Promise;

/**
 * UpdateJiraTask!
 *
 */
public class UpdateJiraTask {
	final static Logger logger = LoggerFactory.getLogger(UpdateJiraTask.class);
	private static HashMap<String, String> taskMap = new HashMap<String, String>();

	public static void main(String[] args) {
		UpdateJiraTask.processCSV();
		System.out.println(taskMap);
		UpdateJiraTask.updateEpicLink();

	}

	private static void updateEpicLink() {

		URI jiraServerUri;
		try {
			jiraServerUri = new URI("https://enable.lrn.com/");
			final JiraRestClient restClient = new AsynchronousJiraRestClientFactory()
					.createWithBasicHttpAuthentication(jiraServerUri, "ashok.jha", "Mr1ty0muksh1yamamr1tat");
			try {
				taskMap.forEach((issueKey, epicVal) -> {
					IssueInputBuilder builder = new IssueInputBuilder();
					builder.setFieldInput(new FieldInput("customfield_10008", epicVal));
					IssueInput epic = builder.build();
					try {
						restClient.getIssueClient().updateIssue(issueKey, epic).claim();
					} catch (RestClientException rex) {
						String err = String.format("Epic Link %s => %s failed : ",issueKey,epicVal);
						System.out.println(err);
						logger.error(err, rex);
					}
				});

			} finally {
				try {
					restClient.close();
				} catch (IOException ioe) {
					logger.error("IO Exp  ", ioe);
				}
			}
		} catch (URISyntaxException urle) {
			logger.error("URL Syntax ", urle);
		}

	}

	public static void processCSV() {
		String csvFileName = ".\\Data\\TCL JIRA TR Combined Data Feb 2023 - 3_23_2023.csv";
		try {
			@SuppressWarnings("deprecation")
			CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase();
			Path path = Paths.get(csvFileName);
			CSVParser csvParser = CSVParser.parse(path, StandardCharsets.UTF_8, csvFormat);
			for (CSVRecord csvRecord : csvParser) {
				String issueKey = csvRecord.get("Issue key");
				String epicLink = csvRecord.get("Custom field (Epic Link)");
				// System.out.println( "issueKey=" + issueKey + " , epicLink" + epicLink);
				if (StringUtils.isNotBlank(epicLink)) {
					taskMap.put(issueKey.trim(), epicLink.trim());
				}
			}
			csvParser.close();
		} catch (IOException ioe) {
			logger.error(csvFileName+" IOExp ",ioe);
		}

	}

	@SuppressWarnings("unused")
	private static void connectToJira() {

		URI jiraServerUri;
		try {
			jiraServerUri = new URI("https://enable.lrn.com/");
			final JiraRestClient restClient = new AsynchronousJiraRestClientFactory()
					.createWithBasicHttpAuthentication(jiraServerUri, "ashok.jha", "Mr1ty0muksh1yamamr1tat");
			try {
				/*
				 * System.out.println(restClient.toString()); IssueInputBuilder iib = new
				 * IssueInputBuilder(); iib.setProjectKey("TCL");
				 * iib.setSummary("SFS Linking Story 12"); iib.setIssueTypeId(7L);
				 * iib.setDescription(
				 * "SFS Linking SFS Linking Story T2 To Check Whether >inking with Ep;ic is working or not ust rest client"
				 * ); iib.setPriorityId(10304L); iib.setAssigneeName("Praveen.Gadde");
				 * iib.setReporterName("Ashok.Jha");
				 * 
				 * 
				 * iib.setFieldInput(new FieldInput("customfield_10008", "TCL-30000"));
				 * IssueInput issue = iib.build(); BasicIssue issueObj =
				 * restClient.getIssueClient().createIssue(issue).claim();
				 * System.out.println("Created Issue " + issueObj.getKey() +
				 * " created successfully"); String issueKey = issueObj.getKey();
				 */
				String issueKey = "TCL-30067";
				IssueInputBuilder builder = new IssueInputBuilder();
				builder.setFieldInput(new FieldInput("customfield_10008", "TCL-30050"));
				IssueInput issueInput = builder.build();
				restClient.getIssueClient().updateIssue(issueKey, issueInput).claim();
				System.out.println(" Epic Link successfull");
				System.out.println(issueKey);

				// issueObj = restClient.getIssueClient().updateIssue(issue).claim();

				// issueObj = restClient.getIssueClient().createIssue(issue).claim();
				// System.out.println(issue);
				// System.out.println("Created Issue " + issueObj.getKey() + " created
				// successfully");

			} finally {
				try {
					restClient.close();
				} catch (IOException ioe) {
					logger.error("IO  ",ioe);
				}
			}
		} catch (URISyntaxException urle) {
			logger.error("URL Syntax  ",urle);
		}

	}

}
