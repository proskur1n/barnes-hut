RUN_OPTIONS := -classpath build:lib/CodeDraw.jar
MAIN_FILE := Simulation
TEST_FILE := Test

compile: build/$(MAIN_FILE).class

run: compile
	java $(RUN_OPTIONS) $(MAIN_FILE)

clean:
	rm -rf build

test: build/$(TEST_FILE).class
	java $(RUN_OPTIONS) $(TEST_FILE)

build/%.class: src/*.java
	javac -d build -sourcepath src -classpath lib/CodeDraw.jar src/$*.java
