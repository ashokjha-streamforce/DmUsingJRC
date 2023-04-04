from  jira import JIRA
import os


jira = JIRA({"server": "https://enable.lrn.com/"}, basic_auth=("ashok.jha", "Mr1ty0muksh1yamamr1tat"))

project = 'TCL'
new_issues = jira.search_issues("Project = " + project + " ORDER BY issueKey DESC", maxResults=200)

df_attachments = []

for issue in new_issues:
    attachment_dict = {}
    print(issue.key)
    issue_num = issue.key

    issue = jira.issue(issue_num, fields='attachment')

    attachments =  issue.fields.attachment

    for attachment in issue.fields.attachment:

        dir_path = os.path.join(os.getcwd(), project, issue_num)  # will return 'PROJECT/ISSUEKEY-XX'
        #print(dir_path)
        os.makedirs(dir_path, exist_ok=True)                             # create directory [current_path]/PROJECT/ISSUEKEY-XX
        with open(os.path.join(dir_path, attachment.filename), 'wb') as file:
            print("Saving file: " + str(file))
            file.write(attachment.get())