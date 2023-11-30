import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

/**
 * Utilities for our simple implementation of JSON.
 */
public class JSON {
  // +---------------+-----------------------------------------------
  // | Static fields |
  // +---------------+

  /**
   * The current position in the input.
   */
  static int pos;

  /**
   * Stores a character value read (for recursion purposes)
   */
  static int cachedChar;

  // +----------------+----------------------------------------------
  // | Static methods |
  // +----------------+

  /**
   * Parse a string into JSON.
   */
  public static JSONValue parse(String source) throws ParseException, IOException {
    return parse(new StringReader(source));
  } // parse(String)

  /**
   * Parse a file into JSON.
   */
  public static JSONValue parseFile(String filename) throws ParseException, IOException {
    FileReader reader = new FileReader(filename);
    JSONValue result = parse(reader);
    reader.close();
    return result;
  } // parseFile(String)

  /**
   * Parse JSON from a reader.
   */
  public static JSONValue parse(Reader source) throws ParseException, IOException {
    pos = 0;
    JSONValue result = parseKernel(source);
    if (-1 != skipWhitespace(source)) {
      throw new ParseException("Characters remain at end", pos);
    } // if
    return result;
  } // parse(Reader)

  // +---------------+-----------------------------------------------
  // | Local helpers |
  // +---------------+

  /**
   * Parse JSON from a reader, keeping track of the current position
   */
  static JSONValue parseKernel(Reader source) throws ParseException, IOException {
    int ch;
    ch = skipWhitespace(source);
    if (-1 == ch) {
      throw new ParseException("Unexpected end of file", pos);
    } else {    
      String input = "";

      //Integers and Reals
      if (Character.isDigit(((char) ch)) || '.' == ch) {
        // Do/while because we need to add in that first character we read
        do {
          input += String.valueOf((char) ch);
        } while (!isEndingChar(ch = skipWhitespace(source)));
        cachedChar = ch;
        if (input.contains(".")) {
          return new JSONReal(input);
        } else {
          return new JSONInteger(input);
        } // if/else
      } // if isDigit

      // Strings
      else if ('\"' == ch) {
        // We don't want to skip whitespace when reading strings
        while ('\"' != (ch = source.read())) {
          if (-1 == ch) {
            throw new ParseException("Unexpected end of file", pos);
          } // if
          input += String.valueOf((char) ch);
        } // while we don't hit a second double quote
        if (isEndingChar(ch = skipWhitespace(source))) {
          cachedChar = ch;
          return new JSONString(input);  
        } // if isEndingChar    
      } // else if double quote

      // Constants
      else if ('t' == ch || 'f' == ch || 'n' == ch) {
        do {
          input += String.valueOf((char) ch);
        } while (!isEndingChar(ch = skipWhitespace(source)));
        cachedChar = ch;
        switch(input) {
          case "true":
            return JSONConstant.TRUE;
          case "false":
            return JSONConstant.FALSE;
          case "null":
            return JSONConstant.NULL;  
          default:
            throw new IOException("Invaild Constant Value");
        } // switch
      } // else if constant

      // Arrays
      else if ('[' == ch) {
        JSONArray resultArr = new JSONArray();
        while (']' != ch) {
          if (-1 == ch) {
            throw new ParseException("Unexpected end of file", pos);
          } // if
          resultArr.add(parseKernel(source));
          ch = cachedChar;
        } // while we don't hit a closing bracket
        if (isEndingChar(ch = skipWhitespace(source))) {
          cachedChar = ch;
          return resultArr;
        } // if isEndingChar
      } // else if array

      // Hashes
      else if ('{' == ch) {
        JSONHash resultHash = new JSONHash();
        JSONString key = null;
        JSONValue val = null;
        while ('}' != ch) {
          if (-1 == ch) {
            throw new ParseException("Unexpected end of file", pos);
          } // if
          JSONValue temp = parseKernel(source);
          ch = cachedChar;

          if (':' == ch) {
            key = (JSONString) temp;
          } else if (',' == ch || '}' == ch) {
            val = temp;
            resultHash.set(key, val);
          } // if/else
        } // while no closing brace
        if (isEndingChar(ch = skipWhitespace(source))) {
          cachedChar = ch;
          return resultHash;
        } // if isEndingChar
      } // else if hash
    } // if/else
    throw new ParseException("Invalid Value", pos);
  } // parseKernel(Reader)

  /**
   * Get the next character from source, skipping over whitespace.
   */
  static int skipWhitespace(Reader source) throws IOException {
    int ch;
    do {
      ch = source.read();
      ++pos;
    } while (isWhitespace(ch));
    return ch;
  } // skipWhitespace(Reader)

  /**
   * Determine if a character is JSON whitespace (newline, carriage return,
   * space, or tab).
   */
  static boolean isWhitespace(int ch) {
    return (' ' == ch) || ('\n' == ch) || ('\r' == ch) || ('\t' == ch);
  } // isWhiteSpace(int)

  /**
   * Determine if a character is some sort of JSON object terminating character
   * (EOF, comma, closing brace, closing bracket, colon). Mainly a helper for
   * the recursion
   */
  static boolean isEndingChar(int ch) {
    return (-1 == ch) || (',' == ch) || ('}' == ch) || (']' == ch) || (':' == ch);
  } // isWhiteSpace(int)

} // class JSON
