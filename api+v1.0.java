/**设计文档：孙悠然版
一。关于通信协议
    一个数据报包括三个部分：数据报头，数据报体和数据报尾。数据报头格式如下：
    [num:STR]\r\n[]\r\n
其中num是协议类型的编号，STR是协议类型对应的字符串，详见下面或者Protocol的注释。
数据报头中的空方括号是留待将来RSA加密发送嗯公钥使用。数据报体为正文，依协议不同
而不同。数据报尾格式如下：
    \r\n[END]
用于分界和校验。
二。关于通信过程
    1.请求Peer列表的操作
        编号：1，对应字符串：RPL，请求时发送：
        [1:RPL]\r\n
        []
        \r\n[END]
    对方peer处理后会返回：
        [5:RPLR]\r\n
        [[一个信息],[又一个信息],...]
        \r\n[END]
    其中pk是公钥，没有就是null，t1是最近活跃时间。本机接受对方的返
    回数据后更新数据库。用TCP实现。
        主程序调用Transmission.requestPeerList(peer);会打开一个新线程，此线程会
    向刚刚指定的peer请求Peer列表，并更新数据库。在requestPeerList(peer)中，首先
    创建Guest对象g，将其type设置为1，将其content设置为"[]"，开启新线程执行之。
    在g的内部，run()被执行，它会检测this.type，发现是1，之后向Peer dst发送请求
    报文，等待返回，处理返回，更新数据库，线程结束。对方服务器会在开始时运行
        Transmission.listen(port);
    打开一个新线程用于循环监听port端口。在listenTCP()内部，它会初始化一个Server的实
    例ser，之后ser被用于初始化新线程Thread t并运行。在Server内部的run函数中，它
    会一值监听port端口的tcp数据报，接收到数据报之后，处理数据报的头部获悉请求类
    型为请求Peer列表后，在本线程内处理并返回对应的消息。在本线程内是因为在数据库
    中找到Peer们并返回不会用太久，好处是不但可以降低编程难度，而且可以减小系统压
    力。
        返回的body设计成那样是因为那样可以用json的包来处理。
    2.请求不在线期间的帖子的操作
        编号：4,对应字符串：RP，请求时body为：
        [BEFORE:TimeStramp]
    对方接收到请求后返回的body为：
        [p1.toString,p2.toString,p3.toString]
    p1p2p3是帖子们，toString方法在帖子中已经写好方便使用。用TCP实现。
        主程序调用Transmission.requestPost(peer,time);这会启动一个新线程，与peer
    通信，请求time之后的帖子然后结束。在requestPost内部，它首先建立Guest对象g，
    设置g.content，用g初始化新线程并打开。在g的run函数内，它会检测自己的this.type
    ，发现是4，于是向this.dst发送请求，等待对方返回，处理返回的数据并更新数据库。
    在通信协议的另一端，一个listen正在监听端口，它会收到一个数据报，解析后发现是
    请求Post列表，于是立即从数据库中提取数据组织成报文返回。考虑到这并不会花费
    太多时间，所以也在循环监听内部解决。
    3.心跳包
        编号：3,字符串：HB，协议body：
        [IPORT:"x.x.x.x:x",BEATAT:TimeStramp]
        用UDP向所有已知节点发送心跳包，行为要模仿udp访问百度的行为。
        为了补充这种心跳包方法的不足，还要辅以一个节点信息更新协议。
    4.泛洪法发送Post
        编号：2,字符串：FF，数据报体：
        [时间,哈希,父哈希,内容]
        UDPServer受到一个泛洪法数据报后，检验自己是否收到过，如果收到过，无视之，
        如果没收到过，更新数据库，调用Transmission.floodfill()。数据报体中的哈希
        有助于校验。泛洪法用udp实现，否则等待tcp连接的那些时间就够受的。
    5.节点信息更新协议
        可以利用请求节点列表的协议请求节点列表，所以只需要加一个咨询节点公钥的协议。
        编号：7,字符串：RPK（Request Public Key），数据报体：
        [一个节点,另一个节点,...]
        返回编号：8,字符串：RPKR，数据报体：
        [[一个节点:公钥],[另一个节点:公钥],...]
三。类的设计
    Peer类实现了节点对象，有参数strip，port，publicKeyString。提供toString和hashCode方法。
    Post类实现了帖子对象，有参数time，content，parentHashCode。提供toString和hshCode方法。
    DataBase类进行数据库相关操作，目前只有initTables用于完成数据库中表的初始化。
    Protocal类有关于协议，定义了一系列常量，有助于提高代码可读性。
    Transmission类是想最后封装好提供给主程序使用的类。提供诸多方法，这个类应当是
最后一层封装。其依赖于以上诸类和下面的Server类以及Client类。
    Server类用于实现tcp和udp两个主机，用于接受请求并作出响应，或是返回请求，或是
继续泛洪。其他通信交由Client完成。
    Client类用于处理发起请求并且等待请求返回处理信息 和 泛洪法中的一步泛洪。
四。等着你写的东西
    Server的：
        listenUDP()
        dealFF()
    Client的：
        sendFF()
    数据库那里有一个防止sql注入，还有定时发送心跳包的部分WhyMustIHaveAName想写。

*/

/*用于生成java注释的命令
javadoc -d doc -windowtitle P2PBBSAPI -version -author -private
javadoc -d doc -windowtitle P2PBBSAPI -version -author -private Post.java Peer.java Protocal.java DataBase.java Server.java Client.java Transmission.java
*/
/*用于参考的代码
TCP Client
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
TCP Server
    ServerSocket ss = new ServerSocket(3333);
    Socket socket = ss.accept();
    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    String str = in.readLine();
    PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
    out.println(info);
    in.close();
    out.flush();
    out.close();
UDP通信
    datagramSocket = new DatagramSocket(PORT_NUM);
    datagramPacket = new DatagramPacket(receMsgs, receMsgs.length);

*/
/**
泛洪法发送心跳包
public static void sendHeartbeat(){
    Guest g=new Guest(Protocal.HB);
    g.setContent(String.format("[[IPORT:\"%s\"],BEATAT:%d]",iport字符串,Date().getTime()/1000));
    Thread t=new Thread(g);
    t.start();
    try{
        Class.forName("org.sqlite.JDBC");
        Connection c=DriverManager.getConnection("jdbc:sqlite:PeerList.db");
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM PEER ORDER BY T1 DESC;");
        while(rs.next()){
            String iport=rs.getString("IPORT");
            String key=rs.getString("KEY");
            Guest g=new Guest(Protocal.HB,new Peer(iport,key));

            Thread t=new Thread(g);
            t.start();
        }
        rs.close();stmt.close();c.close();
    }catch ( Exception e ) {
        System.err.println(e.getClass().getName()+": "+e.getMessage());
    }
}
*/
