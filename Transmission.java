import Peer;
import Protocal;
import Post;
import java.io.*;
import java.net.*;
/**
    The class deals with network transmission.

    想要请求活跃Peer列表怎么办？
        Transmission.requestPeerList(peer);
        会打开一个新线程，向peer请求列表，更新数据库
        并在结束时返回一些信号，这里我还不会，计划用notify
    想要请求一段时间的帖子怎么办？
        Transmission.requestPost(peer,time);
        会打开一个新线程，自己处理之后的事，与本线程用信号进行通信
    想要开始泛洪怎么办?
        Transmission.floodfill(post);
        会打开很多新线程，自己处理之后的事，与本线程用信号通信
    想要发送心跳包怎么办？
        Transmission.sendHeartbeat();
        会打开很多线程，自己处理后事，与本线程用信号通信
    想要接收数据怎么办？
        Transmission demo=new Transmission();
        demo.listen(port);
        会打开新线程，一直循环监听port端口，并开启多线程处理好一切
    @author Sun Youran
*/
public class Transmission implements Runnable{
    private Socket socket;
    private byte type;
    public static void requestPeerList(Peer peer){
        Guest g=new Guest(Protocal.RPL,peer);
        Thread t=new Thread(g);
        t.start();
    }
    public static void requestPost(Peer peer,long time){
        Guest g=new Guest(Protocal.RP,peer,time);
        Thread t=new Thread(g);
        t.start();
    }
    public static void floodfill(Post aPost){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c=DriverManager.getConnection("jdbc:sqlite:PeerList.db");
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM COMPANY;" );
            while(rs.next()){
                String ip=rs.getString("IP");
                int port=rs.getInt("PORT");
                String key=rs.getString("KEY");
                Guest g=new Guest(Protocal.FF,new Peer(ip,port,key),aPost);
                Thread t=new Thread(g);
                t.start();
            }
            rs.close();stmt.close();c.close();
        }catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
    public static void sendHeartbeat(){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c=DriverManager.getConnection("jdbc:sqlite:PeerList.db");
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM COMPANY;" );
            while(rs.next()){
                String ip=rs.getString("IP");
                int port=rs.getInt("PORT");
                String key=rs.getString("KEY");
                Guest g=new Guest(Protocal.HB,new Peer(ip,port,key));
                Thread t=new Thread(g);
                t.start();
            }
            rs.close();stmt.close();c.close();
        }catch ( Exception e ) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
    }
    public static void listen(int port){
        Server ser=new Server(port);
        Thread t=new Thread(ser);
        t.start();
    }
    /**
        Java程序照例会有的无聊程序
    */
    public void setSocket(Socket s){
        this.socket=s;
    }
    public void setType(byte t){
        this.type=t;
    }
    public byte getType(){
        return this.type;
    }
    public Socket getSocket(){
        return this.socket;
    }
}
/*TCP Client
    Socket socket = new Socket("127.0.0.1",3333);
    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
    // 接收控制台的输入
    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    String info = input.readLine();
    //写Socket
    out.println(info);
    //读Socket
    String str = in.readLine();
    //打扫干净
    in.close();
    out.close();
*/
/*TCP Server
    ServerSocket ss = new ServerSocket(3333);
    Socket socket = ss.accept();
    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    String str = in.readLine();
    PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
    out.println(info);
    in.close();
    out.flush();
    out.close();
*/
