import java.io.*;
import java.net.*;
import java.util.Random;
import java.sql.*;
import java.util.logging.*;
/**
    最后被包装好供主程序直接调用的类。
使用方法：
    想要请求活跃Peer列表怎么办？
        Transmission.requestPeerList(peer);
        会打开一个新线程，向peer请求列表，更新数据库
    想要请求一段时间的帖子怎么办？
        Transmission.requestPost(peer,time);
        会打开一个新线程，自己处理之后的事，与本线程用信号进行通信
    想要开始泛洪怎么办?
        Transmission.floodfill(post);
        会打开很多新线程，自己处理之后的事，与本线程用信号通信
    想要发送心跳包怎么办？
        发送心跳包是Server的行为，应当在Server中指定。就决定在UDPserver中实现吧。
    想要监听TCP端口并自动处理、返回数据怎么办？
        Transmission.listenTCP(port);
        会打开新线程，一直循环监听port端口，并开启多线程处理好一切
    想要监听TCP端口并自动处理、返回数据怎么办？
        Transmission.listenUDP(port);
        会打开新线程，监听UDP端口，并进行自动的泛洪

    @author WhyMustIHaveAName
*/
public class Transmission{
    private static Logger log=Logger.getLogger("lavasoft");
    /**向peer请求节点列表*/
    public static void requestPeerList(Peer peer){
        Client g=new Client(Protocal.RPL,peer);
        Thread t=new Thread(g);
        t.start();
    }
    /**向peer请求time之后的帖子*/
    public static void requestPost(Peer peer,long time){
        Client g=new Client(Protocal.RP,peer);
        Thread t=new Thread(g);
        t.start();
    }
    /**泛洪法发送帖子*/
    public static void floodfill(String postString){
        Client g=new Client(Protocal.FF);
        g.setContent(postString);
        Thread t=new Thread(g);
        t.start();
    }
    /**打开TCPserver*/
    public static void listenTCP(int port){
        Server ser=new Server(Server.TCP,port);
        Thread t=new Thread(ser);
        t.start();
    }
    /**打开UDPServer*/
    public static void listenUDP(int port){
        Server ser=new Server(Server.UDP,port);
        Thread t=new Thread(ser);
        t.start();
    }

    /**打开心跳Server*/
    public static void sendHB(int port){
        Server ser=new Server(Server.HEARTBEATSERVER,port);
        Thread t=new Thread(ser);
        t.start();
    }

    /**启动TCP，UDP，HB服务器，以随机端口*/
    public static byte onCreate(){
        int port;
        Connection conn;
        Statement stmt;
        ResultSet rs;
        String sql;
        try{
            Class.forName("org.sqlite.JDBC");
            conn=DriverManager.getConnection("jdbc:sqlite:Datas.db");
            stmt=conn.createStatement();
            rs=stmt.executeQuery("SELECT * FROM PORT");
            if(rs.next()){
                port=rs.getInt("MYPORT");
            }else{
                Random r=new Random(System.currentTimeMillis());
                do{
                    port=1024+r.nextInt(65536-1024);
                    log.info("try port:"+Integer.toString(port));
                }while(!getPortUsability(port));
                sql=String.format("INSERT INTO PORT(MYPORT) VALUES(%d)",port);
                stmt.executeUpdate(sql);
            }
            rs.close();stmt.close();conn.close();
        }catch(Exception e){
            e.printStackTrace();
            return -1;
        }
        Transmission.listenTCP(port);
        Transmission.listenUDP(port);
        Transmission.sendHB(port);
        log.info("created TCP,UDP and HB server");
        return 0;
    }

    /**判断端口是否可用，可用返回true*/
    public static boolean getPortUsability(int port){
        try {
            ServerSocket socket=new ServerSocket(port);
            socket.close();
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**获得网络时间，还没写，先写一个能用的凑活着*/
    public static long getNetTime(){
        long time=System.currentTimeMillis()/1000;
        return time;
    }


    public static void main(String[] args){
        try{
            //System.out.println(Transmission.getLocalHostLANAddress().getHostAddress());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
