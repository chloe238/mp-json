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
    }
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
      //Code for arrays and hashes

      return parseKernelHelper(source, ch);
    }
  } // parseKernel(Reader)
  
  static JSONValue parseKernelHelper(Reader source, int ch) throws ParseException, IOException {
    String input = "";
    if (Character.isDigit(((char) ch)) || '.' == ch) {
      // Do/while because we need to add in that first character we read
      do {
        input += String.valueOf((char) ch);
      } while (-1 != (ch = skipWhitespace(source))) ;
      if (input.contains(".")) {
        return new JSONReal(input);
      } else {
        return new JSONInteger(input);
      }
    }

    else if ('\"' == ch) {
      //We don't want to skip whitespace when reading strings
      while ('\"' != (ch = source.read())) {
        if (-1 == ch) {
          throw new ParseException("Unexpected end of file", pos);
        }
        input += String.valueOf((char) ch);
      } // while we don't hit a second double quote
      return new JSONString(input);  
    }

    else if ('t' == ch || 'f' == ch || 'n' == ch) {
      do {
        input += String.valueOf((char) ch);
      } while (-1 != (ch = skipWhitespace(source))) ;
      switch(input){
        case "true":
          return JSONConstant.TRUE;
        case "false":
          return JSONConstant.FALSE;
        case "null":
          return JSONConstant.NULL;  
        default:
          throw new IOException("Invaild Constant Value");
      }
    }
    throw new ParseException("Unimplemented", pos);
  }

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

} // class JSON
