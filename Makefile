.SUFFIXES: .java .class
all: oneway/g3/Player.class oneway/sim/oneway.class

oneway/g3/Player.class: oneway/g3/*.java oneway/sim/Player.java
	javac $^

oneway/sim/oneway.class: oneway/sim/*.java
	javac $^

.PHONY: run
run: all
	java oneway.sim.Oneway g3

.PHONY: clean
clean:
	$(RM) oneway/g3/*.class
	$(RM) oneway/sim/*.class