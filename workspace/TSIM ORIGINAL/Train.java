import TSim.*;
public class Train extends Thread{

    /*
      Main starts only one train, with
         identity 1
         starting from the north (true)
         velocity 30
    */
    public static void main(String[] a) {
        TSimInterface inter = TSimInterface.getInstance();
        new Train(1,inter,true,30).start();
    }



    private int id;
    private TSimInterface inter;
    private boolean north; // north is true, south is false
    private int vel;


    public Train(int id, TSimInterface inter, boolean north, int vel){
        this.id = id;this.inter=inter;this.north=north;this.vel = vel;
        init(north);
    }

    private void init(boolean north){
        if(north){
            // Set the switches for the whole way.  This cannot be done if
            // there are going to be many trains running!
            try{
                inter.setSwitch(17,7,inter.SWITCH_RIGHT);
                inter.setSwitch(15,9,inter.SWITCH_RIGHT);
                inter.setSwitch(4,9,inter.SWITCH_LEFT);
                inter.setSwitch(3,11,inter.SWITCH_RIGHT);            
            }catch(TSim.CommandException e){System.err.println(e.getMessage());}
        }
        else{}
    }

    public void run(){

        try {
            if(north){
                n2s();
            }else{s2n();}
        }
        catch (CommandException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }catch(InterruptedException e){
            System.err.println(e.getMessage());
            System.exit(1);        
        }
    }        
    
    private void n2s() throws TSim.CommandException, InterruptedException{
        // start the train
        inter.setSpeed(id,vel);
        // wait it to pass trough the sensor close to the south station
        SensorEvent se = inter.getSensor(id);
        while(se.getXpos()!=11 || se.getYpos()!=13){
            se = inter.getSensor(id);
        }
        // stop the train, wait and restart in the other direction
        inter.setSpeed(id,0);
        nap(10000);
        s2n();
    }



    private void s2n()throws TSim.CommandException, InterruptedException{
        inter.setSpeed(id,-vel);
        SensorEvent se = inter.getSensor(id);
        while(se.getXpos()!=10 || se.getYpos()!=3){
            se = inter.getSensor(id);
        }
        inter.setSpeed(id,0);
        nap(10000);
        n2s();
    }

    
    private static void nap(int millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }


}