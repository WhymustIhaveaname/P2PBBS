import java.util.logging.*;
import java.io.*;
public class Test{
    private static Logger log=Logger.getLogger("lavasoft");
    public static void testRPL(){
        Transmission.listen(3334);
        try{
            Thread.sleep(500);
        }catch(InterruptedException e){}
        Transmission.requestPeerList(new Peer("127.0.0.1:3334",null));
        try{
            Thread.sleep(500);
        }catch(InterruptedException e){}
        Transmission.requestPeerList(new Peer("127.0.0.1:3334",null));
    }

    public static void testRP(){
        Transmission.listen(3333);
        try{
            Thread.sleep(500);
        }catch(InterruptedException e){}
        Transmission.requestPost(new Peer("127.0.0.1:3333",null),0);
    }

    public static void testReceiveUDP()
    {
        System.out.println("in testUDP");
        Server ser = new Server(Server.UDP, 6666);
        Thread t = new Thread(ser);
        t.start();
    }

    public static void testHB(){
        Server ser0 = new Server(Server.UDP, 3333);
        Thread t0 = new Thread(ser0);
        t0.start();
        try{Thread.sleep(300);}catch(Exception e){e.printStackTrace();}
        Server ser = new Server(Server.HEARTBEATSERVER, 3333);
        Thread t = new Thread(ser);
        t.start();
    }
    public static void startUDP(){
      Server ser0 = new Server(Server.UDP, 3333);
      Thread t0 = new Thread(ser0);
      t0.start();
    }
    public static void main(String args[]){
        //Test.testRPL();
        //Test.testRP();
        //Test.testReceiveUDP();
        //Test.testHB();
        Test.startUDP();
    }
}
