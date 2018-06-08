import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.logging.*;
import java.util.*;
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
    public static int HEARTBEATINTERVAL=10000;
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
                try{
                    listenTCP();
                }catch(P2PBBSException e){
                    e.printStackTrace();
                }
                break;
            case(Server.UDP):
                try{
                    listenUDP();
                }catch(P2PBBSException e){
                    e.printStackTrace();
                }
                break;
            case(Server.HEARTBEATSERVER):
                heartbeatServer();
                break;
        }
    }

    /**监听TCP端口的Server*/
    private void listenTCP()throws P2PBBSException{
        //log.info("in fun listenTCP");
        //打开TCP端口
        ServerSocket ss;
        try{
            ss=new ServerSocket(this.port);
            log.info("listening on "+this.port);
        }catch(Exception e){
            log.warning("Failed to create socket on "+this.port);
            e.printStackTrace();
            throw new P2PBBSException(P2PBBSException.GETTCPSOCKETFAILED);
        }
        while(true){//何时退出这个死循环我还没有想好
            try{
                log.info("ready to receive");
                Socket socket=ss.accept();
                log.info("accept "+socket.getInetAddress().getHostAddress()
                                                       +":"+socket.getPort());
                BufferedReader in=new BufferedReader(
                                new InputStreamReader(socket.getInputStream()));
                String head=in.readLine();
                log.info("get request head:"+head);
                String content=in.readLine();
                String tail=in.readLine();
                if(!(tail.equals("[END]"))){
                    log.warning("protocoal tail error:"+tail);
                    continue;
                }
                switch(head){
                    case("[1:RPL]")://Request Peer List
                        replyRPL(socket);
                        break;
                    case("[4:RP]"):
                        replyRP(socket,content);
                        break;
                }
            }catch(Exception e){
                e.printStackTrace();
                log.warning("error in whlie loop");
            }
        }
    }

    /**监听UDP端口的Server,用于处理与泛洪法相关的数据报*/
    private void listenUDP()throws P2PBBSException{
        DatagramPacket datagramPacket = new DatagramPacket(new byte[MAX_UDP_MESSAGE_LENGTH], MAX_UDP_MESSAGE_LENGTH);
        DatagramSocket datagramSocket;
        try{
            datagramSocket = new DatagramSocket(this.port);
        }catch (Exception e){
            e.printStackTrace();
            throw new P2PBBSException(P2PBBSException.GETUDPSOCKETFAILED);
        }
        while (true){
            try{
                log.info("ready to receive");
                datagramSocket.receive(datagramPacket);
                String fromip=datagramPacket.getAddress().getHostAddress();
                String datagramString = new String(datagramPacket.getData(),0,datagramPacket.getLength());
                log.info(String.format("Server received message from %s:%d\n%s",
                                  fromip,datagramPacket.getPort(),datagramString));
                String[] datagramStringArray=datagramString.split("\r\n");
                String packetHead = datagramStringArray[0];
                String packetBody = datagramStringArray[1];
                if(!(datagramStringArray[2].equals("[END]"))){
                    log.info("tail error");
                    continue;
                }
                switch(packetHead){
                    case("[2:FF]"):
                        dealFF(packetBody);break;
                    case("[3:HB]"):
                        dealHB(packetBody,fromip);break;
                }
            }catch (Exception e){
                e.printStackTrace();
                log.info("Exception caught when waiting to receive: "+e.getMessage());
            }
        }
    }

    /**发送心跳包的服务*/
    private void heartbeatServer(){
        while(true){
            log.info("I wake up!");
            try{
                sendHeartbeat();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                try{
                    log.info("I am going to sleep for "+Integer.toString(Server.HEARTBEATINTERVAL)+"ms");
                    Thread.sleep(Server.HEARTBEATINTERVAL);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**发送一次心跳包的服务*/
    private void sendHeartbeat()throws P2PBBSException{
        //准备内容
        String content=String.format("[PORT:%d]",this.port);
        String msg=Protocal.genHead(Protocal.HB)+content+Protocal.genTail(Protocal.HB);
        byte[] msgByte=msg.getBytes();
        //获得UDP套接字
        DatagramSocket datagramSocket;
        try{
            datagramSocket=new DatagramSocket();
        }catch(Exception e){
            e.printStackTrace();
            throw new P2PBBSException(P2PBBSException.GETUDPSOCKETFAILED);
        }
        //从数据库中读出目的地
        List<String> iports=new ArrayList<String>();
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c=DriverManager.getConnection("jdbc:sqlite:Datas.db");
            Statement stmt=c.createStatement();
            ResultSet rs=stmt.executeQuery( "SELECT * FROM PEER ORDER BY T1 DESC;");
            while(rs.next()){
                iports.add(rs.getString("IPORT"));
            }
            rs.close();stmt.close();c.close();
        }catch(Exception e){
            e.printStackTrace();
            throw new P2PBBSException(P2PBBSException.DBERROR+e.getMessage());
        }
        //发送
        for(String iport:iports){
            //log.info("get "+iport);
            String[] iport_temp=iport.split(":");
            try{
                log.info("sending "+msg+" to "+iport);
                String ip=iport_temp[0];
                int port=Integer.parseInt(iport_temp[1]);
                DatagramPacket datagramPacket=new DatagramPacket(msgByte,msgByte.length,InetAddress.getByName(ip),port);
                datagramSocket.send(datagramPacket);
            }catch(Exception e){
                e.printStackTrace();continue;
            }
        }
        //擦屁股
        if(datagramSocket!=null){
            datagramSocket.close();
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
            //这里写得不好，有时间要改
            while(rs.next()){
                long time=rs.getLong("TIME");
                int hash=rs.getInt("HASH");
                int phash=rs.getInt("PHASH");
                String con=rs.getString("CONTENT");
                replyBuilder.append(String.format("[%d,%d,%d,%s],",time,
                                 hash,phash,Post.escape(con)));
            }
            rs.close();preStat.close();conn.close();
        }catch ( Exception e ) {
            e.printStackTrace();
        }
        replyBuilder.append("]");
        replyBuilder.append(Protocal.genTail(Protocal.RPR));
        String reply=replyBuilder.toString();
        log.info("generate reply:\n"+reply);
        //用socket返回给请求者
        try{
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            out.print(reply);
            out.flush();
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**处理泛洪法发送帖子*/
    private static void dealFF(String packetBody)
    {
        if (packetBody.charAt(0) != '[' || packetBody.charAt(packetBody.length()-1) != ']')
        {
            log.warning("packetBody not enveloped by []");
            return;
        }
        String[] packetBodySplit = packetBody.substring(1, packetBody.length()-1).split(",");
        if (packetBodySplit.length != 4)
        {
            log.warning("packetBodySplit length not equals to 4");
            return;
        }
        try
        {
            Post post = new Post(Long.parseLong(packetBodySplit[0]), Post.reverseEscape(packetBodySplit[3]), Integer.parseInt(packetBodySplit[2]));
            if (post.hashCode() != Integer.parseInt(packetBodySplit[1]))
            {
                log.warning("hashCode not match");
                return;
            }
            if (DataBase.insertPost(post) == 1) Transmission.floodfill(packetBody);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**处理心跳包*/
    private static void dealHB(String body,String ip){
        if(!(body.matches("\\[PORT:[0-9]+\\]"))){
            log.warning("body format error");
            return;
        }
        String iport=ip+":"+body.substring(6,body.length()-1);
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
                if(!checkT(Tnow,T1,T2)){return;}
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
            rs.close();preStat.close();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**dealHB中用于检验三个时间合理性的函数，还要再改*/
    private static boolean checkT(long Tnow,long T1,long T2){
        if(Tnow<T1){
            return false;
        }else{
            return true;
        }
    }

    /**测试dealHeartbeat*/
    public static void testDealHB(){
        Server.dealHB("[PORT:3333]","127.0.0.1");
    }
    
    /**测试dealFF*/
    public static void testDealFF()
    {
        //[时间,哈希,父哈希,内容]
        Server.dealFF("[1111,555,555,hi_5]");
    }

    /**主函数*/
    public static void main(String[] args){
        //DataBase.initTables();
        //Server.testDealHB();
        Server.testDealFF();
    }

    public void setMode(byte m){
        this.mode=m;
    }
    public byte getMode(){
        return this.mode;
    }
}
