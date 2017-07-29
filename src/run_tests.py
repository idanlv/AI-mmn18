from os import system,chdir,getcwd,path
import sys
import time
 
system("md res >NUL 2> NUL")
print ("Results can be found in \"res\" folder")
 
print ("---------------------"+"Compiling"+"---------------------")
system("javac *.java")
 
main ="HW3"
print ("---------------------"+"Running "+ main + " program:"+"---------------------")
 
for c in [0,1]:
    for i in range(1,4):
        args1 =  "%d examples%d.txt notused.txt" % (c,i)
        command= "java " + main + " "+args1+" > "+"\""+"res\\"+args1+".txt"+"\""
        print ("\n\n\n" + command)
        system(command)
for c in [2,3]:
    for i in range(1,4):
        for j in range(1,4):
            args1 =  "%d examples%d.txt examples%d.txt" % (c,i,j)
            command= "java " + main + " "+args1+" > "+"\""+"res\\"+args1+".txt"+"\""
            print ("\n\n\n" + command)
            system(command)

time.sleep(1)            
out_file_name = "res\\joined_results.txt"
joined_txt = ""
for c in [0,1]:
    for i in range(1,4):
        args1 =  "%d examples%d.txt notused.txt" % (c,i)
        with open("res\\"+args1+".txt") as args1_file:            
            joined_txt+=args1_file.read()+"\n"
for c in [2,3]:
    for i in range(1,4):
        for j in range(1,4):
            args1 =  "%d examples%d.txt examples%d.txt" % (c,i,j)
            with open("res\\"+args1+".txt") as args1_file:            
                joined_txt+=args1_file.read()+"\n"

with open(out_file_name,'w') as out_file:
    out_file.write(joined_txt)