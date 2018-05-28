public class Server implements Runnable{
    private int port;
    Server(int p){
        this.port=p;
    }
    void Run(){
        ServerSocket ss = new ServerSocket(this.port);
        whlie(1){//何时退出这个死循环我还没有想好

        }
        //应该实现的功能
        private void handlePeerListRequest(){
            //根据this.socket中的信息应该能新建一个Peer并返回给他他想要的东西
        }
        private void handlePostRequest(){}
        private void handlePeerListResponse(){
            //更新Peerlist
        }
        private void handleHeartbeat(){

        }
        private void handleFloodfill(){
            //查看是否收到过这个帖子如果没有则写入记录并直接调用floodfill即可
        }
}
