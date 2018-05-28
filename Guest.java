import Peer;
import Protocal;
import Post;
/**
    只处里向一个点的发送
    @author 孙悠然
*/
public class Guest implements Runnable{
    private byte type;
    private String content;
    private Peer dst;

    Guest(byte t,Peer d){
        this.type=t;
        this.dst=d;
    }
    /**
        在泛洪法发送Post时会用到
    */
    Guest(byte t,Peer d,Post p){
        this.type=t;
        this.dst=d;
        this.content=String.format("[%d:%s]\t\n%s\t\n[END]",this.type,
                                         Protocal.PSTR[this.type],p.toString());
    }
    /**
        在请求time之前的帖子时会用到
    */
    Guest(byte t,Peer d,long time){
        this.type=t;
        this.dst=d;
        this.content=String.format("[%d:%s]\t\n[%d]\t\n[END]",this.type,
                                                 Protocal.PSTR[this.type],time);
    }
    void run(){
        switch(this.type){
            case(Protocal.RPL)://请求节点列表
                Socket socket=new Socket(this.dst.getstrip(),this.dst.getport());
                PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
                this.content=String.format("[%d:%s]\t\n[END]",
                                            this.type,Protocal.PSTR[this.type]);
                out.print(this.content);
                out.close();
                break;
            case(Protocal.HB)://心跳包
                Socket socket=new Socket(this.dst.getstrip(),this.dst.getport());
                PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
                this.content=String.format("[%d:%s]\t\n[%s:%d][END]",
                                            this.type,Protocal.PSTR[this.type]);
                out.print(this.content);
                out.close();
                break;
            case(Protocal.FF)://泛洪法发送帖子
                Socket socket=new Socket(this.dst.getstrip(),this.dst.getport());
                PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
                out.print(this.content);
                out.close();
                break;
            case(Protocal.RP)://请求帖子
                Socket socket=new Socket(this.dst.getstrip(),this.dst.getport());
                PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
                out.print(this.content);
                out.close();
                break;
        }
    }
}
