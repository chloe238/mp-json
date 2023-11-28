import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Testing{

  @Test
  public void integerOutputTest(){
    assertTrue(new JSONInteger(3).equals(3));
  }
}