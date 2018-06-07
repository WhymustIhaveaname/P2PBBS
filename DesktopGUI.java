import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.basic.BasicButtonListener;
import java.util.logging.*;
import java.sql.*;
import javax.swing.*;
public class DesktopGUI implements MouseListener,Runnable{
    private Frame F;
    private Panel Pnorth,Pwest,Pcenter,Pnorth1,Pnorth2,Pnorth3,Pnorth4;;
    private Button Binit,Bdel;
    private Button Btcp,Budp,Bhb,Bviewposts,Bviewpeers;
    private Button Brpl,Brp,Bpost;
    private Label Lportnum,Lpeernum,Lfrom;
    private TextArea TA;
    private TextField TF,TFPeer;
    private static Logger Log=Logger.getLogger("lavasoft");
    private byte TAFlag=0;
    private void initLayout(){
        F=new Frame("P2PBBS Desktop");
        Pnorth=new Panel(new GridLayout(5,1));
        Pwest=new Panel(new GridLayout(10,1));
        Pcenter=new Panel();
        Pnorth1=new Panel(new FlowLayout());
        Pnorth2=new Panel(new FlowLayout());
        Pnorth3=new Panel(new FlowLayout());
        Pnorth4=new Panel(new FlowLayout());
        Binit=new Button("Init DB");
        Bdel=new Button("Del DB");
        Btcp=new Button("Start TCP server");
        Budp=new Button("Start UDP server");
        Bhb=new Button("Start HB server");
        Bviewposts=new Button("View posts");
        Bviewpeers=new Button("View peers");
        Brpl=new Button("Request peer list");
        Brp=new Button("Request Post");
        Bpost=new Button("Post");
        Lportnum=new Label();
        Lportnum.setText("Port:");
        Lpeernum=new Label();
        Lpeernum.setText("Online peer num:\nTotal peer num:");
        Lfrom=new Label();
        Lfrom.setText("Peer:");
        TA=new TextArea(24,80);showPost();
        TF=new TextField("3333",6);
        TFPeer=new TextField("",22);
        Pnorth1.add(Binit);
        Pnorth1.add(Bdel);
        Pnorth2.add(Lfrom);
        Pnorth2.add(TFPeer);
        Pnorth2.add(Brpl);
        Pnorth2.add(Brp);
        Pnorth3.add(Lportnum);
        Pnorth3.add(TF);
        Pnorth3.add(Btcp);
        Pnorth3.add(Budp);
        Pnorth3.add(Bhb);
        Pnorth4.add(Bpost);
        Pnorth4.add(Lpeernum);
        Pnorth.add(Pnorth1);
        Pnorth.add(Pnorth2);
        Pnorth.add(Pnorth3);
        Pnorth.add(Pnorth4);
        Pwest.add(Bviewposts);
        Pwest.add(Bviewpeers);
        Pcenter.add(TA);
        F.add(Pnorth,BorderLayout.NORTH);
        F.add(Pwest,BorderLayout.WEST);
        F.add(Pcenter,BorderLayout.CENTER);
        F.pack();
        F.setVisible(true);
    }
    private void initListener(){
        F.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
        Btcp.setActionCommand("STCP");
        Btcp.addMouseListener(this);
        Budp.addMouseListener(this);
        Bhb.addMouseListener(this);
        Bviewposts.addMouseListener(this);
        Bviewpeers.addMouseListener(this);
        Binit.addMouseListener(this);
        Bdel.addMouseListener(this);
        Brpl.addMouseListener(this);
        Brp.addMouseListener(this);
        Bpost.addMouseListener(this);
    }
    @Override
    public void mouseClicked(MouseEvent e){
        //Log.info(e.paramString());
        //Log.info(Integer.toString(e.getButton()));
        Button Btemp=(Button)e.getSource();
        String label=Btemp.getLabel();
        switch(label){
            case("Start TCP server"):
                startTCPServer(Btemp);break;
            case("Start UDP server"):
                startUDPServer(Btemp);break;
            case("Start HB server"):
                startHBServer(Btemp);break;
            case("View posts"):
                TAFlag=0;
                showPost();
                break;
            case("View peers"):
                TAFlag=1;
                showPeer();
                break;
            case("Request peer list"):
                Transmission.requestPeerList(new Peer(TFPeer.getText(),null));
                break;
            case("Request Post"):
                Transmission.requestPost(new Peer(TFPeer.getText(),null),0);
                break;
            case("Init DB"):
                DataBase.initTables();break;
            case("Del DB"):
                DataBase.delDataBase();break;
            case("Post"):
                PostWindow PW=new PostWindow();
                PW.init();
                break;
        }
    }
    private void startTCPServer(Button B){
        int port=Integer.parseInt(TF.getText());
        TF.setEditable(false);
        Transmission.listenTCP(port);
        B.setLabel("TCP port:"+Integer.toString(port));
    }
    private void startUDPServer(Button B){
        int port=Integer.parseInt(TF.getText());
        TF.setEditable(false);
        Transmission.listenUDP(port);
        B.setLabel("UDP port:"+Integer.toString(port));
    }
    private void startHBServer(Button B){
        int port=Integer.parseInt(TF.getText());
        TF.setEditable(false);
        Transmission.sendHB(port);
        B.setLabel("HB port:"+Integer.toString(port));
    }
    private void showPost(){
        StringBuilder Sb=new StringBuilder();
        try{
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:Datas.db");
            String s="SELECT * FROM POST ORDER BY TIME DESC;";
            PreparedStatement preStat=conn.prepareStatement(s);
            ResultSet rs=preStat.executeQuery();
            while(rs.next()){
                long time=rs.getLong("TIME");
                int hash=rs.getInt("HASH");
                int phash=rs.getInt("PHASH");
                String apost=String.format("%010d %010d %010d\n%s\n",time,hash,phash,rs.getString("CONTENT"));
                Sb.append(apost);
            }
            rs.close();preStat.close();conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        TA.setText(Sb.toString());
    }
    private void showPeer(){
        StringBuilder Sb=new StringBuilder();
        try{
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:Datas.db");
            String s="SELECT * FROM PEER ORDER BY T1 DESC;";
            PreparedStatement preStat=conn.prepareStatement(s);
            ResultSet rs=preStat.executeQuery();
            while(rs.next()){
                long t1=rs.getLong("T1");
                long t2=rs.getLong("T2");
                long t3=rs.getLong("T3");
                String apost=String.format("%s %010d %010d %010d\n",rs.getString("IPORT"),t1,t2,t3);
                Sb.append(apost);
            }
            rs.close();preStat.close();conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        TA.setText(Sb.toString());
    }
    public void run(){
        while(true){
            try{
                Class.forName("org.sqlite.JDBC");
                Connection conn = DriverManager.getConnection("jdbc:sqlite:Datas.db");
                String s="SELECT COUNT(*) AS NUM FROM(SELECT * FROM PEER WHERE T1>?);";
                PreparedStatement preStat=conn.prepareStatement(s);
                preStat.setLong(1,Transmission.getNetTime()-10*60);
                ResultSet rs=preStat.executeQuery();
                int n1=rs.getInt("NUM");
                rs.close();preStat.close();conn.close();
                Lpeernum.setText(String.format("online peer num:%d",n1));
            }catch(Exception e){
                e.printStackTrace();
            }
            switch(TAFlag){
                case 0:showPost();break;
                case 1:showPeer();break;
            }
            try{
                Thread.sleep(1000);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e){
        //Log.info(e.paramString());
    }
    @Override
    public void mouseExited(MouseEvent e){
        //Log.info(e.paramString());
    }
    @Override
    public void mousePressed(MouseEvent e){
        //Log.info(e.paramString());
    }
    @Override
    public void mouseReleased(MouseEvent e){
        //Log.info(e.paramString());
    }
    public static void main(String ags[]){
        DesktopGUI D=new DesktopGUI();
        D.initLayout();
        D.initListener();
        Thread t=new Thread(D);
        t.start();
        PostWindow PW=new PostWindow();
        //PW.init();
    }
}
class PostWindow{
    private Frame F;
    private JTextArea JTA;
    private Button Bp;
    private Panel P;
    public void init(){
        F=new Frame("Post");
        P=new Panel(new GridLayout(10,1));
        JTA=new JTextArea(16,40);
        Bp=new Button("Post");
        P.add(Bp);
        F.add(P,BorderLayout.EAST);
        F.add(JTA,BorderLayout.WEST);
        F.pack();
        F.setVisible(true);
        F.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                F.dispose();
            }
        });
    }


}
