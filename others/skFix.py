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
  
    os.system("ant -Dbuild.compile=javac1.6   -f "+ defect4j+"/framework/projects/defects4j.build.xml    -Dd4j.home="+defect4j+"    -Dbasedir="+os.getcwd()+"  instrument.tests ")

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
        tmp=script+clsList[i]+"  -Dtest.entry.method="+mtdList[i]
        print tmp
        os.system(tmp)
        os.system("cp .trace_state.txt .trace_state"+str(i)+".txt")

    os.system("ant -Dbuild.compile=javac1.6   -f "+ defect4j+"/framework/projects/defects4j.build.xml    -Dd4j.home="+defect4j+"    -Dbasedir="+os.getcwd()+"  sketchFix.repair ")
        
        
_main_()
