JAVAC = javac
JAVACFLAGS = -classpath ".:lib/jgrapht-core-0.9.1.jar:src/" -d "./"
.SUFFIXES : .class .java
.java.class :
	$(JAVAC) $(JAVACFLAGS) $(SRC)
SRC= src/Scheduler.java \
	src/Instruction.java

CLS= $(SRC:.java=.class)


all:  $(CLS)

clean:
	rm *.class

