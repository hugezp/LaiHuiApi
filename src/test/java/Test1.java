import com.cyparty.laihui.utilities.Base64Utils;
import com.cyparty.laihui.utilities.RSAUtils;

import java.io.InputStream;
import java.security.PrivateKey;

/**
 * Created by Administrator on 2017/4/5.
 */
public class Test1 {
    public static void main(String []args) throws Exception {
        String s ="{\"status\":200}";
        System.out.println(s.substring(10,13));
    }
}
