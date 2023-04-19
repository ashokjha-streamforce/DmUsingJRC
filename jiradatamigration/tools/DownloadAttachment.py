from jira import JIRA

import os

# LRN SERVER
username = '<USER NAME>'
password = '<KEY>'
the_url = "<URL>"
jira = JIRA({"server": the_url}, basic_auth=(username, password))


projectList = ['TCL','TPRCR','ACORNK']
project = projectList[0]
size = 1000
initial = 0
cnt = 1
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
        dir_path = os.path.join(os.getcwd(), project, issue_num)
        try: 
            os.makedirs(dir_path)  #, exist_ok=True)
        except OSError as error:
            print("Directory '%s' already created " %dir_path)      
        print(str(cnt) + " => "+ dir_path)
        cnt = cnt+1
        for attachment in issue.fields.attachment:
            attacmentFile  = os.path.join(dir_path, attachment.filename)
            if not os.path.isfile(attacmentFile): 
                with open(attacmentFile, 'wb') as file:
                    file.write(attachment.get())
                    print(attacmentFile)
                    

           
           
