import java.io.*;
/**
    A node in the P2P network is called a peer.
    @author Sun Youran
*/
public class Peer{
    private String strip;
    private int port, lastActiveTime;

    Peer(String strip,int port){
        this.strip=strip;
        this.port=port;
    }

    public void setstrip(String strip){
        this.strip=strip;
    }
    public void setport(int port){
        this.port=port;
    }
    public void setlastActiveTime(int t){
        this.lastActiveTime=t;
    }

    public String getstrip(){
        return this.strip;
    }
    public int getport(){
        return this.port;
    }
    public int getlastActiveTime(){
        return this.lastActiveTime;
    }
    public String toString(){
        //return this.strip+":"+Integer.toString(port);
        return String.format("[%d]%s:%d",this.lastActiveTime,this.strip,this.port);
    }
    public static void main(String argv[]){
        Peer aPeer=new Peer("1.1.1.1",80);
        System.out.println(aPeer.toString());
    }
}
