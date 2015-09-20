
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
        K [] key;
        V [] value;
        @SuppressWarnings("unchecked")
        Bucket ()
        {
            nKeys = 0;
            key   = (K []) Array.newInstance (classK, SLOTS);
            value = (V []) Array.newInstance (classV, SLOTS);
        } // constructor
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

        for (int i = 0; i < nBuckets; i++) {
            Bucket newBucket = new Bucket();

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
            } //for
        } //for

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

        //  Implemented by Ashley Bennett
        // Iterates through keys it is found, and returns the value associated with it
        for (int j = 0; j < b.nKeys; j++) {
          if (key.equals(b.key[j]))
              return b.value[j];
        } //for

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
        //Check that a key is given.
        if (key == null)
          return null;

        int    i = h (key);
        Bucket b = dir.get (i);

        //  T O   B E   I M P L E M E N T E D
        // Implemented by Ashley Bennett

        // Adds key-value pair to the bucket if there is room.
        if (b.nKeys < SLOTS) {
          count++;
          b.key[b.nKeys] = key;
          b.value[b.nKeys] = value;
          b.nKeys++;
          return null;
        } // if

        // Adds a bucket to the hash table, and increments mod.
        // Put is recursively called until there is an available bucket.
        else {
            this.mod = this.mod++;
            this.hTable.add(b);
            put(key, value);
        } // else

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
        int bCount = 1; //number of buckets
        int iCount = 0; //number of key-value pairs (items)

        for (Bucket curr : dir) {
            out.println("\nBucket #" + bCount + ":");
            bCount++;

            for (int i = 0; i < curr.nKeys; i++) {
                out.print("Item #" + iCount + ": ");
                out.println(curr.value[i].toString());

                iCount++;
            } //for
        } //for

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
        out.println("Test One\n");
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
        out.println("Test Two\n");
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

