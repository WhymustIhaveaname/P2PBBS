import java.security.*;
import java.security.interfaces.*;
import java.math.BigInteger;
import java.io.*;

public class RSA{
    /**产生密钥对，既存储在文件中，又会以KeyPair的形式返回*/
    public static KeyPair generateKey(){
        try{
            //产生公钥和私钥
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            KeyPair kp = kpg.genKeyPair();
            RSAPublicKey pubkey =(RSAPublicKey)kp.getPublic();
            RSAPrivateKey prikey =(RSAPrivateKey)kp.getPrivate();
            //保存
            ObjectOutputStream pubOutStream=new ObjectOutputStream(new FileOutputStream("publicKey"));
            ObjectOutputStream priOutStream=new ObjectOutputStream(new FileOutputStream("privateKey"));
            pubOutStream.writeObject(pubkey);
            priOutStream.writeObject(prikey);
            pubOutStream.close();
            priOutStream.close();
            return kp;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    /**加密*/
    public static byte[] encrypt(byte[] ptext,RSAPublicKey pbk){
        try{
            BigInteger e = pbk.getPublicExponent();
            BigInteger n = pbk.getModulus();
            BigInteger m = new BigInteger(ptext);
            BigInteger c = m.modPow(e,n);
            return c.toByteArray();
        }catch(Exception exce){
            exce.printStackTrace();
        }
        return null;
    }
    /**解密*/
    public static byte[] decrypt(byte[] ctext,RSAPrivateKey prk){
        try{
            BigInteger c = new BigInteger(ctext);
            BigInteger d = prk.getPrivateExponent();
            BigInteger n = prk.getModulus();
            BigInteger m = c.modPow(d, n);
            return m.toByteArray();
        }catch(Exception exce){
            exce.printStackTrace();
        }
        return null;
    }
    public static void main(String args[]){
        //RSA.generateKey();
        String text="Marry has a little lamb";
        try{
            //读取密钥
            ObjectInputStream pubInStream=new ObjectInputStream(new FileInputStream("publicKey"));
            RSAPublicKey pbk = (RSAPublicKey) pubInStream.readObject();
            pubInStream.close();
            //加密
            byte[] ctext=RSA.encrypt(text.getBytes(),pbk);
            //转换为字符串输出
            StringBuilder cStrBuilder=new StringBuilder();
            for(byte i:ctext){
                cStrBuilder.append(String.format("%02x",i));
            }
            String cStr=cStrBuilder.toString();
            System.out.println("Encrypted:"+cStr);

            //读取密钥
            ObjectInputStream priInStream=new ObjectInputStream(new FileInputStream("privateKey"));
            RSAPrivateKey prk = (RSAPrivateKey) priInStream.readObject();
            priInStream.close();
            //解密
            byte[] ptext=RSA.decrypt(ctext,prk);
            String pStr=new String(ptext);
            System.out.println("Decrypted:"+pStr);
        }catch(Exception exce){
            exce.printStackTrace();
        }
    }
}
