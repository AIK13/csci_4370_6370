/************************************************************************************
 * @file LinHashMap.java
 *
 * @author  John Miller
 */

import java.io.*;
import java.lang.reflect.Array;
import static java.lang.System.out;
import java.util.*;

/************************************************************************************
 * This class provides hash maps that use the Linear Hashing algorithm.
 * A hash table is created that is an array of buckets.
 */
public class LinHashMap <K, V>
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
        int    nKeys;
        K []   key;
        V []   value;
        Bucket next;
        @SuppressWarnings("unchecked")
        Bucket (Bucket n)
        {
            nKeys = 0;
            key   = (K []) Array.newInstance (classK, SLOTS);
            value = (V []) Array.newInstance (classV, SLOTS);
            next  = n;
        } // constructor
    } // Bucket inner class

    /** The list of buckets making up the hash table.
     */
    private final List <Bucket> hTable;

    /** The modulus for low resolution hashing
     */
    private int mod1;

    /** The modulus for high resolution hashing
     */
    private int mod2;

    /** Counter for the number buckets accessed (for performance testing).
     */
    private int count = 0;

    /** The index of the next bucket to split.
     */
    private int split = 0;

    /********************************************************************************
     * Construct a hash table that uses Linear Hashing.
     * @param classK    the class for keys (K)
     * @param classV    the class for keys (V)
     * @param initSize  the initial number of home buckets (a power of 2, e.g., 4)
     */
    public LinHashMap (Class <K> _classK, Class <V> _classV, int initSize)
    {
        classK = _classK;
        classV = _classV;
        hTable = new ArrayList <> ();
        mod1   = initSize;
        mod2   = 2 * mod1;
        
        // adding empty buckets to hash table
        for(int i=0; i<initSize; i++){
        	hTable.add(new Bucket(null));
        }
        
    } // constructor

    /********************************************************************************
     * Return a set containing all the entries as pairs of keys and values.
     * @return  the set view of the map
     */
    public Set <Map.Entry <K, V>> entrySet ()
    {
    	Set <Map.Entry <K, V>> enSet = new HashSet <> ();

    	for( int i=0; i<hTable.size(); i++ ){ // loop through bucket
    		Bucket temp = hTable.get(i);
    		for( int j=0; j<temp.nKeys; j++ ){ // loop through values and keys
    			enSet.add(new AbstractMap.SimpleEntry<>( temp.key[j],temp.value[j]) );
    			//add keys and values to enSet (hashSet)
    		}
    	} // end for loop
    	return enSet;
    } // entrySet

    /********************************************************************************
     * Given the key, look up the value in the hash table.
     * @param key  the key used for look up
     * @return  the value associated with the key
     */
    public V get (Object key)
    {
    	int i = h (key);
    	if(i<split){
    		i=h2(key);
    	}
    	Bucket temp = hTable.get(i);
    	if( temp.nKeys==0 ){ // check if bucket is empty
    		return null;
    	}
    	else{ // get value from given key
    	   while(temp!=null){
    		   count++; // add to bucket counter
    		   for( int j=0; j<temp.nKeys; j++ ){
    			   if( key.equals(temp.key[j]) ){
    				   return temp.value[j];
    			   } // end if
    		   } // if for loop
    		   temp = temp.next; // if key not found, try next
    	   } // end while
    	} // end else

    	return null; 
    } // get

    /********************************************************************************
     * Put the key-value pair in the hash table.
     * @param key    the key to insert
     * @param value  the value to insert
     * @return  null (not the previous value)
     */
    public V put (K key, V value)
    {
       int i = h (key);
       if(i<split){
              i=h2(key);
       }
       Bucket temp = hTable.get(i);
       if( temp.nKeys < SLOTS ){ // simple insert, no split
              temp.key[temp.nKeys] = key;
              temp.value[temp.nKeys] = value;
              temp.nKeys++;
       } // end if
       
       else{ // split required
    	   // out.println("SPLIT HERE!"); // testing for split
    	   hTable.add(new Bucket(null));
    	   while(temp.next != null){
    		   temp = temp.next;
    	   }
    	   //check in the last bucket of the chain
    	   if(temp.nKeys < SLOTS){
    		   temp.key[temp.nKeys] = key;
    		   temp.value[temp.nKeys] = value;
    		   temp.nKeys++;
    	   }else{ // add to new bucket
    		   temp.next = new Bucket(null);
    		   temp = temp.next;
    		   temp.key[temp.nKeys]=key;
    		   temp.value[temp.nKeys]=value;
    		   temp.nKeys++;
    	   }

    	   Bucket replaceSplit = new Bucket(null); // bucket to replace split
    	   Bucket newTemp = new Bucket(null); // new bucket
    	   temp = hTable.get(split); //the bucket to split
    	   for(int m = 0; m<temp.nKeys; m++){
    		   if(i == split){ // splitting time
    			   if(replaceSplit.next ==null){
    				   replaceSplit.next = new Bucket(null);
    				   replaceSplit.next = replaceSplit;
    			   }   
    			   replaceSplit.key[replaceSplit.nKeys] = temp.key[m];
    			   replaceSplit.value[replaceSplit.nKeys] = temp.value[m];
    			   temp.nKeys++;
    		   } // end if
    		   else{ // new bucket time
    			   if(newTemp.next==null){
    				   newTemp.next = new Bucket(null);
    				   newTemp = newTemp.next;
    			   }
    			   newTemp.key[newTemp.nKeys] = temp.key[m];
    			   newTemp.value[newTemp.nKeys] = temp.value[m];  	
    		   } // end else
    	   } // end for loop
    	   // update split accordingly if
    	   if(split == mod1-1){ // mod1 = size, so -1 for index
    		   split = 0;
    		   mod1 = mod1*2;
    		   mod2 = mod1*2;
    	   }
    	   else{ // add 1 if split < mod1
    		   split++;
    	   }
       } // end else     
             
        return null;
    } // put

    /********************************************************************************
     * Return the size (SLOTS * number of home buckets) of the hash table. 
     * @return  the size of the hash table
     */
    public int size ()
    {
        return SLOTS * (mod1 + split);
    } // size

    /********************************************************************************
     * Print the hash table.
     */
    private void print ()
    {
    	out.println ("Hash Table (Linear Hashing) - Entries shown as Key:Value");
    	out.println ("-------------------------------------------");
    	
    	for(int i=0; i<hTable.size(); i++){
    		Bucket temp = hTable.get(i);
    		boolean chain = false;
    		if( temp.next!=null ){
    			chain = true; // chain exists if there is a next element
    		}
    		if(chain){ // printing chain of buckets
    			for( int j=0; j<SLOTS; j++ ){
    				out.print("");
    				out.print(temp.key[j]);
    				out.print(":");
    				out.print(temp.value[j]);
    				out.print("\t");
    				if(SLOTS!=j+1){
    					out.print("\t");
    				} // end if
    				else{
    					out.print(" \t(chain) =>");
    				} // end else
    			} // end for loop
    			for( int j=0; j<SLOTS; j++ ){ // last bucket
    				out.print("");
    				out.print(temp.key[j]);
    				out.print(":");
    				out.print(temp.value[j]);
    				out.print("\t");
    				if(SLOTS!=j+1){
    					out.print(" \n");
    				} // end if
    			} // end for loop
              } 	// end if chain
              else{	
            	  for( int j=0; j<SLOTS; j++ ){ // only bucket
            		  out.print("");
            		  out.print(temp.key[j]);
            		  out.print(":");
            		  out.print(temp.value[j]);
            		  out.print("\t");
            		  if(SLOTS!=j+1){
            			  if(temp.value[j]==null)
            				  out.print("");
            			  else
            				  out.print("\t"); 
            		  } // end if
            	  }	 // end for loop
            	  out.print(" \n");
              } // end else	
       } // end first for loop
    	out.println ("-------------------------------------------");
    } // print

    /********************************************************************************
     * Hash the key using the low resolution hash function.
     * @param key  the key to hash
     * @return  the location of the bucket chain containing the key-value pair
     */
    private int h (Object key)
    {
        return key.hashCode () % mod1;
    } // h

    /********************************************************************************
     * Hash the key using the high resolution hash function.
     * @param key  the key to hash
     * @return  the location of the bucket chain containing the key-value pair
     */
    private int h2 (Object key)
    {
        return key.hashCode () % mod2;
    } // h2

    /********************************************************************************
     * The main method used for testing.
     * @param  the command-line arguments (args [0] gives number of keys to insert)
     */
    public static void main (String [] args)
    {
        LinHashMap <Integer, Integer> ht = new LinHashMap <> (Integer.class, Integer.class, 11);
        
        int nKeys = 30;
        if (args.length == 1) nKeys = Integer.valueOf (args [0]);
        for (int i = 1; i < nKeys; i += 2) ht.put (i, i * i);
       
        // random testing
        out.println("Test - Value at Key12: " + ht.get(12));
        ht.put(12, 50);
        ht.put(18, 70);
        ht.put(22, 90);
        ht.put(28, 40);
        out.println("Test - Value at Key1: " + ht.get(1));
        out.println("Test - Value at Key5: " + ht.get(5));
        out.println("Test - Value at Key12 (after put method): " + ht.get(12));
        out.println("Test - Value at Key8: " + ht.get(8));
        out.println("Test - Value at Key17: " + ht.get(17));
        out.println("");
        // end random testing
        
        out.println("(Printing Hash Table... only keys with values will appear as not null.)");
        out.println("");
        ht.print (); // print test/hash table
        for (int i = 0; i < nKeys; i++) { // print keys and values
            out.println ("key = " + i + " value = " + ht.get (i));
        } // for loop
        
        out.println ("-------------------------------------------");
        out.println ("Average number of buckets accessed = " + ht.count / (double) nKeys);
    
    } // main

} // LinHashMap class
