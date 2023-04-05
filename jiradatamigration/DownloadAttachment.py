from jira import JIRA

import os

# LRN SERVER
username = 'ashok.jha'
password = 'PWD'
the_url = "https://enable.lrn.com/"
jira = JIRA({"server": the_url}, basic_auth=(username, password))

# SFS
# username = 'ashok.jha@streamforcesolutions.com'
# the_url = 'https://streamforcesolutions.atlassian.net/'
# token = "<TOKEN>"
# jira = JIRA({"server": the_url}, basic_auth=(username, token))

projectList = ['TCL', 'DTM']
project = projectList[0]
size = 100
initial = 0
while True:
    start = initial*size
    issues = jira.search_issues(
        "Project = '" + project + "'",  start, size)  # , maxResults=200)
    if len(issues) == 0:
        break
    initial += 1
    df_attachments = []
    for issue in issues:
        attachment_dict = {}
        issue_num = issue.key
        issue = jira.issue(issue_num, fields='attachment')
        attachments = issue.fields.attachment
        for attachment in issue.fields.attachment:
            dir_path = os.path.join(os.getcwd(), project, issue_num)
            os.makedirs(dir_path, exist_ok=True)
            with open(os.path.join(dir_path, attachment.filename), 'wb') as file:
                file.write(attachment.get())
