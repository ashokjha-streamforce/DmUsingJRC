from jira import JIRA
from jira.resources import User
import pandas as pd 
import math
import os
import datetime
import json

username = '<USER NAME>'
password = '<KEY>'
the_url = "<SERVER>"
jira = JIRA({"server": the_url}, basic_auth=(username, password))
dtf = pd.read_csv('TCL JIRA TR Combined Data Feb 2023 - 3_23_2023.csv', header = 0, sep = ',')

value = 'ali.al-asadi'
#User Query
params = {
    'username': value,
    'includeActive': True,
    'includeInactive': True
}

list_search = jira._fetch_pages(
    item_type=User,
    items_key=None,
    request_path='user/search',
    params=params
)
jira_user= list_search[0]
lisstIssues = ["TCL-30118","TCL-30117","TCL-30116","TCL-30115","TCL-30114","TCL-30113","TCL-30112","TCL-30111","TCL-30110","TCL-30109","TCL-30108","TCL-30107","TCL-30106","TCL-30105","TCL-30104","TCL-30103","TCL-30094","TCL-30091","TCL-30090","TCL-30089","TCL-30088","TCL-30085","TCL-30083","TCL-30049","TCL-30021","TCL-30020","TCL-29985","TCL-29984","TCL-29958","TCL-29870","TCL-29869","TCL-29868","TCL-29867","TCL-29866","TCL-29842","TCL-29837","TCL-29836","TCL-29812","TCL-29803","TCL-29801","TCL-29796","TCL-29795","TCL-29794","TCL-29793","TCL-29792","TCL-29782","TCL-29780","TCL-29778","TCL-29773","TCL-29762","TCL-29761","TCL-29760","TCL-29759","TCL-29758","TCL-29757","TCL-29753","TCL-29752","TCL-29749","TCL-29724","TCL-29667","TCL-29666","TCL-29663","TCL-29661","TCL-29658","TCL-29655","TCL-29650","TCL-29639","TCL-29637","TCL-29629","TCL-29628","TCL-29620","TCL-29619","TCL-29618","TCL-29617","TCL-29616","TCL-29615","TCL-29614","TCL-29613","TCL-29612","TCL-29611","TCL-29608","TCL-29607","TCL-29606","TCL-29605","TCL-29604","TCL-29603","TCL-29602","TCL-29572","TCL-29571","TCL-29569","TCL-29568","TCL-29567","TCL-29566","TCL-29564","TCL-29563","TCL-29554","TCL-29553","TCL-29552","TCL-29551","TCL-29550","TCL-29549","TCL-29548","TCL-29547","TCL-29546","TCL-29545","TCL-29530","TCL-29529","TCL-29528","TCL-29527","TCL-29525","TCL-29507","TCL-29504","TCL-29484","TCL-29483","TCL-29482","TCL-29481","TCL-29480","TCL-29479","TCL-29478","TCL-29477","TCL-29476","TCL-29475","TCL-29451","TCL-29450","TCL-29449","TCL-29448","TCL-29447","TCL-29446","TCL-29445","TCL-29444","TCL-29443","TCL-29442","TCL-29441","TCL-29440","TCL-29439","TCL-29438","TCL-29437","TCL-29436","TCL-29435","TCL-29434","TCL-29433","TCL-29373","TCL-29362","TCL-29356","TCL-29355","TCL-29354","TCL-29353","TCL-29323","TCL-29322","TCL-29299","TCL-29298","TCL-29297","TCL-29296","TCL-29288","TCL-29287","TCL-29277","TCL-29255","TCL-29171"] 
for index, row in dtf.iterrows():
#for row in lisstIssues:
    issueKey = row['Issue key']
    issueType = row['Issue Type'].strip()
    #issueKey = row  
    
    #value = row['Inward issue link']                                 #customfield_10003
    #value = row['Inward issue link']                                 #customfield_10500
    #value = row['Inward issue link']                                 #customfield_10500
    #value = row['Inward issue link (Cloners)']                       #customfield_10001
    
    #value = row['Custom field (Epic Status)']                        #customfield_10010
    #value = row['Custom field (Review Count)']    
    #value = row['Custom field (Course Name)']
    #value = row['Custom field (Assigned to Function)']               #customfield_19804
    #value = row['Custom field (Client Review Date)']                 #customfield_19901  
    #value = row['Custom field (Type of Defect)']  	                  #customfield_19817
    value = 'ali.al-asadi' # row['Custom field (Technical Team)']                     #customfield_19906 ali.al-asadi
    #value = row['Custom field (QA Owner (Multi-User))']
    #value = row['Custom field (Product Owner)']                      #customfield_14502
    #value = row['Custom field (ID Resolver)']    
    #value = row['Custom field (ID Owner)']
    #value = row['Custom field (Development Team)']
    #value = row['Custom field (Defect Resolver)']
    #value = row['Custom field (Defect Owner)']
    #value = row['Custom field (QA Complete Date)']                  #customfield_19911 
    #value = row['Custom field (Issue Owned By)']                    #customfield_19806   
    #value = row['Custom field (Review Stage)']                      #customfield_19813 
    #value = row['Custom field (Quality Health)']                    #customfield_19812
    #value = row['Custom field (Test Scope)']                        #customfield_19815
    #value = row['Custom field (Profiler)']                          #customfield_19810 
    #value = row['Custom field (Platform)']                          #customfield_19809
    #value = row['Custom field (Operating System/Browser)']          #customfield_19816
    #value = row['Custom field (Issue Origin)']               	     #customfield_19805
    #value = row['Custom field (External issue ID)']                 #customfield_19802  
    #value = row['Custom field (Frame Count)']                       #customfield_19900
    #value = row['Custom field (Languages)']                         #customfield_19808
    #value = row['Custom field (Vendor)']                            #customfield_19814 
    #value = row['Custom field (Project Type)']                      #customfield_19811 
    

    
    if not isinstance(value,float) :
    #if not math.isnan(value):
        issue = jira.issue(issueKey)
        try:
        

            # Assignee
            #issue.update(fields={'assignee': jira_user.raw})
            #print(issueKey+ " updated for customfield_13330 with " + value)
            
            #customfield_13330
            #value=str(int(value))
            #issue.update(fields={'customfield_13330': {"value":value}})
            #print(issueKey+ " updated for customfield_13330 with " + value)

            #customfield_10010               #Custom field (Epic Status)
            #issue.update(fields={'customfield_10010': {'value':value.strip()}})
            #print(issueKey+ " updated for customfield_10010 with " + value)

            
            #customfield_19904
            #issue.update(fields={'customfield_19904': [value]})
            #print(issueKey+ " updated for customfield_19904 with " + value)
            

            #User Query
            #params = {
            #    'username': value,
            #    'includeActive': True,
            #    'includeInactive': True
            #}

            #list_search = jira._fetch_pages(
            #    item_type=User,
            #    items_key=None,
            #    request_path='user/search',
            #    params=params
            #)

            #jira_user= list_search[0]

            #customfield_19906      Custom field (Technical Team) 
            if(issueType != 'Epic'):
                issue.update(fields={'customfield_19906': [{"name" : value}]})
                #print(jira_user.raw)
                print(issueKey+ " updated for customfield_19906 with "+ value + " Type " + issueType)            
            #customfield_19905
            #issue.update(fields={'customfield_19905': [{'value':jira_user.raw}]})
            #print(issueKey+ " updated for customfield_19905 with " + value)             
            
            #customfield_14502               #Custom field (Product Owner)
            #issue.update(fields={'customfield_14502': jira_user.raw})
            #print(issueKey+ " updated for customfield_14502 with " + value) 
            
            #customfield_19908
            #issue.update(fields={'customfield_19908': jira_user.raw})
            #print(issueKey+ " updated for customfield_19908 with " + value)           
            
            #customfield_19902
            #issue.update(fields={'customfield_19902': jira_user.raw})
            #print(issueKey+ " updated for customfield_19902 with " + value)
            
            #customfield_19907
            #issue.update(fields={'customfield_19907': [{'value':jira_user.raw}]})
            #print(issueKey+ " updated for customfield_19907 with " + value)            
            
            #customfield_19903
            #issue.update(fields={'customfield_19903': jira_user.raw})
            #print(issueKey+ " updated for customfield_19903 with " + value)
            
            #customfield_19909
            #issue.update(fields={'customfield_19909': jira_user.raw})
            #print(issueKey+ " updated for customfield_19909 with " + value)


            #customfield_19901            #Custom field (Client Review Date) 
            #value = datetime.datetime.strptime(value, "%m/%d/%y %H:%M").strftime("%Y-%m-%d")
            #issue.update(fields={'customfield_19901': value})
            #print(issueKey+ " updated for customfield_19901 with " + value)
            
            #customfield_19911             #Custom field (QA Complete Date)
            #value = datetime.datetime.strptime(value, "%m/%d/%Y %H:%M").strftime("%Y-%m-%d")
            #issue.update(fields={'customfield_19911': value})
            #print(issueKey+ " updated for customfield_19911 with " + value)                        
            
            #customfield_19806      #Custom field (Issue Owned By)
            #issue.update(fields={'customfield_19806': {'value':value}})
            #print(issueKey+ " updated for customfield_19806 with " + value)

            #customfield_19817
            #defList= value.split("->")
            #if len(defList)==1 :
            #    issue.update(fields={'customfield_19817': {'value':defList[0].strip()}})
            #else:
            #    issue.update(fields={'customfield_19817': {'value':defList[0].strip(),"child": {'value':defList[1].strip()}}})        
            #print(issueKey+ " updated for customfield_19817 with " + value)
            
            #customfield_19813      #Custom field (Review Stage)
            #issue.update(fields={'customfield_19813': {'value':value}})
            #print(issueKey+ " updated for customfield_19813 with " + value)
            
            #customfield_19812  Custom field (Quality Health)
            #issue.update(fields={'customfield_19812': {'value':value}})
            #print(issueKey+ " updated for customfield_19812 with " + value) 
            
            #customfield_19815              #Custom field (Test Scope)
            #issue.update(fields={'customfield_19815': [{'value':value}]})
            #print(issueKey+ " updated for customfield_19815 with " + value) 
        
            #customfield_19810        #Custom field (Profiler)
            #issue.update(fields={'customfield_19810': {'value':value}})
            #print(issueKey+ " updated for customfield_19810 with " + value) 
            
            #customfield_19809
            #issue.update(fields={'customfield_19809': [{'value':value}]})
            #print(issueKey+ " updated for customfield_19809 with " + value) 

        
            #customfield_19816         #Custom field (Operating System/Browser)
            #issue.update(fields={'customfield_19816': [{'value':value}]})
            #print(issueKey+ " updated for customfield_19816 with " + value) 

            #customfield_19802           #Custom field (External issue ID)
            #value=str(int(value))
            #issue.update(fields={'customfield_19802': value})
            #print(issueKey+ " updated for customfield_19802 with " + value)             
            
            #customfield_19804 Custom field (Assigned to Function)
            #issue.update(fields={'customfield_19804': {'value':value}})
            #print(issueKey+ " updated for customfield_19804 with " + value)     

            
            #customfield_19805   #Custom field (Issue Origin)
            #issue.update(fields={'customfield_19805': {'value':value}})
            #print(issueKey+ " updated for customfield_19805 with " + value)    
                      
        
            #customfield_19808    #Custom field (Languages)
            #issue.update(fields={'customfield_19808': [{'value':value}]})
            #print(issueKey+ " updated for customfield_19808 with " + value)
            
            #customfield_19811             #Custom field (Project Type)
            #issue.update(fields={'customfield_19811': [{'value':value}]})
            #print(issueKey+ " updated for customfield_19811 with " + value)

            #customfield_19814                       #Custom field (Vendor)
            #issue.update(fields={'customfield_19814': [{'value':value}]})
            #print(issueKey+ " updated for customfield_19814 with " + value)
            
            #customfield_19900                  #Custom field (Frame Count)
            #value=str(int(value))
            #issue.update(fields={'customfield_19900': value})
            #print(issueKey+ " updated for customfield_19900 with " + value)  
            
            
        except Exception as error:      

            print("Value = ", value, "Error ", str(error))
            #exit(0)

            #print(jira_user) 
            #for attr in dir(jira_user):
            #    # Getting rid of dunder methods
            #    if not attr.startswith("__"):
            #        print(attr, getattr(jira_user, attr))            
            #print(jira_user.raw)            
            #print(123)