import java.util.*;
import java.util.logging.*;
import java.io.*;
/**命令行界面，只能作为服务器，不能发帖看贴*/
public class CUI{
    private static Logger log=Logger.getLogger("lavasoft");
    public static void main(String args[]){
        byte temp;
        temp=DataBase.checkDB();
        if(temp==1){
            System.out.print("It seems that you have not connect to the network,"
                +"please input a iport(x.x.x.x:x) to connect or input 0.0.0.0:0 to continue:");
            Scanner sc=new Scanner(System.in);
            String iport=sc.next();
            if(!(iport.equals("0.0.0.0:0"))){
                Transmission.requestPeerList(new Peer(iport,null));
                Transmission.requestPost(new Peer(iport,null),0);
                System.out.println("I have get Peer and Post list.");
            }
        }else if(temp==-1){
            System.out.println("There occurs some fatal error.Byebye.");
            System.exit(-1);
        }
        Transmission.onCreate();
        System.out.println("Server has started.");
    }
}
