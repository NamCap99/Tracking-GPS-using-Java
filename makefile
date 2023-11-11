JCC = javac
JFLAGS = -g
# when CLASSPATH = /path/to/sodium/library/sodium.jar:.; /path/to/sodium/library/swidgets.jar:.
CLASSPATH = "C:\\Users\\kxeam\\Downloads\\Compressed\\W09\\sodium.jar;C:\\Users\\kxeam\\Downloads\\Compressed\\W09\\swidgets.jar;."
# type to run: java -cp "C:\Users\kxeam\Downloads\Compressed\W09\sodium.jar;C:\Users\kxeam\Downloads\Compressed\W09\swidgets.jar;." GpsGUI


default: all

all: 
	$(JCC) $(JFLAGS) -cp $(CLASSPATH) *.java

clean: 
	$(RM) *.class
