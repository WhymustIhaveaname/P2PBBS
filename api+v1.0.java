class Peer
{
    String ip;
    int port, lastActiveTime;
}

/**
    孙悠然在2018_5_26日修改
*/
class Transmission{
    byte type;
    Object content;
    private Socket socket;
    Transmission(){}
    Transmission(byte type,Object content){}
    void send(Peer destination){}
    void floodfill(){}
    void listen(){}
    void handleGlobalActivePeerListRequest(Peer src){}
    void handleGlobalActivePeerListResponse(){}
    void handleHeartbeat(String time){}
    void handlePost(Post post){}
}


class GlobalActivePeerList
{
    static Peer[] peer;

    static void loadFromFile() //开启app时从文件读入到内存中
    {

    }

    static void saveToFile() //退出app时保存到文件
    {

    }

    static Peer[] generateCopy()
    {

    }

    static void add(Peer peer)
    {

    }
}


class RecentActivePeerList
{
    static Peer[] peer;
    static int ACTIVE_DURATION; //多少时间内算“最近”

    static void loadFromFile()
    {

    }

    static void saveToFile()
    {

    }

    static Peer[] generateCopy()
    {

    }

    static void add(Peer peer)
    {

    }
}


class Post
{
    String time, parentPostHash, content;
}


class PostList
{
    Post[] Post; // 始终按时间顺序排序

    static void loadFromFile() //
    {

    }

    static void saveToFile()
    {

    }

    static Peer[] generate()
    {

    }

    static void add(Post post)
    {

    }
}


class GUI
{
    static void render()
    {
        // GUI里有比较重要的一块是，给出按时间排好序的Post列表，如何把它们整理成一个个主题帖以及它们的回复。一个主题帖是一棵树，同一个主题的回复是按时间顺序显示的，同时每个回复会显示回复的是哪个帖子。参考树洞的效果。应该不会很难。
    }
}


class Main()
{
    public static void main()
    {
        //万物之源，在这里启动GUI、开启多线程等
    }
}
