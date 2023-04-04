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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

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
            UpdateJiraTask.updateEpicLink();
            System.out.println("Update to JIRA Task is successful");
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
            String fileName = null;
            try {
                Issue issueObj = restClient.getIssueClient().getIssue("TCL-30000").claim();
                Iterable<Attachment> attachments = issueObj.getAttachments();
                attachments.forEach(attached -> {
                    System.out.println(attached);
                    System.out.println(attached.getFilename());
                    System.out.println(attached.getContentUri());
                    System.out.println(attached.getThumbnailUri());
                    try {
                        FileUtils.copyURLToFile(attached.getContentUri().toURL(), new File(attached.getFilename()),
                                100000, 10000);

                    } catch (IOException exp) {
                        // TODO Auto-generated catch block
                        exp.printStackTrace();
                    }
                    try {
                        String issueKey = "TCL-30112";
                        Issue issueObj1 = restClient.getIssueClient().getIssue("TCL-30112").claim();
                        restClient.getIssueClient()
                                .addAttachment(issueObj1.getAttachmentsUri(),
                                        new FileInputStream(new File(attached.getFilename())), attached.getFilename())
                                .claim();
                    } catch (RestClientException rex) {
                        // System.out.println(err);
                        logger.error("Err :", rex);
                    } catch (FileNotFoundException exp) {
                        // TODO Auto-generated catch block
                        exp.printStackTrace();
                    }

                });
                System.out.println("Issue " + issueObj);

                IssueInputBuilder builder = new IssueInputBuilder();
                // File fileToUpload = new File(path);
                // FileBody fileBody = new FileBody(fileToUpload, fileComment,
                // "application/octet-stream", "UTF-8");
                // builder.setFieldInput(new FieldInput(appProps.getProperty("ATTACHMENT"),
                // epicVal));
                // IssueInput epic = builder.build();
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

    /*
     * public void addAttachmentToJira(String filename, String jiraissue) throws
     * Exception { HttpHeaders headers = new HttpHeaders();
     * headers.setContentType(MediaType.MULTIPART_FORM_DATA);
     * headers.setBasicAuth(jiraUser, jiraPwd); headers.set("X-Atlassian-Token",
     * "no-check"); File sampleFile = new File(filename);
     * 
     * MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
     * ContentDisposition contentDisposition =
     * ContentDisposition.builder("form-data").name("file")
     * .filename(sampleFile.getName()).build();
     * fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
     * byte[] fileContent = Files.readAllBytes(sampleFile.toPath());
     * HttpEntity<byte[]> fileEntity = new HttpEntity<>(fileContent, fileMap);
     * MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
     * body.add("file", fileEntity);
     * 
     * HttpEntity<MultiValueMap<String, Object>> requestEntity = new
     * HttpEntity<>(body, headers); try { String addAttachUrl =
     * "https://yourjirainstance.com/rest/api/2/issue/" + issueId + "/attachments";
     * ResponseEntity<String> response = restTemplate.exchange(addAttachUrl,
     * HttpMethod.POST, requestEntity, String.class); } catch
     * (HttpClientErrorException e) { e.printStackTrace(); } }
     */

}
