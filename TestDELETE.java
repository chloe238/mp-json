public class TestDELETE {
  
  public static void main(String[] args){
    JSONString key = new JSONString("firstkey");
    System.out.println("String made");
    JSONHash test = new JSONHash(); 
    System.out.println("Hash made");
   // System.out.println("Hash size: " + test.values.length);
    //test.expand();
   // System.out.println("Hash expanded");
   // System.out.println("Hash expanded size: " + test.values.length);
    for (int i = 0; i < 25; i++){
      test.set(new JSONString("key " + i), new JSONReal(i * 3.14));
    }  
       /*
        *Overwriting is not yet implemented, find() breaks if the length changes, 
        *need to make a hashContains(key) function or something and add it to find(key)
        */

    System.out.println(test.toString());
    test.set(new JSONString("key " + 15), new JSONString("firstval") );
    System.out.println("Hash reset");
    System.out.println("Key exists at index: " + test.find(new JSONString("key " + 15)));
    System.out.println("Val at key: " + test.get(new JSONString("key " + 15)));
  }//main
}
