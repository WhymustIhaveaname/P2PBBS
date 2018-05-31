/**
    定义了点对点传输协议的样子。定义了一些常量。
    RPL：请求节点列表
        [1:RPL]\t\n[]\t\n[END]
    FF：泛洪
        [2:FF]\t\n[]\t\n
        Post.toString()或[时间,哈希,父哈希,内容]
        \t\n[END]
    HB：心跳
        [3:HB]\t\n[]\t\n
        [[x.x.x.x:x,pk,BEATAT:blabla],...]
        \t\n[END]
    RP：请求帖子
        [4:RP]\t\n[]\t\n
        [BEFORE:TimeStramp]
        \t\n[END]
    RPLR：请求节点列表返回
        [5:RPLR]\t\n[]\t\n
        [[x.x.x.x:x,pk],[x.x.x.x:x,pk],...]
        \t\n[END]
    RPR：请求帖子返回
        [6:RPR]\t\n
        [p1.toString,p2.toString,p3.toString]
        \t\n[END]
    @author WhyMustIHaveAName
*/
public class Protocal{
    /**请求节点列表*/
    public static final byte RPL=1;//Request Peer List
    /**泛洪法发送帖子*/
    public static final byte FF=2;//FloodFill
    /**心跳包*/
    public static final byte HB=3;//Heart Beat
    /**请求帖子*/
    public static final byte RP=4;//Request Post
    /**请求节点列表返回*/
    public static final byte RPLR=5;//Request Peer List Reply
    /**请求帖子列表返回*/
    public static final byte RPR=6;//Request Post Reply
    /**不同的协议对应的字符串*/
    public static final String[] PSTR={"","RPL","FF","HB","RP","RPLR","RPR"};
}
