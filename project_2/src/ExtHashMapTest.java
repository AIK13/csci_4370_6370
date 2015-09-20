import static org.junit.Assert.*;

import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;



public class ExtHashMapTest {

	ExtHashMap<Integer, Integer> test;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void setUp() {
		test = new ExtHashMap(Integer.class, Integer.class, 10);
	}
	
	// Verifies put and get methods
	@Test
	public void testPutGet() {
		// Places key-value pairs into the map
		test.put(10,  25);
		test.put(24,  3);
		test.put(15,  30);
		test.put(18,  21);
		test.put(5,  10);
		test.put(12,  17);
		test.put(1, 5);
		test.put(6,  7);
		test.put(20, 20);
		test.put(25,  2);
		
		// Verifies that the get method returns the same values that were added
		assertEquals((int)test.get(10), 25);
		assertEquals((int)test.get(24), 3);
		assertEquals((int)test.get(15), 30);
		assertEquals((int)test.get(18), 21);
		assertEquals((int)test.get(5), 10);
		assertEquals((int)test.get(12), 17);
		assertEquals((int)test.get(1), 5);
		assertEquals((int)test.get(6), 7);
		assertEquals((int)test.get(20), 20);
		assertEquals((int)test.get(25), 2);
	}
	
	@Test
	public void testEntrySet() {
		
		// Places key-value pairs into the map
		testPutGet();
		
		// Creates the set representation
		Set<Entry<Integer, Integer>> testSet = test.entrySet();
		
		// Checks that the size of the set equals the number of pairs added
		assertEquals(testSet.size(), 10);
	}
}
