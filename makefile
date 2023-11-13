JCC = javac
JFLAGS = -g
# CLASSPATH = /path/to/sodium/library/sodium.jar:/path/to/sodium/library/swidgets.jar.
CLASSPATH = "C:\\Users\\kxeam\\Downloads\\Compressed\\W09\\sodium.jar;C:\\Users\\kxeam\\Downloads\\Compressed\\W09\\swidgets.jar;."

default: all

all: 
	$(JCC) $(JFLAGS) -cp $(CLASSPATH) *.java

clean: 
	$(RM) *.class

run:
	java -cp $(CLASSPATH) GpsGUI
