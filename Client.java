import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.logging.*;
/**
    处理作为客户端发出请求的种种。包括：请求节点列表，请求帖子列表，泛洪法发送
    帖子。为了使用本类你需要首先用type和dst（可选）构造类的实例：
        Client g=new Client(Protocal.BLABLA,peer);
    之后指定内容：
        g.setContent("[]");
    之后作为多线程来执行：
        Thread t=new Thread(g);
        t.start();

    @author WhyMustIHaveAName
*/
public class Client implements Runnable{
    private byte type;
    private String content;
    private Peer dst;
    private static Logger log=Logger.getLogger("lavasoft");
    /**在与泛洪相关的时候会用到这个构造函数，因为向好多人发送，所以没有dst可言*/
    Client(byte t){
        this.type=t;
    }
    /**在TCP时会用到这个构造函数*/
    Client(byte t,Peer d){
        this.type=t;
        this.dst=d;
    }
    /**Runnable接口指定的函数*/
    public void run(){
        switch(this.type){
            case(Protocal.RPL):
                sendRPL();
            case(Protocal.RP):
                sendRP();
            case(Protocal.FF):
                sendFF();
        }
    }
    /**发送请求节点列表的请求，等待返回，更新数据库
       使用前不需要初始化content*/
    private void sendRPL(){
        this.content="[]";
        Socket socket;
        try{
            socket=new Socket(this.dst.getstrip(),this.dst.getport());
        }catch(Exception e){
            log.warning("Create socket failed");
            e.printStackTrace();return;
        }
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
            out.println(String.format("%s%s%s",Protocal.genHead(this.type),this.content,Protocal.genTail(this.type)));
            out.flush();
            log.info("request has send.waiting for server's reply");
            sendRPLAux(in);
            out.close();//out.close一定要放在后面，因为close时会释放socket导致in不可用
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**sendRPL中用于处理返回的函数*/
    private void sendRPLAux(BufferedReader in){
        try{
            //检查头部
            Protocal.confirmHead(in.readLine(),Protocal.RPLR);
            StringBuilder contentBuilder=new StringBuilder();
            String temp;
            while((temp=in.readLine())!=null){
                contentBuilder.append(temp);
            }
            String content=contentBuilder.toString();
            log.info("get server's reply\n"+content);
            //检查尾部
            if(!content.endsWith(",][END]")){
                throw new P2PBBSException("Tail error");
            }
            //把头和尾截掉
            content=content.substring(2,content.length()-8);
            //打开数据库
            Class.forName("org.sqlite.JDBC");
            Connection conn=DriverManager.getConnection("jdbc:sqlite:Datas.db");
            String s="INSERT INTO PEER(IPORT,PUBKEY,T1) VALUES(?,?,?);";
            PreparedStatement preStat=conn.prepareStatement(s);
            for(String i:content.split("\\],\\[")){
                log.info("saving "+i);
                String[] j=i.split(",");
                preStat.setString(1,j[0]);
                preStat.setString(2,j[1]);
                preStat.setLong(3,Long.parseLong(j[2]));
                try{
                    preStat.executeUpdate();
                    preStat.clearParameters();
                }catch(org.sqlite.SQLiteException e){
                    log.info("peer has exist");
                    preStat=conn.prepareStatement(s);
                }
            }
            preStat.close();conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**测试sendRPLAux功能的函数*/
    public static void testSendRPLAux(){
        Client c=new Client((byte)1);
        String s="[5:RPLR]\r\n[[0.0.0.0:3333,null,12335],[1.1.1.1:4444,null,123456],]\r\n[END]";
        BufferedReader in=new BufferedReader(new StringReader(s));
        c.sendRPLAux(in);
    }
    /**发送请求帖子请求，等待返回，更新数据库
       请求什么时间之后的那个时间是在Transmission中setContent时指定的*/
    private void sendRP(){
        this.content=String.format("[BEFORE:%d]",System.currentTimeMillis()/1000);
        Socket socket;
        try{
            socket=new Socket(this.dst.getstrip(),this.dst.getport());
        }catch(Exception e){
            log.info("create socket failed");
            e.printStackTrace();return;
        }
        try{
            PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
            out.println(String.format("%s%s%s",Protocal.genHead(this.type),this.content,Protocal.genTail(this.type)));
            out.flush();
            log.info("request has send.waiting for server's reply");
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sendRPAux(in);
            out.close();
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**sendRP中用于处理返回的函数*/
    private static void sendRPAux(BufferedReader in){
        //检查头部
        try{
            Protocal.confirmHead(in.readLine(),Protocal.RPR);
            StringBuilder contentBuilder=new StringBuilder();
            String temp;
            while((temp=in.readLine())!=null){
                contentBuilder.append(temp);
            }
            String reply=contentBuilder.toString();
            log.info("get server's reply\n"+reply);
            //检查尾部
            if(!(reply.endsWith(",][END]"))){
                throw new P2PBBSException("Tail error");
            }
            //把头和尾截掉
            reply=reply.substring(2,reply.length()-8);
            //打开数据库
            Class.forName("org.sqlite.JDBC");
            Connection conn=DriverManager.getConnection("jdbc:sqlite:Datas.db");
            String s="INSERT INTO POST(TIME,HASH,PHASH,CONTENT) VALUES(?,?,?,?);";
            PreparedStatement preStat=conn.prepareStatement(s);
            for(String i:reply.split("\\],\\[")){
                log.info("saving "+i);
                String[] j=i.split(",");
                preStat.setLong(1,Long.parseLong(j[0]));
                preStat.setInt(2,Integer.parseInt(j[1]));
                preStat.setInt(3,Integer.parseInt(j[2]));
                preStat.setString(4, Post.reverseEscape(j[3]));
                try{
                    preStat.executeUpdate();
                    preStat.clearParameters();
                }catch(org.sqlite.SQLiteException e){
                    preStat=conn.prepareStatement(s);
                }
            }
            preStat.close();conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**测试sendRPAux功能的函数*/
    public static void testSendRPAux(){
        String s="[6:RPR]\r\n[[1,2049459487,0,helloworld],[2,2049459488,0,helloworld],]\r\n[END]";
        Client c=new Client((byte)1);
        BufferedReader in=new BufferedReader(new StringReader(s));
        c.sendRPAux(in);
    }

    
    public static void testSendUDP(String message){
        System.out.println("in testSendUDP");
        try
        {
            DatagramSocket datagramSocket = new DatagramSocket();
            datagramSocket.setSoTimeout(10000);
            InetAddress host = InetAddress.getByName("localhost");
            DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), host, 6666);
            datagramSocket.send(datagramPacket);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.info("Exception caught in testSendUDP: "+e.getMessage());
        }
    }

    public static void main(String args[]){
        //testSendRPLAux();
        testSendRPAux();
        //testSendUDP(args[0]);
    }
    /**泛洪法发送帖子*/
    private void sendFF(){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c=DriverManager.getConnection("jdbc:sqlite:Datas.db");
            Statement stmt=c.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT * FROM PEER ORDER BY T1 DESC;");
            DatagramSocket datagramSocket = new DatagramSocket();
            while(rs.next()){
                String iport=rs.getString("IPORT");
                String key=rs.getString("PUBKEY");
                Peer peer = new Peer(iport, key);
                try
                {
                    InetAddress host = InetAddress.getByName(peer.getstrip());
                    String message = "[2:FF]\r\n"+content+"\r\n";
                    DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), host, peer.getport());
                    datagramSocket.send(datagramPacket);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    log.info("Exception caught in testSendUDP: "+e.getMessage());
                }
            }
            rs.close();stmt.close();c.close();
            datagramSocket.close();
        }catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void setContent(String s){
        this.content=s;
    }
    public String getContent(){
        return this.content;
    }
  }
