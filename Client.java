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
    void run(){
        switch(this.type){
            case(Protocal.RPL):
                sendRPL();
            case(Protocal.RP):
                sendRP();
            case(Protocal.FF):
                sendFF();
        }
    }
    /**发送请求节点列表的请求，等待返回，更新数据库*/
    private void sendRPL(){
        Socket socket=new Socket(this.dst.getstrip(),this.dst.getport());
        PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
        out.print(String.format("%s%s%s",this.genHead(),this.content,this.genTail()));
        out.close();
        //还要等待接收返回的数据并更新数据库
    }
    /**发送请求帖子请求，等待返回，更新数据库
       请求什么时间之后的那个时间是在Transmission中setContent时指定的*/
    private void sendRP(){
        //发送TCP请求
        //等待返回
        //更新数据库
    }
    /**泛洪法发送帖子*/
    private void sendFF(){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c=DriverManager.getConnection("jdbc:sqlite:Datas.db");
            Statement stmt=c.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT * FROM PEER ORDER BY T1 DESC;");
            while(rs.next()){
                String iport=rs.getString("IPORT");
                String key=rs.getString("KEY");
                //用Peer(iport,key)生成peer并把UDP包发出去
            }
            rs.close();stmt.close();c.close();
        }catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    /**生成数据报头*/
    public String genHead(){
        String s=String.format("[%d:%s]\t\n[]\t\n",this.type,Protocal.PSTR[this.type]);
        return s;
    }
    /**生成数据报尾*/
    public String genTail(){
        return String.format("\t\n[END]");
    }

    public void setContent(String s){
        this.content=s;
    }
    public String getContent(){
        return this.content;
    }
}
