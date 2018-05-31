import java.io.*;
/**
    一个P2P网络中的节点
    @author WhyMustIHaveAName
*/
public class Peer{
    /**长成x.x.x.x这样的字符串*/
    private String strip;
    /**这个节点的端口*/
    private int port;
    /**这个节点的公钥，没有就是“null”*/
    private String publicKeyString;
    /**用x.x.x.x:x的字符串和公钥pk构造一个peer，没有公钥pk就是"null"*/
    Peer(String iport,String pk){
        String[] temp=iport.split(":");
        this.strip=temp[0];
        this.port=Integer.parseInt(temp[1]);
        this.publicKeyString=pk;
    }
    /**就是请求节点列表返回时应该有的样子*/
    public String toString(){
        return String.format("[%s:%d,%s]",this.strip,this.port
                              ,this.publicKeyString);
    }
    /**没什么用*/
    public int hashCode(){
        return this.toString().hashCode();
    }
    public static void main(String argv[]){
        Peer aPeer=new Peer("1.1.1.1:80",null);
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
