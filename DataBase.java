import java.io.*;
import java.sql.*;
import java.util.logging.*;
/**
    有关数据库的操作。
    @author WhyMustIHaveAName
*/
public class DataBase{
    public String s;
    private static Logger log=Logger.getLogger("lavasoft");
    public static void initTables(){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c=DriverManager.getConnection("jdbc:sqlite:Datas.db");
            Statement stmt=c.createStatement();
            String sql="CREATE TABLE PEER(IPORT TEXT PRIMARY KEY NOT NULL,"+
                                  "PUBKEY TEXT,T1 BIGINT NOT NULL,T2 BIGINT,T3 BIGINT);";
            stmt.executeUpdate(sql);
            System.out.println("PEER created");
            sql="CREATE TABLE POST(TIME BIGINT NOT NULL,HASH INT PRIMARY KEY NOT NULL,"+
                                     "PHASH INT NOT NULL,CONTENT TEXT NOT NULL);";
            stmt.executeUpdate(sql);
            System.out.println("POST created");
            stmt.close();c.close();
        }catch(Exception e){
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }
    public static void delDataBase(){
        File db=new File("Datas.db");
        if(db.delete()){
            log.info("del db suc");
        }else{
            log.warning("del db err");
        }
    }
    public static void exp1(){
        try{
            Class.forName("org.sqlite.JDBC");
            Connection c=DriverManager.getConnection("jdbc:sqlite:Datas.db");
            Statement stmt=c.createStatement();
            ResultSet rs=stmt.executeQuery( "SELECT * FROM PEER ORDER BY T1 DESC;");
            while(rs.next()){
                System.out.println(rs.getString("IPORT"));
                System.out.println(rs.getString("PUBKEY"));
                if(rs.getString("PUBKEY")==null){System.out.println("eql to null");}
                System.out.println(rs.getInt("T1"));
                System.out.println(rs.getInt("T2"));
                System.out.println(rs.getInt("T3"));
            }
        }catch(Exception e){
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }
    public static void main(String args[]){
        //DataBase.initTables();
        //DataBase.exp1();
        DataBase a=new DataBase();
        a.s=null;
        System.out.println(a.s);
    }
}
