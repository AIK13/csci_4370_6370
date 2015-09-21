
/************************************************************************************
 * @file BpTreeMap.java
 *
 * @author  John Miller
 */

import java.io.*;
import java.lang.reflect.Array;
import static java.lang.System.out;
import java.util.*;

/************************************************************************************
 * This class provides B+Tree maps.  B+Trees are used as multi-level index structures
 * that provide efficient access for both point queries and range queries.
 */
public class BpTreeMap <K extends Comparable <K>, V>
       extends AbstractMap <K, V>
       implements Serializable, Cloneable, SortedMap <K, V>
{
    /** The maximum fanout for a B+Tree node.
     */
    private static final int ORDER = 5;

    /** The class for type K.
     */
    private final Class <K> classK;

    /** The class for type V.
     */
    private final Class <V> classV;

    /********************************************************************************
     * This inner class defines nodes that are stored in the B+tree map.
     */
    private class Node
    {
        boolean   isLeaf;
        int       nKeys;
        K []      key;
        Object [] ref;
        @SuppressWarnings("unchecked")
        Node (boolean _isLeaf)
        {
            isLeaf = _isLeaf;
            nKeys  = 0;
            key    = (K []) Array.newInstance (classK, ORDER);
            if (isLeaf) {
                //ref = (V []) Array.newInstance (classV, ORDER);
                ref = new Object [ORDER + 1];
            } else {
                ref = (Node []) Array.newInstance (Node.class, ORDER + 1);
            } // if
        } // constructor
    } // Node inner class

    /** The root of the B+Tree
     */
    private Node root;

    /** The counter for the number nodes accessed (for performance testing).
     */
    private int count = 0;
    
    private int treeSize = 0;

    /********************************************************************************
     * Construct an empty B+Tree map.
     * @param _classK  the class for keys (K)
     * @param _classV  the class for values (V)
     */
    public BpTreeMap (Class <K> _classK, Class <V> _classV)
    {
        classK = _classK;
        classV = _classV;
        root   = new Node (true);
    } // constructor

    /********************************************************************************
     * Return null to use the natural order based on the key type.  This requires the
     * key type to implement Comparable.
     */
    public Comparator <? super K> comparator () 
    {
        return null;
    } // comparator

    /********************************************************************************
     * Return a set containing all the entries as pairs of keys and values.
     * @return  the set view of the map
     */
    public Set <Map.Entry <K, V>> entrySet ()
    {
        Set <Map.Entry <K, V>> enSet = new HashSet <> ();

        //  T O   B E   I M P L E M E N T E D
            
        return enSet;
    } // entrySet

    /********************************************************************************
     * Given the key, look up the value in the B+Tree map.
     * @param key  the key used for look up
     * @return  the value associated with the key
     */
    @SuppressWarnings("unchecked")
    public V get (Object key)
    {
        return find ((K) key, root);
    } // get

    /********************************************************************************
     * Put the key-value pair in the B+Tree map.
     * @param key    the key to insert
     * @param value  the value to insert
     * @return  null (not the previous value)
     */
    public V put (K key, V value)
    {
        insert (key, value, root, null);
        treeSize++;
        return null;
    } // put

    /********************************************************************************
     * Return the first (smallest) key in the B+Tree map.
     * @return  the first key in the B+Tree map.
     */
    
 
    public K firstKey () 
    {
    	
    	Node n = root;
    	K smallestKey = n.key[0];
    	    	
    	//if node is a leaf return the smallest key(should be the first)
    	if(n.isLeaf){
    		return smallestKey;
    	}
    	
    	//if node is not a leaf then set n to first node referenced by the current node
    	while(!n.isLeaf){
    	  n = (Node)n.ref[0];
    	  smallestKey = n.key[0];
    	}
    	    		
    	return smallestKey;
    } // firstKey

    /********************************************************************************
     * Return the last (largest) key in the B+Tree map.
     * @return  the last key in the B+Tree map.
     */
    public K lastKey () 
    {
    	Node n = root;
    	K largestKey = n.key[n.key.length - 2];
    	
    	if(n.isLeaf){
    		for(int i = n.key.length - 2; i >= 0; i--){
    			if(n.key[i] != null){
    				largestKey = n.key[i];
    				return largestKey;
    			}
    		}
			
		}
    	while(!n.isLeaf){
    		
    		for(int i = n.nKeys - 1 ; i >= 0; i--){
    			if(n.key[i]!= null){
    				n = (Node)n.ref[i + 1];
    				i = n.nKeys;
    				if(n.isLeaf){
    					for(int j = n.nKeys; j >= 0; j-- ){
    						if(n.key[j]!= null){
    							largestKey = n.key[j];
    							break;
    						}
    					}
    				break;
    				
    				}
    			}
    		}
    		
    		
    	}
    		
    	return largestKey;

        
    } // lastKey

    /********************************************************************************
     * Return the portion of the B+Tree map where key < toKey.
     * @return  the submap with keys in the range [firstKey, toKey)
     */
    public SortedMap <K,V> headMap (K toKey)
    {
        return subMap(null, toKey);
    } // headMap

    /********************************************************************************
     * Return the portion of the B+Tree map where fromKey <= key.
     * @return  the submap with keys in the range [fromKey, lastKey]
     */
    public SortedMap <K,V> tailMap (K fromKey)
    {
    	return subMap(fromKey, null);
    } // tailMap

    /********************************************************************************
     * Return the portion of the B+Tree map whose keys are between fromKey and toKey,
     * i.e., fromKey <= key < toKey.
     * @return  the submap with keys in the range [fromKey, toKey)
     */
    public SortedMap <K,V> subMap (K fromKey, K toKey)
    {
    	// Create empty submap
    	BpTreeMap <K, V> newMap = new BpTreeMap <> (classK, classV);
    	
    	// Set start and end keys
    	K first = fromKey == null ? firstKey() : fromKey;
    	K second = toKey == null ? lastKey() : toKey;
    	
    	// Get a list of all entries
    	Set<Entry<K, V>> entries = this.entrySet();
    	
    	// Loop through all entries
    	for (Entry<K, V> e : entries)
    	{
    		// If the entry is within the parameters
    		if (e.getKey().compareTo(first) >= 0 && 
    				e.getKey().compareTo(second) <= 0)
    		{
    			// Add the entry to the subtree
    			newMap.put(e.getKey(), e.getValue());
    		}
    	}
    	
        return newMap;
    } // subMap

    /********************************************************************************
     * Return the size (number of keys) in the B+Tree.
     * @return  the size of the B+Tree
     */
    public int size ()
    {
        return treeSize;
    } // size

    /********************************************************************************
     * Print the B+Tree using a pre-order traveral and indenting each level.
     * @param n      the current node to print
     * @param level  the current level of the B+Tree
     */
    @SuppressWarnings("unchecked")
    private void print (Node n, int level)
    {
        for (int j = 0; j < level; j++) out.print ("\t");
        out.print ("[ . ");
        for (int i = 0; i < n.nKeys; i++) out.print (n.key [i] + " . ");
        out.println ("]");
        if ( ! n.isLeaf) {
            for (int i = 0; i <= n.nKeys; i++) print ((Node) n.ref [i], level + 1);
        } // if
    } // print

    /********************************************************************************
     * Recursive helper function for finding a key in B+trees.
     * @param key  the key to find
     * @param ney  the current node
     */
    @SuppressWarnings("unchecked")
    private V find (K key, Node n)
    {
        count++;
        for (int i = 0; i < n.nKeys; i++) {
            K k_i = n.key [i];
            if (key.compareTo(k_i) <= 0) {
                if (n.isLeaf) {
                    return (key.equals (k_i)) ? (V) n.ref [i] : null;
                } else {
                    return find (key, (Node) n.ref [i]);
                } // if
            } // if
        } // for
        return (n.isLeaf) ? null : find (key, (Node) n.ref [n.nKeys]);
    } // find
    
    /********************************************************************************
     * Recursive helper function for inserting a key in B+trees.
     * @param key  the key to insert
     * @param ref  the value/node to insert
     * @param n    the current node
     * @param p    the parent node
     */
    private void insert (K key, V ref, Node n, Node p)
    {
        if (n.isLeaf)
        {
        	if (n.nKeys < ORDER - 1) {
                for (int i = 0; i < n.nKeys; i++) {
                    K k_i = n.key [i];
                    if (key.compareTo(k_i) < 0) {
                        wedge (key, ref, n, i);
                        return;
                    } else if (key.equals (k_i)) {
                        out.println ("BpTreeMap:insert: attempt to insert duplicate key = " + key);
                    }
                }
                wedge (key, ref, n, n.nKeys);
            } else {
                Node sib = split (key, ref, n);
                parentInsert(sib, p);
            }
        }
        else
        {
        	boolean didInsert = false;
        	for (int i = 0;i < n.nKeys;i ++)
        	{
        		K nKey = n.key[i];
        		if (key.compareTo(nKey) < 0)
        		{
        			insert (key, ref, (Node) n.ref[i], n);
        			didInsert = true;
        			break;
        		}
        	}
        	
        	if (!didInsert) {
        		insert (key, ref, (Node) n.ref[n.nKeys], n);
        	}
        	
        	if (n.nKeys > ORDER - 1)
        	{
        		Node sib = split (null, null, n);
                parentInsert(sib, p);
        	}
        	
        }
    } // insert
    
    private void parentInsert (Node sib, Node p)
    {
    	if (p == null)
    	{
    		root = sib;
    	}
    	else
    	{
	    	Node newLeft = (Node) sib.ref[0];
	        Node newRight = (Node) sib.ref[1];
	        K middle = sib.key[0];
	        
	        boolean didWedge = false;
	        for (int i = 0;i < p.nKeys;i ++)
	        {
	        	K pKey = p.key[i];
	        	if (middle.compareTo(pKey) < 0)
	        	{
	        		wedge (middle, newLeft, p, i);
	        		p.ref[i + 1] = newRight;
	        		didWedge = true;
	        		break;
	        	}
	        }
	        
	        if (!didWedge)
	        {
	        	wedge (middle, newLeft, p, p.nKeys);
        		p.ref[p.nKeys] = newRight;
	        }
    	}
    }
    
    /********************************************************************************
     * Wedge the key-ref pair into node n.
     * @param key  the key to insert
     * @param ref  the value/node to insert
     * @param n    the current node
     * @param i    the insertion position within node n
     */
    private void wedge (K key, Object ref, Node n, int i)
    {
        for (int j = n.nKeys; j > i; j--) {
            n.key [j] = n.key [j - 1];
            n.ref [j] = n.ref [j - 1];
        } // for
        n.key [i] = key;
        n.ref [i] = ref;
        n.nKeys++;
    } // wedge
    
    
   


    /********************************************************************************
     * Split node n and return the newly created node.
     * @param key  the key to insert
     * @param ref  the value/node to insert
     * @param n    the current node
     */
    private Node split (K key, Object ref, Node n)
    {
        //out.println ("split not implemented yet");
    	
    	//loop through node to wedge in new key before splitting
    	if(key != null){
    		boolean didWedge = false;
    		for(int i = 0; i < n.nKeys; i++ ){
    			if(key.compareTo(n.key[i]) < 0){
    				wedge(key, ref, n, i);
    				didWedge = true;
    				break;
    			}
    		}
    		if (!didWedge)
    		{
    			wedge (key, ref, n, n.nKeys);
    		}
    		
    	}
        
        //split on the middle key
        int middleIndex = (int)(ORDER/2);
        K middleKey = n.key[middleIndex];
        if(!n.isLeaf){
        	n.key[middleIndex] = null;
        }
        
        //create new nodes for the split
        Node newParent = new Node(false);
        Node firstChild = new Node(n.isLeaf);
        Node secondChild = new Node(n.isLeaf);
       
        //filling new children with keys and values
        
        for(int i = 0; i < n.nKeys; i++){
        	if(n.key[i] == null){
        		firstChild.ref[i] = n.ref[i];
        	}
        	else
        	{
	        	if(n.key[i].compareTo(middleKey) <= 0){
	        		wedge(n.key[i], n.ref[i], firstChild, firstChild.nKeys);
	        	}
	        	else{
	        		wedge(n.key[i], n.ref[i], secondChild, secondChild.nKeys);
	        	}
        	}
        }
        
        if(!n.isLeaf){
        	secondChild.ref[secondChild.nKeys] = n.ref[n.nKeys];
        }
        else{
        	//TODO add child reference
        }
        
        //setting up new parent node
        newParent.key[0] = middleKey;
        newParent.nKeys = 1;
        newParent.ref[0] = firstChild;
        newParent.ref[1] = secondChild;

        return newParent;
    } // split

    /********************************************************************************
     * The main method used for testing.
     * @param  the command-line arguments (args [0] gives number of keys to insert)
     */
    public static void main (String [] args)
    {
        BpTreeMap <Integer, Integer> bpt = new BpTreeMap <> (Integer.class, Integer.class);
        int totKeys = 100;
        if (args.length == 1) totKeys = Integer.valueOf (args [0]);
        
        for (int i = 1; i < totKeys; i += 1)
        {
        	bpt.put (i, i * i);
        	out.println("Putting " + i);
        }
        
        out.println();
        out.println ("Printing BpTreeMap...");
        out.println ("-------------------------------------------");
        bpt.print (bpt.root, 0);
        out.println ("-------------------------------------------");
        out.println();
        
        for (int i = 0; i < totKeys; i++) {
            out.println ("key = " + i + " value = " + bpt.get (i));
        } // for
        out.println ("-------------------------------------------");
        out.println ("Average number of nodes accessed = " + bpt.count / (double) totKeys);
       
       
    } // main

} // BpTreeMap class

