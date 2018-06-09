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
    
    /**将一组帖子插入数据库。如果数据库操作抛出了除了”条目已存在“以外的异常，则返回-1，否则返回成功插入了几条不在数据库中的帖子*/
    public static int insertPost(Post[] post)
    {
        Connection connection = null;
        PreparedStatement statementInsertPost = null;
        String statementInsertPost_String = "INSERT INTO POST(TIME,HASH,PHASH,CONTENT) VALUES(?,?,?,?);";
        boolean unexpectedExceptionOccured = false;
        int newPostCount = 0;
        
        try
        {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Datas.db");
            statementInsertPost = connection.prepareStatement(statementInsertPost_String);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }
        
        for (int i=0; i<post.length; ++i)
        {
            try
            {
                statementInsertPost.clearParameters();
                statementInsertPost.setLong(1, post[i].getTime());
                statementInsertPost.setInt(2, post[i].hashCode());
                statementInsertPost.setInt(3, post[i].getParentHashCode());
                statementInsertPost.setString(4, post[i].getContent());
                try
                {
                    statementInsertPost.executeUpdate();
                    newPostCount++;
                }
                catch (org.sqlite.SQLiteException e)
                {
                    org.sqlite.SQLiteErrorCode errorCode = e.getResultCode();
                    if (errorCode != org.sqlite.SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY)
                    {
                        unexpectedExceptionOccured = true;
                        e.printStackTrace();
                    }
                    else
                    {
                        log.info("post "+post[i].toString()+" exists");
                    }
                    statementInsertPost = connection.prepareStatement(statementInsertPost_String);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        try
        {
            statementInsertPost.close();
            connection.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.warning("exception occured while closing");
            unexpectedExceptionOccured = true;
        }
        
        if (unexpectedExceptionOccured) return -1; else return newPostCount;
    }
    
    /**将一个帖子插入数据库。重载了插入一组帖子的insertPost，返回值与之相同。*/
    public static int insertPost(Post post)
    {
        return insertPost(new Post[]{post});
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
