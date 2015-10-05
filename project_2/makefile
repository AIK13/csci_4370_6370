compile:
	javac src/*.java -d bin -cp junit-4.12.jar &> /dev/null
run:
	java -cp bin MovieDB
test:
	java -cp bin:junit-4.12.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore TableTest
	java -cp bin:junit-4.12.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore BpTreeMapTest
	java -cp bin:junit-4.12.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore ExtHashMapTest
clean:
	rm bin/*
