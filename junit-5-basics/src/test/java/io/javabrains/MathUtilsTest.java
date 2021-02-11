package io.javabrains;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

//The below @TestInstance annotation (optional to override junit default behavior) creates one instance of MathUtilsTest for the whole class when used Lifesysle.PER_CLASS
//Lifecycle.PER_METHOD creates instance of MathUtilsTest per method. Since PER_CLASS class level annotation created the instance of the class before hand, we can run the @BeforeAll method without being a static method.
//@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("When running MathUtils")
class MathUtilsTest {
	
	MathUtils mathUtils;
	
	//Below 2 dependencies JUnit performs dependency injection. 
	//We can provide our own custom provider and inject it using JUnit when required..
	TestInfo testInfo; 
	TestReporter testReporter;
	
	//The below method has to be static because.. Since the method annotated with @BeforeAll has to run even before the instance of the class gets created,
	//its not possible as per java language convention. When the method itself is inside the same class. So to run this the method should be static.
	@BeforeAll
	static void beforeAllInit() {
		System.out.println("This needs to run before all");
	}
	
	/*@BeforeEach
	void init() {
		//Instead of creating the mathUtils instance inside each test method, this is a JUnit (life cycle) way of creating the instance before each test method runs.
		mathUtils = new MathUtils();
	}*/
	
	@BeforeEach
	void init(TestInfo testInfo, TestReporter testReporter) {
		this.testInfo = testInfo;
		this.testReporter = testReporter;
		
		//Instead of creating the mathUtils instance inside each test method, this is a JUnit (life cycle) way of creating the instance before each test method runs.
		mathUtils = new MathUtils();
		testReporter.publishEntry("Running " + testInfo.getDisplayName() + " with tags " + testInfo.getTags());
	}
	
	@AfterEach
	void cleanup() {
		System.out.println("Cleaning up...");
	}

	//The @Test annotation acts as main method in plain java program and helps us running the test case
	@Test
	@DisplayName("Testing add method")
	void testAdd() {
		//fail("Not yet implemented"); //Failure
		//System.out.println("This test ran"); //Success
		
		//MathUtils mathUtils = new MathUtils();
		int expected = 2;
		int actual = mathUtils.add(1, 1);
		//assertEquals(expected, actual);
		//The third parameter in the assertEquals is for provoding additional information details as a convenience feature
		assertEquals(expected, actual, "The add method should add two numbers");
		assertEquals(5, mathUtils.add(1, 4));
	}
	
	@Test
	@DisplayName("multiply method")
	@Tag("Math")
	void testMultiply() {
		System.out.println("Running " + testInfo.getDisplayName() + " with tags " + testInfo.getTags());
		//The below reporting output goes to JUnit console log for time stamp logging
		testReporter.publishEntry("Running TestReporter" + testInfo.getDisplayName() + " with tags " + testInfo.getTags());
		//O/P -> ReportEntry [timestamp = 2021-01-24T21:40:15.852, value = 'Running TestReportermultiply method with tags [Math]']
		
		//assertEquals(4, mathUtils.multiply(2, 2), "should return the right product");
		//assertEquals(7, mathUtils.multiply(2, 3), "should return the right product");
		
		//replacement for above code
		assertAll(
				() -> assertEquals(4, mathUtils.multiply(2, 2)),
				() -> assertEquals(0, mathUtils.multiply(2, 0)),
				() -> assertEquals(-2, mathUtils.multiply(2, -1)),
				() -> assertEquals(1, mathUtils.multiply(-1, -1))
				);
	}
	@Test
	@Tag("Math")
	//@EnabledOnOs(OS.LINUX)
	void testDivide() {
		//MathUtils mathUtils = new MathUtils();
		//mathUtils.divide(1, 0);
		
		//The below assumptionValue can be determined by program logic. It allows to disable the actual test case if assumption goes wrong
		boolean isServerUp = true;
		assumeTrue(isServerUp);//If this doesn't fail, then only the actual test case gets executed
		assertThrows(ArithmeticException.class, () -> mathUtils.divide(1, 0), "Divide by zero should throw");
	}
	
	@Test
	@Tag("Circle")
	void testComputeCircleRadius() {
		assertEquals(314.1592653589793, mathUtils.computeCircleArea(10), "Should return right circle area");
	}
	
	/*
	//For repeating the same test case multiple number of times
	@RepeatedTest(3)
	void testComputeCircRadius() {
		//MathUtils mathUtils = new MathUtils();
		assertEquals(314.1592653589793, mathUtils.computeCircleArea(10), "Should return right circle area");
	}
	
	//If we want to execute different cases based on the repetition number, getting hold of repetition information
	@RepeatedTest(2)
	void testCompCircleRadius(RepetitionInfo repetitionInfo) {
		int currentRepetition = repetitionInfo.getCurrentRepetition();
		if(currentRepetition == 1)
			assertEquals(314.1592653589793, mathUtils.computeCircleArea(10), "Should return right circle area");
		if(currentRepetition == 2)
			assertEquals(314.1592653589793, mathUtils.computeCircleArea(10), "Should return right circle area");
	}
	*/
	
	@Test
	@Disabled //Skips this test case
	@DisplayName("TDD method. Should not run")
	void testDisabled() {
		fail("This test should be disabled");
	}
	
	//@Nested demonstration
	//We can have many such test methods to just test the functionality of add method depending upon the cases. So here we can use @Nested
	//We need to create a Nested class putting all the relevant test cases together as a group. It makes the add test cases grouped under AddTest class.
	@Nested
	@DisplayName("add method")
	@Tag("Math")
	class AddTest {
		@Test
		@DisplayName("when adding two positive numbers")
		void testAddPositive() {
			assertEquals(5, mathUtils.add(1, 4), "should return the right sum");
		}
		
		@Test
		@DisplayName("when adding two negative numbers")
		void testAddNegative() {
			int expected = -5;
			int actual = mathUtils.add(-1, -4);
			//assertEquals(expected, actual, "should return sum "+expected +" but returned "+actual);
			//Now every time the test case ran, the String in 3rd parameter is created/constructed every time. In case the test case depends on some resource creation, it will be an expensive/heavy process.
			//So since this 3rd parameter value is computed and passed to assertEquals(..) irrespective of whether its going to be used (if the test case fails) or not.
			//So We can compute this piece of code(String here) Lazily as well using a Lambda. i.e; JUnit executes this last piece of code only if the test case fails, if the test case passes, JUnit not going to execute this piece of code.
			//This small optimization provides a large flexibility
			assertEquals(expected, actual, () -> "should return sum "+expected +" but returned "+actual);
			
		}
		
		@Test
		@DisplayName("when adding positive & negative numbers")
		void testAddPositiveAndNegative() {
			assertEquals(2, mathUtils.add(-1, 3), "should return the right sum");
		}
	}
}
