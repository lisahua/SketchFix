#!/usr/bin/python

import os
import string


#read file to get failing tests
def _main_():
    tests=""
    fname="defects4j.build.properties"
    defect4j=os.environ["DEFECT4J"]
    print defect4j
    script="ant -Dbuild.compile=javac1.6   -f "+ defect4j+"/framework/projects/defects4j.build.xml    -Dd4j.home="+defect4j+"    -Dbasedir="+os.getcwd()+"   run.dev.tests 2>&1  -Dtest.entry.class="
    
    f= open(fname,"r")
    content = f.readlines()
    for lin in content:
        if lin.startswith("d4j.tests.trigger"):
            tests = lin
        
    token = tests.split("=")
    token2 = token[1].split(",");
    clsList = []
    mtdList = []
    # create cmd
    for tmp in token2:
        token = tmp.split("::")
        clsList.append(token[0])
        mtdList.append(token[1])

    for i in range(0,len(clsList)):
        tmp=script+clsList[0]+"  -Dtest.entry.method="+mtdList[i]
        print tmp
        os.system(tmp)
        os.system("cp .trace_state.txt .trace_state.txt"+str(i))


_main_()