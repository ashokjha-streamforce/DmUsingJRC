/*
 * Licensed to the Streamforce Solution(SFS) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The SFS licenses this file to You under the SFS License, Version 2.0
 * (the "License"); 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * @author Ashok Kumar Jha (ashok.jha@streamforcesolutions.com)
 */
package sfs.dm.jira.jiramigration;

import java.util.HashMap;
import java.util.Properties;
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
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;

//import io.atlassian.util.concurrent.Promise;

/**
 * UpdateJiraTask!
 * 
 * @author ASHOK KR JHA (ashok.jha@streamforcesolutions.com)
 */
public class UpdateJiraTask {
	final static Logger logger = LoggerFactory.getLogger(UpdateJiraTask.class);
	private static HashMap<String, String> taskMap = new HashMap<String, String>();
	private static Properties appProps = new Properties();

	public static void main(String[] args) {
		try {
			appProps.load(UpdateJiraTask.class.getClassLoader().getResourceAsStream("jiradatamig.properties"));
			UpdateJiraTask.testConnectJira();
			UpdateJiraTask.processCSV();
			logger.debug("Task to Update: " + taskMap);
			System.out.println(taskMap);
			UpdateJiraTask.updateEpicLink();
		} catch (IOException e) {
			logger.error("", e);
		}

	}

	private static void updateEpicLink() {
		try {
			final JiraRestClient restClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(
					new URI(appProps.getProperty("JIRA_SERVER")), appProps.getProperty("USER"),
					appProps.getProperty("KEY"));
			try {
				taskMap.forEach((issueKey, epicVal) -> {
					IssueInputBuilder builder = new IssueInputBuilder();
					builder.setFieldInput(new FieldInput(appProps.getProperty("EPIC_LINK"), epicVal));
					IssueInput epic = builder.build();
					try {
						restClient.getIssueClient().updateIssue(issueKey, epic).claim();
					} catch (RestClientException rex) {
						String err = String.format("Epic Link %s => %s failed : ", issueKey, epicVal);
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
			Path path = Paths.get(appProps.getProperty("DATAFILE"));
			CSVParser csvParser = CSVParser.parse(path, StandardCharsets.UTF_8, csvFormat);
			for (CSVRecord csvRecord : csvParser) {
				String issueKey = csvRecord.get("Issue key");
				String epicLink = csvRecord.get("Custom field (Epic Link)");
				if (StringUtils.isNotBlank(epicLink)) {
					taskMap.put(issueKey.trim(), epicLink.trim());
				}
			}
			csvParser.close();
		} catch (IOException ioe) {
			logger.error(csvFileName + " IOExp ", ioe);
		}

	}

	private static void testConnectJira() {
		try {
			final JiraRestClient restClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(
					new URI(appProps.getProperty("JIRA_SERVER")), appProps.getProperty("USER"),
					appProps.getProperty("KEY"));
			try {
				Issue issueObj = restClient.getIssueClient().getIssue("TCL-30000").claim();
				System.out.println("Issue " + issueObj);
			} finally {
				try {
					restClient.close();
				} catch (IOException ioe) {
					logger.error("IO  ", ioe);
				}
			}
		} catch (URISyntaxException urle) {
			logger.error("URL Syntax  ", urle);
		}

	}

}
