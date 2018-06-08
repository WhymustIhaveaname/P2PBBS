/**
    Post类，表示一个帖子。

    @author WhyMustIHaveAName
*/
public class Post{
    /**没有父帖子时的哈希*/
    public static final int THEHASHCODE=0;
    public static final char COMMA = '\ue000';
    public static final char LEFT_BRACKET = '\ue001';
    public static final char RIGHT_BRACKET = '\ue002';
    public static final char LINE_BREAK = '\ue003';
    /**帖子发出的时间，以自纪元以来的秒数表示，为一个long（因为int只能存大约30年）*/
    private long time;
    /**帖子的内容，为一个字符串*/
    private String content;
    /**父帖子的哈希值，为一个int*/
    private int parentHashCode;
    /**以c为内容以t为时间构造Post，parentHashCode默认为THEHASHCODE*/
    Post(long t,String c){
        this.time=t;
        this.content=c;
        this.parentHashCode=Post.THEHASHCODE;
    }
    /**以c为内容以t为时间以f为父哈希值构造Post*/
    Post(long t,String c,int f){
        this.time=t;
        this.content=c;
        this.parentHashCode=f;
    }
    /**返回这个帖子的哈希，一个int型整数*/
    public int hashCode(){
        String h=String.format("%d%d%d",content.hashCode(),parentHashCode,time);
        //time在最后使得时间接近的内容相同的帖子有相近的哈希，
        //同时保证了一秒内只能有一条内容相同的帖子被发送
        return h.hashCode();
    }
    /**将帖子中的"["、"]"、","、"\r\n"转义*/
    public static String escape(String s)
    {
        return s.replace(',', COMMA)
                .replace('[', LEFT_BRACKET)
                .replace(']', RIGHT_BRACKET)
                .replace("\r\n", String.valueOf(LINE_BREAK));
    }
    /**将帖子中转义过的"["、"]"、","、"\r\n"还原*/
    public static String reverseEscape(String s)
    {
        return s.replace(COMMA, ',')
                .replace(LEFT_BRACKET, '[')
                .replace(RIGHT_BRACKET, ']')
                .replace(String.valueOf(LINE_BREAK), "\r\n");
    }
    /**
        这就是想象中会在数据报中传送的帖子的样子
        [时间,哈希,父哈希,内容]
    */
    public String toString(){
        String s=String.format("[%d,%d,%d,%s]",this.time,this.hashCode(),
                               this.parentHashCode,
                               Post.escape(this.content));
        return s;
    }
    /**运行main函数可以测试类的好坏*/
    public static void main(String argv[]){
        Post p=new Post(1,"helloworld");
        System.out.println(p.toString());
        System.out.println(p.hashCode());
        p.setTime(2);
        System.out.println(p.toString());
        System.out.println(p.hashCode());
        p.setContent("Helloworld");
        System.out.println(p.toString());
        System.out.println(p.hashCode());
        p.setParentHashCode(1);
        System.out.println(p.toString());
        System.out.println(p.hashCode());
    }
    public void setTime(long t){
        this.time=t;
    }
    public void setParentHashCode(int p){
        this.parentHashCode=p;
    }
    public void setContent(String s){
        this.content=s;
    }
    public long getTime(){
        return this.time;
    }
    public int getParentHashCode(){
        return this.parentHashCode;
    }
    public String getContent(){
        return this.content;
    }
}
