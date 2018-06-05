/**
    定义了点对点传输协议的样子。定义了一些常量。
    RPL：请求节点列表
        [1:RPL]\r\n[END]
    FF：泛洪
        [2:FF]\r\n
        Post.toString()或[时间,哈希,父哈希,内容]
        \r\n[END]
    HB：心跳
        [3:HB]\r\n
        [x.x.x.x:x]
        \r\n[END]
    RP：请求帖子
        [4:RP]\r\n
        [BEFORE:TimeStramp]
        \r\n[END]
    RPLR：请求节点列表返回
        [5:RPLR]\r\n
        [[x.x.x.x:x,pk,t1],[x.x.x.x:x,pk,t1],...,]
        \r\n[END]
    RPR：请求帖子返回
        [6:RPR]\r\n
        [p1.toString,p2.toString,p3.toString]
        \r\n[END]
    其中p1.toString为[时间,哈希,父哈希,内容]
    RPK：请求公钥
        [7:RPK]\r\n
        [一个节点,...]
        \r\n[END]
    RPKR:请求公钥返回
        [8:RPKR]\r\n
        [一个节点:公钥,...]
        \r\n[END]

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
    public static final String[] PSTR={"","RPL","FF","HB","RP","RPLR","RPR","RPK","RPKR"};
    /**不同协议对应报头的字符串*/
    public static final String[] PHSTR={"","[1:RPL]","[2:FF]","[3:HB]","[4:RP]"
                                ,"[5:RPLR]","[6:RPR]","[7:RPK]","[8:RPKR]"};
    /**生成数据报头*/
    public static String genHead(byte t){
        String s=String.format("[%d:%s]\r\n",t,Protocal.PSTR[t]);
        return s;
    }
    /**生成数据报尾*/
    public static String genTail(byte t){
        return String.format("\r\n[END]");
    }
    /**确认报头的正确性，把数据报的第一行和期待的状态输入，
    如果对了什么也不会发生，如果不对抛出一个异常*/
    public static void confirmHead(String h,byte t)throws P2PBBSException{
        try{
            if(!(Protocal.PHSTR[t].equals(h))){
                throw new P2PBBSException("Header error.");
            }
        }catch(ArrayIndexOutOfBoundsException e){
            throw new P2PBBSException("Header error.");
        }
    }
    public static void main(String args[]){
        try{
            Protocal.confirmHead("[1:RPL]",(byte)1);
            Protocal.confirmHead("[1:RPL]",(byte)2);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
