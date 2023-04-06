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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.google.common.collect.ImmutableSet;

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
            /*
             * File issueDir = new File(appProps.getProperty("PROJECT_ID") + File.separator
             * + "TCL-30000"); if (issueDir.exists() && issueDir.isDirectory()) { File[]
             * listOfFiles = issueDir.listFiles(); for (File file : listOfFiles) {
             * System.out.println(file.getName());
             * 
             * } }
             */
            // UpdateJiraTask.testConnectJira();
            // UpdateJiraTask.processCSV();
            logger.debug("Task to Update: " + taskMap);
            // UpdateJiraTask.updateEpicLink();
            UpdateJiraTask.doAttachment();

            System.out.println("Update to JIRA Task is successful");
            System.exit(0);
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    private static void doAttachment() {
        int i = 1;
        try {
            final JiraRestClient restClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(
                    new URI(appProps.getProperty("JIRA_SERVER")), appProps.getProperty("USER"),
                    appProps.getProperty("KEY"));
            Integer maxResult = 100;
            Integer initial = 0;
            Set<String> fields = ImmutableSet.of("key", "id", "attachment", "summary", "issuetype", "created",
                    "updated", "project", "status", "attachments");
            File prjDir = new File(appProps.getProperty("PROJECT_ID"));
            if (!prjDir.isDirectory())
                prjDir.mkdir();
            int cnt = 1;
            while (true) {
                Integer startAt = initial * maxResult;
                SearchResult searchResult = restClient.getSearchClient()
                        .searchJql("project = " + appProps.getProperty("PROJECT_ID"), maxResult, startAt, fields)
                        .claim();
                if (searchResult.getIssues().iterator().hasNext()) {
                    for (Issue issue : searchResult.getIssues()) {
                        // System.out.println(i + " " + issue.getKey() + " => " + issue.getSummary());
                        // File issueDir = new File(appProps.getProperty("PROJECT_ID") + File.separator
                        // + issue.getKey());
                        // if (!issueDir.isDirectory())
                        // issueDir.mkdir();
                        // i++;
                        File issueDir = new File(appProps.getProperty("PROJECT_ID") + File.separator + issue.getKey());
                        if (issueDir.exists() && issueDir.isDirectory()) {
                            File[] listOfFiles = issueDir.listFiles();
                            for (File file : listOfFiles) {
                                try {
                                    restClient.getIssueClient().addAttachment(issue.getAttachmentsUri(),
                                            new FileInputStream(file), file.getName()).claim();
                                } catch (RestClientException rex) {
                                    logger.error("Err :", rex);
                                } catch (FileNotFoundException exp) {
                                    exp.printStackTrace();
                                }
                                // System.out.println(file.getName());
                            }
                        }
                        System.out.println(cnt++ + " -> successful attached to  " + issue.getKey());
                    }
                    initial++;

                } else {
                    break;
                }

            }
            System.out.println("Total: " + (i - 1));
        } catch (URISyntaxException urle) {
            logger.error("URL Syntax ", urle);
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
        try {
            // CSVFormat csvFormat =
            // CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();
            // Path path = Paths.get(appProps.getProperty("DATAFILE"));
            CSVParser csvParser = CSVParser.parse(Paths.get(appProps.getProperty("DATAFILE")), StandardCharsets.UTF_8,
                    CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build());
            for (CSVRecord csvRecord : csvParser) {
                String issueKey = csvRecord.get("Issue key");
                String epicLink = csvRecord.get("Custom field (Epic Link)");
                if (StringUtils.isNotBlank(epicLink)) {
                    taskMap.put(issueKey.trim(), epicLink.trim());
                }
            }
            csvParser.close();
        } catch (IOException ioe) {
            logger.error(appProps.getProperty("DATAFILE") + " IOExp ", ioe);
        }

    }

    /**
     * Test Connection
     */
    private static void testConnectJira() {
        try {
            final JiraRestClient restClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(
                    new URI(appProps.getProperty("JIRA_SERVER")), appProps.getProperty("USER"),
                    appProps.getProperty("KEY"));
            try {
                Issue issueObj = restClient.getIssueClient().getIssue("TCL-30000").claim();
                Iterable<Attachment> attachments = issueObj.getAttachments();
                attachments.forEach(attached -> {
                    System.out.println(attached);
                    System.out.println(attached.getFilename());
                    System.out.println(attached.getContentUri());
                    System.out.println(attached.getThumbnailUri());

                    FileInputStream is;
                    try {
                        is = new FileInputStream(attached.getContentUri().getRawPath());
                        // create output stream
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        // create buffer
                        byte[] b = new byte[1024];
                        // Read the input to the byte stream
                        int len = 0;
                        while ((len = is.read(b)) != -1) {
                            outputStream.write(b, 0, len);
                        }
                        is.close();
                        // convert to bytearray
                        final byte[] byteArray = outputStream.toByteArray();
                        outputStream.close();
                    } catch (FileNotFoundException exp) {
                        // TODO Auto-generated catch block
                        exp.printStackTrace();
                    } catch (IOException exp) {
                        // TODO Auto-generated catch block
                        exp.printStackTrace();
                    }

                    /*
                     * try {
                     * 
                     * FileUtils.copyURLToFile(attached.getContentUri().toURL(), new
                     * File(attached.getFilename()), 100000, 10000); // Check other API as file is
                     * corrupt
                     * 
                     * } catch (IOException exp) { exp.printStackTrace(); }
                     */
                    try {
                        String issueKey = "TCL-30112";
                        Issue issueObj1 = restClient.getIssueClient().getIssue("TCL-30112").claim();
                        restClient.getIssueClient()
                                .addAttachment(issueObj1.getAttachmentsUri(),
                                        new FileInputStream(new File(attached.getFilename())), attached.getFilename())
                                .claim();
                    } catch (RestClientException rex) {
                        logger.error("Err :", rex);
                    } catch (FileNotFoundException exp) {
                        exp.printStackTrace();
                    }

                });
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
