/**
    Post类，表示一个帖子，有三个基本变量：
        @time 帖子发出的时间，以自纪元以来的秒数表示，为一个long（因为int只能存大约30年）
        @content 帖子的内容，为一个字符串
        @parentHashCode 父帖子的哈希值，为一个int
    另外有一个静态常量：
        @THEHASHCODE 为没有父帖子时的哈希

    @author 孙悠然
*/
public class Post{
    public static final int THEHASHCODE=0;
    private long time;
    private String  content;
    private int parentHashCode;
    Post(long t,String c){
        this.time=t;
        this.content=c;
    }
    Post(long t,String c,int f){
        this.time=t;
        this.content=c;
        this.parentHashCode=f;
    }
    /**
        返回这个帖子的哈希，一个int型整数
    */
    public int hashCode(){
        String h=String.format("%d%d%d",content.hashCode(),parentHashCode,time);
        //time在最后使得时间接近的内容相同的帖子有相近的哈希，
        //同时保证了一秒内只能有一条内容相同的帖子被发送
        return h.hashCode();
    }
    /**
        这就是想象中会在数据报中传送的帖子的样子
        [时间,哈希,父哈希,内容]
    */
    public String toString(){
        String s=String.format("[%d,%d,%d,%s]",this.time,this.hashCode(),
                               this.parentHashCode,this.content);
        return s;
    }
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
