import java.io.PrintWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * JSON hashes/objects.
 */
public class JSONHash implements JSONValue{
  
  /**
   * The load factor for expanding the table.
   */
  static final double LOAD_FACTOR = 0.5;
  static final double PROBE_OFFSET = 17;
  static final int INITIAL_SIZE = 10;

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  int size;

  Object[] values;

  
  /**
   * Our helpful random number generator, used primarily when expanding the size of the table..
   */
  Random rand;
  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  public JSONHash () {
    this.size = 0;
    this.values = new Object[INITIAL_SIZE];
  }
  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  @SuppressWarnings("unchecked")
  public String toString() {
    String str = "";
    for (Object objpair : this.values) {
      if (objpair != null) {
        KVPair<JSONString, JSONValue> pair = (KVPair<JSONString, JSONValue>) objpair;
        str = str + ", " + pair.key() + ": " + pair.value(); 
      }// if not a null value
    }// for
    return "{" + str.substring(1) + " }";
  } // toString()

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {
    for (int i = 0; i < size(); i++) {
    if (!((other instanceof JSONHash) && ((JSONHash) other).values[i].equals(this.values[i]))) {
      return false;
      } // if
    } // for
    return true;        
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  @SuppressWarnings("unchecked")
  public int hashCode() {
    int hash = 0;
    for (Object objpair : this.values){
      KVPair<JSONString, JSONValue> pair = (KVPair<JSONString, JSONValue>) objpair;
      hash = hash + pair.hashCode(); 
    } // for
    return hash;
  } // hashCode()

  // +--------------------+------------------------------------------
  // | Additional methods |
  // +--------------------+

  /**
   * Write the value as JSON.
   */
  public void writeJSON(PrintWriter pen) {
    pen.print(this.toString());
  } // writeJSON(PrintWriter)

  /**
   * Get the underlying value.
   */
  public Iterator<Object> getValue() {
    return this.iterator();
  } // getValue()

  // +-------------------+-------------------------------------------
  // | Hashtable methods |
  // +-------------------+

  @SuppressWarnings("unchecked")
  public boolean containsKey(JSONString key) {
    int index = find(key);
    KVPair<JSONString, JSONValue> pair = (KVPair<JSONString, JSONValue>) values[index];
    if (pair == null) {
      throw new IndexOutOfBoundsException("Invalid key: " + key);
    } else {
      while (!key.equals(pair.key())) {
        index++;
        if(index >= size) return false;
        pair = (KVPair<JSONString, JSONValue>) values[index];
      }
      return true;
    } // get
  }

  /**
   * Get the value associated with a key.
   */
  @SuppressWarnings("unchecked")
  public JSONValue get(JSONString key) {
    int index = find(key);
    KVPair<JSONString, JSONValue> pair = (KVPair<JSONString, JSONValue>) values[index];
    if (pair == null) {
      throw new IndexOutOfBoundsException("Invalid key: " + key);
    } else {
      while (!key.equals(pair.key())) {
        index++;
        if(index >= size) throw new IndexOutOfBoundsException("Invalid key: " + key);
        pair = (KVPair<JSONString, JSONValue>) values[index];
      }
      return pair.value();
    } // get
  } // get(JSONString)

  /**
   * Get all of the key/value pairs.
   */
  public Iterator<Object> iterator() {
    return new Iterator<Object>() {
      int i = 0;
      public boolean hasNext() {
        do {
          i++;
        } while (JSONHash.this.values[i] == null && i < JSONHash.this.size);
        return i < JSONHash.this.size;
      } // hasNext()

      public Object next() throws NoSuchElementException {
        if(!this.hasNext()){
          throw new NoSuchElementException();
        } // if no next element
        return JSONHash.this.values[i++];
      } // next()
    }; // new Iterator
  } // iterator()

  /**
   * Set the value associated with a key.
   */
  @SuppressWarnings("unchecked")
  public void set(JSONString key, JSONValue value) {
    //boolean set = false;
    if (this.size >= (this.values.length * LOAD_FACTOR)) {
      expand();
    } // if size is approaching load factor
    int index = find(key);
    if (this.values[index] != null) {
      if (((KVPair<JSONString, JSONValue>) this.values[index]).key().equals(key)) {
        this.values[index] = new KVPair<JSONString, JSONValue>(key, value);
        return;
      }
      while (index < this.values.length - 1) {
        index++;
        if (this.values[index] == null) {
          this.values[index] = new KVPair<JSONString, JSONValue>(key, value);
        } // if empty
      } // while
      
      /* 
      Commenting this out as it doesn't seem to affect anything now. Was this meant
      to assist with overwriting values?

      if (!set) {
        this.expand();
        while (index < this.values.length) {
          index++;
          if (this.values[index] == null) {
            this.values[index] = new KVPair<JSONString, JSONValue>(key, value);
            set = true;
          } // if empty
        } // while
      } // if not set
      */
    } else {
      this.values[index] = new KVPair<JSONString, JSONValue>(key, value);
    } // if/else
    this.size++;
  } // set(JSONString, JSONValue)

  /**
   * Find out how many key/value pairs are in the hash table.
   */
  public int size() {
    return this.size;
  } // size()

  // +--------------------------+------------------------------------
  // | Helper Hashtable methods |
  // +--------------------------+
  
  /**
   * Expand the size of the table.
   */
  void expand() {
    //save old vals
    Object[] old =  this.values;
    // Figure out the size of the new table.
    int newSize = 2 * this.values.length + 6;
    // Create a new table of that size.
    Object[] newPairs = new Object[newSize];
    // Move all pairs from the old table to their appropriate
    // location in the new table.
    for (int i = 0; i < old.length; i++) {
      newPairs[i] = old[i];
    }
    // And update our pairs
    this.values =  newPairs;
  } // expand()

  /**
   * Return the index of key. If key is not in the table, return
   * a possible location to put key.
   */
  @SuppressWarnings("unchecked")
  int find(JSONString key) {
    int index = Math.abs(key.hashCode()) % this.values.length;
    if (this.values[index] != null) {
      while (!((KVPair<JSONString, JSONValue>) this.values[index]).key().equals(key)) {
        //to fix issue with using mod arithmetic
        index = (index + (int)PROBE_OFFSET) % this.values.length;
        if (this.size == (int)PROBE_OFFSET) { 
          index++;
        } // if
        if (this.values[index] == null) {
          return index;
        } // if space is found
      } // while
    } // if index != null
    return index;
  } // find(JSONString)

} // class JSONHash
