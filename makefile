JCC = javac

# This will create .class files for all .java files found in the current directory
default: all

all:
    $(JCC) *.java

# This will clean up all .class files so you can start fresh
clean:
    $(RM) *.class

# This target is for running your main GPS GUI application
run: all
    java GpsGUI
