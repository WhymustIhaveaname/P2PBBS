import java.net.*;
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
    void run(){
        switch(this.mode){
            case(Server.TCP):
                listenTCP();
                //打开UDPServer
                Server ser=new Server(this.port,Server.UDP);
                Thread t=new Thread(ser);
                t.start();
            case(Server.UDP):
                listenUDP();
        }
    }
    /**监听TCP端口的Server*/
    private void listenTCP(){
        //打开TCP端口
        ServerSocket ss=new ServerSocket(this.port);
        while(1){//何时退出这个死循环我还没有想好
            Socket socket=ss.accept();
            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //读入报头第一行并解析出协议类型
            String head=in.readLine();
            Pattern p=Pattern.compile("\\[([0-9]+?):(.+?)\\]");
            Matcher m = p.matcher(head);
            if(m.find()){
                byte requestMode=Integer.parseInt(m.group(1));
                byte requestModeStr=m.group(2);
            }
            //根据解析出的requestMode决定调用哪个函数，处理完再继续监听
            switch(requestMode){
                case(Protocal.RPL)://Request Peer List
                    replyRPL(socket);
                case(Protocal.RP):
                    replyRP(socket);
            }
        }
    }
    /**监听UDP端口的Server,用于处理与泛洪法相关的数据报*/
    private void listenUDP(){
        //打开UDP端口
        while(1){
            //读一个UDP数据报
            //根据数据报确定类型
            //进行响应处理和返回
        }
    }
    /**处理请求节点列表*/
    private void replyRPL(Socket socket){
        //生成返回数据报body
        String s="[IPORT:[";
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c=DriverManager.getConnection("jdbc:sqlite:Datas.db");
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM PEER ORDER BY T1 DESC;");
            while(rs.next()){
                String iport=rs.getString("IPORT");
                String key=rs.getString("KEY");
                s+=String.format("\"%s\",",iport);
            }
            rs.close();stmt.close();c.close();
        }catch ( Exception e ) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        s+="]]";
        //用socket返回给请求者
        //关闭socket
    }
    /**处理请求帖子列表*/
    private void replyRP(Socket socket){
        //从socket中读出请求时间
        //从数据库中把帖子提取出来
        //生成返回报文
        //返回并关闭socket
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
