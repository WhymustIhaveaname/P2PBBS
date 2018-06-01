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

    public static void main(String args[]){
        //Test.testRPL();
        Test.testRP();
    }
}
