import static org.junit.Assert.*;
import org.junit.Test;


public class firstKey_tester {
	 
	BpTreeMap <Integer, Integer> treeKeyTest = new BpTreeMap <> (Integer.class, Integer.class);

	@Test
	public void should_give_firstKey_test() {
		assertEquals(1, (long)treeKeyTest.firstKey());
	}

}
