import com.cyparty.laihui.utilities.Base64Utils;
import com.cyparty.laihui.utilities.RSAUtils;

import java.io.InputStream;
import java.security.PrivateKey;

/**
 * Created by Administrator on 2017/4/5.
 */
public class Test1 {
    public static void main(String []args) throws Exception {
        //InputStream inPrivate = getResources().getAssets().open("pkcs8_private_key.pem");
        PrivateKey privateKey = RSAUtils.loadPrivateKey("MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAN1TuADz05124wfx\n" +
                "L2gCQtygb7uKOp/UHrcszmNRlrzoHYzjrlnIBPw7FYc33iSPy9Ikt+QCINLK3wfm\n" +
                "3aCFQHXnfWckLj/SjkG29ADvRzukZABuuTJmD138byCjp6EkxGEjFHfJbGfn6xrd\n" +
                "dJLoLvTHrUcZFuA0dL+CYFOxkmbTAgMBAAECgYEAsoIldE80LFqI/gzfCWpZdwzH\n" +
                "UWFJq0MwitjOlXhmtXIY81tnce5LZwxYbrj2qFJ6N8F5t6Knypvpirv/oMoYk//k\n" +
                "bD4BuzjKUYd4uATC8sC0zZGGD5trDHdSZupjZXcK895LcY0mO05f60qKOvw0kM+A\n" +
                "PwTyMr0mKcbvg9rxJZECQQDwv83o44495mv8A2PHzLGQIYsXpM/6c4IwCacKnTLm\n" +
                "q7WloSydkYlYbtgaRY/NKVkkE8vJM/fcyYuN5NvrtVlfAkEA61jyf61pKcTpZP3s\n" +
                "5QQd3XnS93U+BYpxcNPKnEZDiI2byE9FW0y16ek5U2Lncu/sz2fbKYXI5OQdhp0a\n" +
                "yqdDDQJBAKqm2xeFDdPU3KCD6pu6/fZzwHufCQm4DQVuIikE6wrVlmn0iIKcoiW8\n" +
                "VJSy0E8WSyimm6JgowavGqwXGMjJALMCQQCc/Y991epxv3XFNZGBz3YwmCQ/iAjg\n" +
                "X1J+5figuEoyW+wP+bkVRx2ONFJWKoKj7CH21Jugniq9bOG5OzF0/5A9AkEA4VXI\n" +
                "S0OgQo8RS0OepTzdobtNwXQSG5sCEM29dJAB52hfFZx6AEPxJgk2nFhXh84AqUh4\n" +
                "NQBFnUEpS9tg9S6YSA==");
// 因为RSA加密后的内容经Base64再加密转换了一下，所以先Base64解密回来再给RSA解密
        byte[] encryptedData = Base64Utils.decode("oERwWurC5ifRJ5/ps+kEL6tCntNSC7tum4pu/8FR1i2exVEG0yi6H3+T/KPtFekxa/MqC60bY7dJ\n" +
                "kKJreSEmV1of4/Kcu2shvQiliZ5ybLPVvPu6m83QC03OoloHOeUNTTc1UtXSAiqDbDonarHdB1Ex\n" +
                "v4bPKuc7CouUyp0qsUI=");
        byte[] decryptByte = RSAUtils.decryptData(encryptedData, privateKey);
        String decryptStr = new String(decryptByte);
        String s = decryptStr;
        System.out.print(s);
    }
}
