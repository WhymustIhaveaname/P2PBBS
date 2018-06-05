import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.logging.*;
//test
/**
    作为服务器的类，监听tcp或udp端口，处理请求并返回。应作为后台进程一直开着。
当作为udp服务器时，会自动定时发送心跳包。
    这样使用本类：
        Server ser=new Server(port);
        Thread t=new Thread(ser);
        t.start();
    将会启动关于TCP和UDP的两个服务器线程。
    启动TCP服务器后会自动启动一个同端口的UDP服务器。
    @author WhyMustIHaveAName
*/
public class Server implements Runnable{
    /**作为TCP服务器时mode的值*/
    public static final byte TCP=1;
    /**作为UDP服务器是mode的值*/
    public static final byte UDP=2;
    /**作为发送心跳包时mode的值*/
    public static final byte HEARTBEATSERVER=3;
    /**UDP通信时接受的最大消息长度（字节数）*/
    public static final int MAX_UDP_MESSAGE_LENGTH = 65536;
    /**发送心跳包的间隔*/
    public static int HBINTERVAL=10000;
    /**一个Logger*/
    private static Logger log=Logger.getLogger("lavasoft");
    /**制定这个服务器处于哪种模式,1是TCP，2是UDP*/
    private byte mode;
    /**指定这个服务器监听哪个端口*/
    private int port;
    /**启动一个监听port端口的TCP服务器，它会自动启动同端口的UDP服务器*/
    Server(int p){
        this.mode=Server.TCP;
        this.port=p;
    }
    /**启动一个监听port端口，模式为m的服务器*/
    Server(byte m,int p){
        this.mode=m;
        this.port=p;
    }
    /**Runnable接口指定的多线程入口函数*/
    public void run(){
        switch(this.mode){
            case(Server.TCP):
                //打开UDPServer
                Server ser=new Server(Server.UDP,this.port);
                Thread t=new Thread(ser);
                t.start();
                listenTCP();
                break;
            case(Server.UDP):
                //打开HeartbeatServer
                Server ser2=new Server(Server.HEARTBEATSERVER,this.port);
                Thread t2=new Thread(ser2);
                t2.start();
                listenUDP();
                break;
            case(Server.HEARTBEATSERVER):
                sendHeartbeat();break;
        }
    }

    /**监听TCP端口的Server*/
    private void listenTCP(){
        log.info("in fun listenTCP");
        //打开TCP端口
        ServerSocket ss;
        try{
            ss=new ServerSocket(this.port);
            log.info("listening TCP on "+this.port);
        }catch(Exception e){
            log.warning("Failed to create socket on "+this.port);
            e.printStackTrace();
            return;
        }
        try{
        while(true){//何时退出这个死循环我还没有想好
            log.info("into while loop");
            Socket socket=ss.accept();
            log.info("accept "+socket.getInetAddress().getHostAddress()
                                                       +":"+socket.getPort());
            BufferedReader in=new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
            String head1=in.readLine();
            log.info("get request head:"+head1);
            String content=in.readLine();
            String tail=in.readLine();
            if(!(tail.equals("[END]"))){
                log.info("protocoal tail error:"+tail);continue;
            }
            switch(head1){
                case("[1:RPL]")://Request Peer List
                    replyRPL(socket);
                    break;
                case("[4:RP]"):
                    replyRP(socket,content);break;
            }
        }}catch(Exception e){
            e.printStackTrace();
            log.warning("Server quit while loop");
        }
    }

    /**监听UDP端口的Server,用于处理与泛洪法相关的数据报*/
    private void listenUDP(){
        log.info("in function listenUDP");
        DatagramPacket datagramPacket = new DatagramPacket(new byte[MAX_UDP_MESSAGE_LENGTH], MAX_UDP_MESSAGE_LENGTH);
        DatagramSocket datagramSocket;
        try{
            datagramSocket = new DatagramSocket(this.port);
        }catch (Exception e){
            e.printStackTrace();
            log.info("Exception caught in create udp socket: "+e.getMessage());
            return;
        }
        while (true){
            try{
                log.info("udp wait to receive");
                datagramSocket.receive(datagramPacket);
                String datagramString = new String(datagramPacket.getData(),0,datagramPacket.getLength());
                log.info(String.format("Server received message from %s:%d\n%s",
                                                 datagramPacket.getAddress(),datagramPacket.getPort(),datagramString));
                String[] datagramStringArray=datagramString.split("\r\n");
                String head=datagramStringArray[0];
                String body=datagramStringArray[1];
                String tail=datagramStringArray[2];
                switch(head){
                    case("[3:HB]"):
                        dealHB(body);break;
                }
            }catch (Exception e){
                e.printStackTrace();
                log.info("Exception caught when waiting to receive: "+e.getMessage());
            }
        }
    }

    /**发送心跳包的服务*/
    private void sendHeartbeat(){
        log.info("in fun sendHeartbeat");
        while(true){
            try{
                String content=String.format("[%s:%d]",Transmission.getMyIP(),this.port);
                String msg=String.format("%s%s%s",Protocal.genHead(Protocal.HB),content,
                                                       Protocal.genTail(Protocal.HB));
                byte[] msgByte=msg.getBytes();
                String[] iport;String ip;int port;
                DatagramPacket datagramPacket;

                DatagramSocket datagramSocket=new DatagramSocket();
                Class.forName("org.sqlite.JDBC");
                Connection c=DriverManager.getConnection("jdbc:sqlite:Datas.db");
                Statement stmt=c.createStatement();
                ResultSet rs=stmt.executeQuery( "SELECT * FROM PEER ORDER BY T1 DESC;");
                while(rs.next()){
                    iport=rs.getString("IPORT").split(":");
                    ip=iport[0];port=Integer.parseInt(iport[1]);
                    datagramPacket=new DatagramPacket(msgByte,msgByte.length,InetAddress.getByName(ip),port);
                    datagramSocket.send(datagramPacket);
                    log.info("send "+content+"to "+rs.getString("IPORT"));
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                try{
                    Thread.sleep(Server.HBINTERVAL);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**处理请求节点列表*/
    private void replyRPL(Socket socket){
        //生成返回数据报body
        StringBuilder replyBuilder=new StringBuilder();
        replyBuilder.append(Protocal.genHead(Protocal.RPLR));
        replyBuilder.append("[");
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c=DriverManager.getConnection("jdbc:sqlite:Datas.db");
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM PEER ORDER BY T1 DESC;");
            while(rs.next()){
                String iport=rs.getString("IPORT");
                long t1=rs.getLong("T1");
                replyBuilder.append(String.format("[%s,%d],",iport,t1));
            }
            rs.close();stmt.close();c.close();
        }catch ( Exception e ) {
            e.printStackTrace();
        }
        replyBuilder.append("]");
        replyBuilder.append(Protocal.genTail(Protocal.RPLR));
        String reply=replyBuilder.toString();
        log.info("generate reply:\n"+reply);
        //用socket返回给请求者
        try{
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            out.print(reply);
            out.flush();out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**处理请求帖子列表*/
    private void replyRP(Socket socket,String content){
        Long theT;
        try{
            log.info("get content:"+content);
            if(content.matches("\\[BEFORE:[0-9]+?\\]")){
                theT=Long.parseLong(content.substring(8,content.length()-1));
            }else{
                return;
            }
        }catch(Exception e){
            e.printStackTrace();
            return;
        }
        StringBuilder replyBuilder=new StringBuilder();
        replyBuilder.append(Protocal.genHead(Protocal.RPR));
        replyBuilder.append("[");
        try{
            Class.forName("org.sqlite.JDBC");
            Connection conn=DriverManager.getConnection("jdbc:sqlite:Datas.db");
            String s="SELECT * FROM POST WHERE TIME>?;";
            PreparedStatement preStat=conn.prepareStatement(s);
            preStat.setLong(1,theT);
            ResultSet rs = preStat.executeQuery();
            while(rs.next()){
                long time=rs.getLong("TIME");
                int hash=rs.getInt("HASH");
                int phash=rs.getInt("PHASH");
                String con=rs.getString("CONTENT");
                replyBuilder.append(String.format("[%d,%d,%d,%s],",time,
                                 hash,phash,con.replace(',',Post.DOUHAO)));
            }
            rs.close();preStat.close();conn.close();
        }catch ( Exception e ) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        replyBuilder.append("]");
        replyBuilder.append(Protocal.genTail(Protocal.RPR));
        String reply=replyBuilder.toString();
        log.info("generate reply:\n"+reply);
        //用socket返回给请求者
        try{
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            out.print(reply);
            out.flush();out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**处理泛洪法发送帖子*/
    private void dealFF(){
        //读入帖子，查看是否已经收到过
        //如果没收到过，调用Transmission.floodfill();
    }

    /**处理心跳包*/
    private static void dealHB(String body){
        if(body.charAt(0)!='[' || body.charAt(body.length()-1)!=']'){
            log.info("body format error");
            return;
        }
        String iport=body.substring(1,body.length()-1);
        long Tnow=Transmission.getNetTime();
        try{
            Class.forName("org.sqlite.JDBC");
            Connection conn=DriverManager.getConnection("jdbc:sqlite:Datas.db");
            String s="SELECT T1,T2 FROM PEER WHERE IPORT=?;";
            PreparedStatement preStat=conn.prepareStatement(s);
            preStat.setString(1,iport);
            ResultSet rs=preStat.executeQuery();
            if(rs.next()){
                long T1=rs.getLong("T1");
                long T2=rs.getLong("T2");
                String s2="UPDATE PEER SET T1=?,T2=?,T3=? WHERE IPORT=?;";
                PreparedStatement preStat2=conn.prepareStatement(s2);
                preStat2.setLong(1,Tnow);
                preStat2.setLong(2,T1);
                preStat2.setLong(3,T2);
                preStat2.setString(4,iport);
                preStat2.executeUpdate();
                preStat2.close();
                log.info("updated "+iport);
            }else{
                String s2="INSERT INTO PEER (IPORT,T1) VALUES(?,?);";
                PreparedStatement preStat2=conn.prepareStatement(s2);
                preStat2.setString(1,iport);
                preStat2.setLong(2,Tnow);
                preStat2.executeUpdate();
                preStat2.close();
                log.info("inserted "+iport);
            }
            rs.close();preStat.close();conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**测试dealHeartbeat*/
    public static void testDealHB(){
        Server.dealHB("[10.2.149.191:3333]");
    }

    /**主函数*/
    public static void main(String[] args){
        Server.testDealHB();
    }

    public void setMode(byte m){
        this.mode=m;
    }
    public byte getMode(){
        return this.mode;
    }
}
