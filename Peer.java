import java.io.*;
/**
    一个P2P网络中的节点
    @author Sun Youran
*/
public class Peer{
    private String strip;
    private int port;
    private String publicKeyString;
    Peer(String strip,int port){
        this.strip=strip;
        this.port=port;
        this.publicKeyString="";
    }
    Peer(String strip,int port,String pk){
        this.strip=strip;
        this.port=port;
        this.publicKeyString=pk;
    }
    public String toString(){
        return String.format("[%s:%d,%s]",this.strip,this.port
                              ,this.publicKeyString);
    }
    public int hashCode(){
        return this.toString().hashCode();
    }
    public static void main(String argv[]){
        Peer aPeer=new Peer("1.1.1.1",80);
        System.out.println(aPeer.toString());
    }
    public void setstrip(String strip){
        this.strip=strip;
    }
    public void setport(int port){
        this.port=port;
    }
    public void setPublicKeyString(String pk){
        this.publicKeyString=pk;
    }
    public String getstrip(){
        return this.strip;
    }
    public int getport(){
        return this.port;
    }
    public String getPublicKeyString(){
        return this.publicKeyString;
    }
}
