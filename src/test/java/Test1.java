import com.cyparty.laihui.utilities.Base64Utils;
import com.cyparty.laihui.utilities.RSAUtils;

import java.io.InputStream;
import java.security.PrivateKey;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Administrator on 2017/4/5.
 */
public class Test1 {
    public static void main(String []args) throws Exception {
        Date date = new Date();
        long s = -date.getTime();
        System.out.println(-Integer.parseInt((new Date().getTime()+"").substring(4,13)));
    }
}
