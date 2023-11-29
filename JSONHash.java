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

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  int size = 0;

  KVPair<JSONString, JSONValue>[] values;

  
  /**
   * Our helpful random number generator, used primarily when expanding the size of the table..
   */
  Random rand;
  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  public JSONHash (){
  }
  // +-------------------------+-------------------------------------
  // | Standard object methods |
  // +-------------------------+

  /**
   * Convert to a string (e.g., for printing).
   */
  public String toString() {
    return "";          // STUB
  } // toString()

  /**
   * Compare to another object.
   */
  public boolean equals(Object other) {
    return true;        // STUB
  } // equals(Object)

  /**
   * Compute the hash code.
   */
  public int hashCode() {
    return 0;           // STUB
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
  public Iterator<KVPair<JSONString,JSONValue>> getValue() {
    return this.iterator();
  } // getValue()

  // +-------------------+-------------------------------------------
  // | Hashtable methods |
  // +-------------------+

  /**
   * Get the value associated with a key.
   */
  public JSONValue get(JSONString key) {
    int index = find(key);
    KVPair<JSONString, JSONValue> pair = values[index];
    if (pair == null) {
      throw new IndexOutOfBoundsException("Invalid key: " + key);
    } else {
      while (!key.equals(pair.key())) {
        index++;
        if(index >= size) throw new IndexOutOfBoundsException("Invalid key: " + key);
      }
      return pair.value();
    } // get
  } // get(JSONString)

  /**
   * Get all of the key/value pairs.
   */
  public Iterator<KVPair<JSONString,JSONValue>> iterator() {
    return new Iterator<KVPair<JSONString, JSONValue>>() {
      int i = 0;
      public boolean hasNext() {
        return this.i < JSONHash.this.size;
      } // hasNext()

      public KVPair<JSONString, JSONValue> next() throws NoSuchElementException {
        while(JSONHash.this.values[i] == null){
          if(!this.hasNext()){
            throw new NoSuchElementException();
          }
          i++;
        }
        return JSONHash.this.values[i];
      } // next()
    }; // new Iterator
  } // iterator()

  /**
   * Set the value associated with a key.
   */
  public void set(JSONString key, JSONValue value) {
    int index = find(key);
    if (this.values[index] != null) {
      while (index < this.values.length) {
        index++;
        if (this.values[index] == null) {
          this.values[index] = new KVPair<JSONString, JSONValue>(key, value);
        }
      }
    } else {
      this.values[index] = new KVPair<JSONString, JSONValue>(key, value);
    }
    this.size++;
  } // set(JSONString, JSONValue)

  /**
   * Find out how many key/value pairs are in the hash table.
   */
  public int size() {
    return this.size;
  } // size()

  /**
   * Expand the size of the table.
   */
  void expand() {
    // Figure out the size of the new table.
    int newSize = 2 * this.values.length + 6;
    // Create a new table of that size.
    Object[] newPairs = new Object[newSize];
    // Move all pairs from the old table to their appropriate
    // location in the new table.
    // STUB
    // And update our pairs
  } // expand()


  int find(JSONString key){
    int index = Math.abs(key.hashCode()) % this.values.length;
    while(!(this.values[index]).key().equals(key) && this.values[index] != null){
      index = (index + (int)PROBE_OFFSET) % this.size;
    }
    return index;
  }
} // class JSONHash
