
/************************************************************************************
 * @file ExtHashMap.java
 *
 * @author  John Miller
 */

import java.io.*;
import java.lang.reflect.Array;
import static java.lang.System.out;
import java.util.*;

/************************************************************************************
 * This class provides hash maps that use the Extendable Hashing algorithm.  Buckets
 * are allocated and stored in a hash table and are referenced using directory dir.
 */
public class ExtHashMap <K, V>
       extends AbstractMap <K, V>
       implements Serializable, Cloneable, Map <K, V>
{
    /** The number of slots (for key-value pairs) per bucket.
     */
    private static final int SLOTS = 4;

    /** The class for type K.
     */
    private final Class <K> classK;

    /** The class for type V.
     */
    private final Class <V> classV;

    /********************************************************************************
     * This inner class defines buckets that are stored in the hash table.
     */
    private class Bucket
    {
        int  nKeys;
        int localDepth;
        int bucketNum;
        K [] key;
        V [] value;
        @SuppressWarnings("unchecked")
        Bucket ()
        {
            nKeys = 0;
            localDepth = 0;
            key   = (K []) Array.newInstance (classK, SLOTS);
            value = (V []) Array.newInstance (classV, SLOTS);
        } // constructor
        
        public void setEqual(Bucket a){
        	key   = (K []) Array.newInstance (classK, SLOTS);
            value = (V []) Array.newInstance (classV, SLOTS);
            
        	nKeys = a.nKeys;
        	localDepth = a.localDepth;
        	for(int j = 0; j < a.nKeys; j++){
        		key[j] = a.key[j];
        		value[j] = a.value[j];
        	} // for
        } //setEqual
        
    } // Bucket inner class

    /** The hash table storing the buckets (buckets in physical order)
     */
    private final List <Bucket> hTable;

    /** The directory providing access paths to the buckets (buckets in logical oder)
     */
    private final List <Bucket> dir;

    /** The modulus for hashing (= 2^D) where D is the global depth
     */
    private int mod;

    /** The number of buckets
     */
    private int nBuckets;

    /** Counter for the number buckets accessed (for performance testing).
     */
    private int count = 0;

    /** The global depth of the hashing table
     */
     private int globalDepth = 0;
     private int currentBucket = 1;

    /********************************************************************************
     * Construct a hash table that uses Extendable Hashing.
     * @param classK    the class for keys (K)
     * @param classV    the class for keys (V)
     * @param initSize  the initial number of buckets (a power of 2, e.g., 4)
     */
    public ExtHashMap (Class <K> _classK, Class <V> _classV, int initSize)
    {
        classK = _classK;
        classV = _classV;
        hTable = new ArrayList <> ();   // for bucket storage
        dir    = new ArrayList <> ();   // for bucket access
        mod    = nBuckets = initSize;
        //globalDepth = 1;

        //calculate global depth
        for (int i = nBuckets; i > 1; i = i/2) {
        	globalDepth++;
        }
        
        for (int i = 0; i < nBuckets; i++) {
            Bucket newBucket = new Bucket();

            newBucket.bucketNum = currentBucket;
            currentBucket++;
            newBucket.localDepth = globalDepth;
            hTable.add(newBucket);
            dir.add(newBucket);
        } //for

    } // constructor

    /********************************************************************************
     * Return a set containing all the entries as pairs of keys and values.
     * @return  the set view of the map
     */
    public Set <Map.Entry <K, V>> entrySet ()
    {
        Set <Map.Entry <K, V>> enSet = new HashSet <> ();

        //  Implemented by Ashley Bennett
        for (Bucket curr : dir) { // iterates through buckets in the directory
            for (int i = 0; i < curr.nKeys; i++) { //iterates through keys in each bucket
              enSet.add(new AbstractMap.SimpleEntry<K, V>(curr.key[i], curr.value[i]));
            } // for
        } // for

        return enSet;
    } // entrySet

    /********************************************************************************
     * Given the key, look up the value in the hash table.
     * @param key  the key used for look up
     * @return  the value associated with the key
     */
    public V get (Object key)
    {
        int    i = h (key);
        Bucket b = dir.get (i);

        // Implemented by Ashley Bennett
        // Iterates through keys, and returns the value associated with the parameter key
        for (int j = 0; j < b.nKeys; j++) {
          if (key.equals(b.key[j]))
              return b.value[j];
        } //for

        return null;
    } // get


    /********************************************************************************
     * Splits the bucket and disperses its members.
     * @param Bucket  the bucket to split
     */
     public void split(Bucket b, int index) {
       // implemented by Ashley Bennett
       this.count++;

       // if the global depth is less than or equal to the local depth
       // double the directory
       if (this.globalDepth <= b.localDepth) {
            this.globalDepth++;
            b.localDepth++;

            // duplicate directory
            // set members in correct bucket
            for (int j = mod; j < mod * 2; j++) {
            	dir.add(new Bucket());
            	if (h(j) != index) {
            		 dir.set(j, dir.get(h(j)));
            	} // if
            	else {
                	 mod = mod * 2;
                	 Bucket curr = new Bucket();
                	 Bucket newBucket = new Bucket();
                	 newBucket.localDepth = b.localDepth;
                	 curr.localDepth = globalDepth;
                	 curr.nKeys = 0;
                	 newBucket.nKeys = 0;
                	 
                	 for (int i = 0; i < b.nKeys; i++) {
                		 if (h(b.key[i]) >= mod/2) {
                			 curr.key[curr.nKeys] = b.key[i];
                			 curr.value[curr.nKeys] = b.value[i];
                			 curr.nKeys++;
                		 } // if
                		 else {
                			 newBucket.key[newBucket.nKeys] = b.key[i];
                			 newBucket.value[newBucket.nKeys] = b.value[i];
                			 newBucket.nKeys++;
                		 } // else
                	 } // for
                	 
                	 // b becomes the new bucket
                	 b.setEqual(newBucket);
                	 dir.set(index + mod/2, curr);
                	 hTable.add(curr);
                	 mod = mod/2;
                	 
                	 curr.bucketNum = currentBucket;
                	 currentBucket++;      	 
                } // else
           } // for;
            mod = mod * 2;
       } // if
       else {
    	    b.localDepth++;
    	    Bucket curr = new Bucket();
    	    Bucket newBucket = new Bucket();
        	newBucket.localDepth = b.localDepth;
        	curr.localDepth = b.localDepth;
        	curr.nKeys = 0;
        	newBucket.nKeys = 0;
        	curr.bucketNum = currentBucket;
        	currentBucket++;
        	for (int j = 0; j < b.nKeys; j++) {
        		if (h(b.key[j]) >= mod/2) {
        			curr.key[curr.nKeys] = b.key[j];
        			curr.value[curr.nKeys] = b.value[j];
        			curr.nKeys++;
        		} // if
        		else {
        			newBucket.key[newBucket.nKeys] = b.key[j];
        			newBucket.value[newBucket.nKeys] = b.value[j];
        			newBucket.nKeys++;
        		} //else
        	} // for
        	
        	// b becomes new bucket
        	b.setEqual(newBucket);
        	
        	// update directory
        	if (curr.nKeys > 0) {
        		int index1 = h(curr.key[0]);
        		while(index1 < dir.size()){
        			dir.set(index1, curr);
        			index1 += mod;
        		}
        	} // if
        	if (b.nKeys > 0) {
        		int index1 = h(b.key[0]);
        		while(index1 < dir.size()){
        			dir.set(index1, b);
        			index1 +=mod;
        		}
        	}
        	hTable.add(curr);
        	if (b.nKeys == (SLOTS+1))
        		split(b, index);
        	if (curr.nKeys == (SLOTS+1))
        		split(curr, index);
       } // else
       
       nBuckets = hTable.size();
        
    } // split

    /********************************************************************************
     * Put the key-value pair in the hash table.
     * @param key    the key to insert
     * @param value  the value to insert
     * @return  null (not the previous value)
     */
    public V put (K key, V value)
    {
        //Check that a key is given.
        if (key == null)
          return null;

        int    index = h (key);
        Bucket b = dir.get(index);

        // Implemented by Ashley Bennett
        
        // adds key-value pair to the bucket if there is room
        if (b.nKeys < SLOTS) {
               b.key[b.nKeys] = key;
              b.value[b.nKeys] = value;
              b.nKeys++;
        }

        // if b constains more keys than there are slots, split is called on the bucket and table index
        else {
        	split(b, index);
        }
        
        return null;
      } // put

    /********************************************************************************
     * Return the size (SLOTS * number of buckets) of the hash table.
     * @return  the size of the hash table
     */
    public int size ()
    {
        return SLOTS * nBuckets;
    } // size

    /********************************************************************************
     * Print the hash table.
     */
    private void print ()
    {
        out.println ("Hash Table");
        out.println ("-------------------------------------------");

        // Implemented by Ashley Bennett
        int bCount = 0; //number of buckets
        int iCount = 0; //number of key-value pairs (items)

        for (Bucket curr : dir) {
            out.println("\nBucket #" + bCount + ":");
            bCount++;

            for (int i = 0; i < curr.nKeys; i++) {
                out.print("Item #" + iCount + ": ");
                out.println(curr.value[i].toString());

                iCount++;
            } // for
        } // for

        out.println ("-------------------------------------------");
    } // print

    /********************************************************************************
     * Hash the key using the hash function.
     * @param key  the key to hash
     * @return  the location of the directory entry referencing the bucket
     */
    private int h (Object key)
    {
        return key.hashCode () % mod;
    } // h

    /********************************************************************************
     * The main method used for testing.
     * @param  the command-line arguments (args [0] gives number of keys to insert)
     */
    public static void main (String [] args)
    {
        ExtHashMap <Integer, Integer> ht = new ExtHashMap <> (Integer.class, Integer.class, 11);
        int nKeys = 30;
        if (args.length == 1) nKeys = Integer.valueOf (args [0]);
        for (int i = 1; i < nKeys; i += 2) ht.put (i, i * i);
        ht.print ();
        for (int i = 0; i < nKeys; i++) {
            out.println ("key = " + i + " value = " + ht.get (i));
        } // for
        out.println ("-------------------------------------------");
        out.println ("Average number of buckets accessed = " + ht.count / (double) nKeys);
        
        /********** Additional Testing ************/
        out.println("\nTest Two\n");
        ExtHashMap <Integer, Integer> test = new ExtHashMap <> (Integer.class, Integer.class, 12);
        nKeys = 40;
        if (args.length == 1) nKeys = Integer.valueOf (args [0]);
        for (int i = 0; i < nKeys; i++)
          test.put(i, i*i*i);

        for (int i = 0; i < nKeys; i++)
          out.println("Key = " + i + " Value = " + test.get(i));

        out.println ("-------------------------------------------");
        out.println ("Average number of buckets accessed = " + test.count / (double) nKeys);

        out.print("\n\nEntry Set" + "\n-------------------------------------------" + "\n\n");
        for (Map.Entry<Integer, Integer> entry : test.entrySet()) {
            System.out.println("Key = " + entry.getKey() + " Value = " + entry.getValue());
        }

        out.println("\n");
        test.print();
        
    } // main
} // ExtHashMap class
