import Peer;
import java.io.*;
import java.net.*;
/**
    The class deals with network transmission.
    @author Sun Youran
*/
public class Transmission{

    byte type;//2:tcp,3:udp
    Object content;
    void send(Peer dst){  // 点对点传输
        Socket s=new Socket(dst.getstrip(),dst.getport());
        PrintWriter out=new PrintWriter(s.getOutputStream(),true);
        out.println(content.toString);
        out.close();
    }
    static void receive(Transmission transmission){ //通过这个函数调用之后4个函数，不一定直接调用，也有可能是在启动新线程后再调用
    //如果遇到的是后两种Transmission则还要负则继续floodfill


    }
    void floodfill(){ // 在这里先生成GlobalActivePeerList
        //生成Peer[] aPeerList
        for(Peer aPeer:aPeerList){
            send(aPeer);
        }
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
/*UDP Client

*/
