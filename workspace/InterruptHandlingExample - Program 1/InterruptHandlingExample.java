// This example has been executed by Andrew Cronic
 
public class InterruptHandlingExample {
    public static void main(String a[]){                                           //Line 1
      try{                                                                         //Line 2
            System.out.println("This example has been executed by Andrew Cronic\n");
            for(int i=4;i>=0;i--){                                                 //Line 3
                System.out.println(10/i);                                          //Line 4
            }
        } catch(Exception ex){                                                     //Line 5
            System.out.println("Exception Message: "+ex.getMessage());             //Line 6
            ex.printStackTrace();                                                  //Line 7
        }
        System.out.println("After 'for' loop...");                                 //Line 8
        
    }
}