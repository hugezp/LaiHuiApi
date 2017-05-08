import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.cyparty.laihui.db.AppDB;
import com.cyparty.laihui.utilities.Base64Utils;
import com.cyparty.laihui.utilities.PayConfigUtils;
import com.cyparty.laihui.utilities.RSAUtils;
import org.springframework.stereotype.Controller;

import java.io.InputStream;
import java.security.PrivateKey;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Administrator on 2017/4/5.
 */
@Controller
public class Test1 {
    AppDB appDB;

    public static void main(String[]args) {

    }

}
