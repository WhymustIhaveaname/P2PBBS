import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.logging.*;
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
    /**制定这个服务器处于哪种模式*/
    private byte mode;
    /**指定这个服务器监听哪个端口*/
    private int port;
    private static Logger log=Logger.getLogger("lavasoft");
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
                listenTCP();
                //打开UDPServer
                Server ser=new Server(Server.UDP,this.port);
                Thread t=new Thread(ser);
                t.start();
                break;
            case(Server.UDP):
                listenUDP();
                break;
        }
    }
    /**监听TCP端口的Server*/
    private void listenTCP(){
        //打开TCP端口
        ServerSocket ss;
        try{
            ss=new ServerSocket(this.port);
            log.info("listening TCP on "+this.port);
        }catch(Exception e){
            e.printStackTrace();
            log.info("Failed to create socket on "+this.port);
            return;
        }

        try{
        while(true){//何时退出这个死循环我还没有想好
            log.info("into while loop");
            Socket socket=ss.accept();
            log.info("accept "+socket.getInetAddress().getHostAddress()+":"+socket.getPort());
            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String head1=in.readLine();
            log.info("get request head:"+head1);
            String head2=in.readLine();
            String content=in.readLine();
            String tail=in.readLine();
            if(!(tail.equals("[END]"))){
                log.info("protocoal tail error:"+tail);
                continue;
                //这里应该扔出一个自定义的异常
            }
            /*正则字符串处理，当笔记留着吧
            Pattern p=Pattern.compile("\\[([0-9]+?):([A-Z]+?)\\]");
            Matcher m=p.matcher(head1);
            if(m.find()){
                byte requestMode=Integer.parseInt(m.group(1));
                String requestModeStr=m.group(2);
                log.info("parsed request head:["+String(requestMode)+requestModeStr+"]");
            }*/

            switch(head1){
                case("[1:RPL]")://Request Peer List
                    replyRPL(socket);
                    break;
                case("[4:RP]"):
                    replyRP(socket,content);break;
            }
        }
        }catch(Exception e){
            e.printStackTrace();
            log.info("Server quit while loop");
        }
    }
    /**监听UDP端口的Server,用于处理与泛洪法相关的数据报*/
    private void listenUDP(){
        //打开UDP端口
        //while(1){
            //读一个UDP数据报
            //根据数据报确定类型
            //进行响应处理和返回
        //}
    }
    /**处理请求节点列表*/
    private void replyRPL(Socket socket){
        //生成返回数据报body
        StringBuilder replyBuilder=new StringBuilder();
        replyBuilder.append(String.format("[5:%s]\r\n[]\r\n[",Protocal.PSTR[5]));
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c=DriverManager.getConnection("jdbc:sqlite:Datas.db");
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM PEER ORDER BY T1 DESC;");
            while(rs.next()){
                String iport=rs.getString("IPORT");
                String key=rs.getString("PUBKEY");
                long t1=rs.getLong("T1");
                replyBuilder.append(String.format("[%s,%s,%d],",iport,key,t1));
            }
            rs.close();stmt.close();c.close();
        }catch ( Exception e ) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        replyBuilder.append("]\r\n[END]");
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
        replyBuilder.append(String.format("[6:%s]\r\n[]\r\n[",Protocal.PSTR[6]));
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
        replyBuilder.append("]\r\n[END]");
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
    public void setMode(byte m){
        this.mode=m;
    }
    public byte getMode(){
        return this.mode;
    }
}
