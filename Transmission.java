import Peer;
import java.io.*;
import java.net.*;
/**
    The class deals with network transmission.
    想要请求活跃Peer列表怎么办？
        Transmission t=new Transmission(type);
        t.send(peer);
    想要开始泛洪怎么办?
        Transmission t=new Transmission();
        t.setContent(content);
        t.floodfill();
    想要接收数据怎么办？
        Transmission demo=new Transmission();
        demo.listen();//会一直循环监听，并开启多线程处理好一切

    @author Sun Youran
*/
public class Transmission implements Runnable{
    /**
        @type P2P协议的类型
            1:GlobalActivePeerListRequest
            2:GlobalActivePeerListResponse
            3:Heartbeat
            4:Post
    */
    byte type=-1;
    String content="error";
    private Socket socket;
    /**
        构造函数们
    */
    Transmission(){}
    Transmission(byte type){
        this.type=type;
    }
    /**
        点对点传输，使用时只需要：
            Transmission.send(peer);
    */
    public void send(Peer dst){
        Socket socket=new Socket(dst.getstrip(),dst.getport());
        PrintWriter out=new PrintWriter(socket.getOutputStream(),true);//true 是缓冲区满自动发送
        //这一行应该根据type输出一行指定协议类型的信息
        out.println(content);
        out.close();
    }
    public void floodfill(){
        //根据content生成散列，如果没收到过就发送，收到过就算了
        //生成要发送的Peer列表
        //挨个发送
    }
    /**
        聆听网络通信，每一个端口只需要开一个，它会处理好一切
    */
    public void listen(){
        ServerSocket ss = new ServerSocket(3333);//这个端口也应该怎么改一下
        whlie(1){//何时退出这个死循环我还没有想好
            Transmission subtrans=new Transmission();
            subtrans.setSocket(ss.accept());
            Thread t=new Thread(subtrans,"thread name");
            t.start();
        }
    }
    /**
        在
            Thread t=new Thread(aTransmission,"thread name");
            t.start();
        后会执行的函数，负责根据this.socket确定通信类型并进行后续的处理
    */
    void run(){
        //从this.socket中确定type和content
        switch(this.type){
            case(1):
                //handleGlobalActivePeerListRequest
                break;
            case(2):
                //handleGlobalActivePeerListResponse
                break;
            case(3):
                //handleHeartbeat
                break;
            case(4):
                //handlePost
                break;
        }
    }
    private void handleGlobalActivePeerListRequest(){
        //根据this.socket中的信息应该能新建一个Peer并返回给他他想要的东西
    }
    private void handleGlobalActivePeerListResponse(){
        //更新Peerlist
    }
    private void handleHeartbeat(){

    }
    private void handleFloodfill(){
        //查看是否收到过这个帖子如果没有则写入记录并直接调用floodfill即可
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
    public void setContent(String c){
        this.content=c;
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
