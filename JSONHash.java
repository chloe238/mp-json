import java.io.PrintWriter;
import java.util.Iterator;

/**
 * JSON hashes/objects.
 */
public class JSONHash {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  int size = 0;

  KVPair<JSONString, JSONValue>[] values;

  static final double PROBE_OFFSET = 17;
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
    return null;        // STUB
  } // get(JSONString)

  /**
   * Get all of the key/value pairs.
   */
  public Iterator<KVPair<JSONString,JSONValue>> iterator() {
    return null;        // STUB
  } // iterator()

  /**
   * Set the value associated with a key.
   */
  public void set(JSONString key, JSONValue value) {
                        // STUB
  } // set(JSONString, JSONValue)

  /**
   * Find out how many key/value pairs are in the hash table.
   */
  public int size() {
    return 0;           // STUB
  } // size()

  public int find(JSONString key){
    int index = Math.abs(key.hashCode()) % this.values.length;
    while(!(this.values[index]).key().equals(key) && this.values[index] != null){
      index = (index + (int)PROBE_OFFSET) % this.size;
    }
    return index;
  }
} // class JSONHash
