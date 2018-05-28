/**
    Peer：孙悠然在20180528修改
*/
public class Peer{
    private String strip;
    private int port;
    private String publicKeyString;
    Peer(String strip,int port){}
    Peer(String strip,int port,String pk){}
    public String toString(){}
    public int hashCode(){}
    public static void main(String argv[]){}
}
/**
    Post：孙悠然在20180528修改
*/
public class Post{
    public static final int THEHASHCODE=0;
    private long time;
    private String  content;
    private int parentHashCode;
    Post(long t,String c){}
    Post(long t,String c,int f){}
    public int hashCode(){}
    public String toString(){}
    public static void main(String argv[]){}
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
