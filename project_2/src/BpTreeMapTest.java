import static java.lang.System.out;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.junit.Test;


public class BpTreeMapTest {
	
	/***********************************************************************
	 * This method creates a new B+ tree for use in the unit testing
	 * @return the newly created tree
	 */
	public BpTreeMap createTree(){
		
		//create tree
		BpTreeMap <Integer, Integer> bpt = new BpTreeMap <> (Integer.class, Integer.class);
        int totKeys = 10;
        
        //loop through adding keys and values to the tree        
        for (int i = 1; i <= totKeys; i ++)
        {
        	bpt.put (i, i * i);
        	
        }
        return bpt;
	}

	/*************************************************************************
	 * Tests the firstKey method
	 */
	@Test
	public void testFirstKey() {
		
		BpTreeMap <Integer, Integer> tree = createTree();
		
		assertEquals(1, (int)tree.firstKey() );
	}
	
	/*************************************************************************
	 * Tests the last key method
	 */
	@Test
	public void testLastKey() {
		
		BpTreeMap <Integer, Integer> tree = createTree();
		
		assertEquals(10, (int)tree.lastKey());
		
	}
	
	/**************************************************************************
	 * Tests the entrySet method 
	 */
	@Test
	public void testEntrySet()
	{
		BpTreeMap <Integer, Integer> tree = createTree();
		Set<Map.Entry<Integer, Integer>> set = tree.entrySet();
		
		assertEquals(10, set.size());
	}
	
	/***************************************************************************
	 * Tests the subMap method which is used in the head and tail map methods
	 * as well
	 */
	@Test
	public void testSubMap()
	{
		BpTreeMap <Integer, Integer> tree = createTree();
		BpTreeMap <Integer, Integer> subTree = (BpTreeMap) tree.subMap(3, 7);
		
		assertEquals(null, subTree.get(2));
		assertEquals(9, (int) subTree.get(3));
		assertEquals(null, subTree.get(8));
	}
	
	/***************************************************************************
	 * Tests the size method
	 */
	@Test
	public void testSize()
	{
		BpTreeMap <Integer, Integer> tree = createTree();
		
		assertEquals(10, tree.size());
	}
}
