import static java.lang.System.out;
import static org.junit.Assert.*;

import org.junit.Test;


public class BpTreeMapTest {
	
	public BpTreeMap createTree(){
		
		BpTreeMap <Integer, Integer> bpt = new BpTreeMap <> (Integer.class, Integer.class);
        int totKeys = 10;
                
        for (int i = 1; i < totKeys; i += 2)
        {
        	bpt.put (i, i * i);
        	
        }
        return bpt;
	}

	@Test
	public void testFirstKey() {
		
		BpTreeMap <Integer, Integer> tree = createTree();
		
		assertEquals(1, (int)tree.firstKey() );
	}
	
	
	@Test
	public void testLastKey() {
		
		BpTreeMap <Integer, Integer> tree = createTree();
		
		assertEquals(9, (int)tree.lastKey());
		
	}
	
	

}
