package com.cyparty.laihui.utilities;

/**
 * Created by zhu on 2016/12/27.
 */
public class PayConfigUtils {

    private final static String private_key="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALaMBeCgPn1khvzYO9x/8O5mD+4QGiKUVq5kxlxpLq35p+L6zqNCrzwYZWIxFlgtNT3hBbtndRoakcv86nQmLjQchJIo1ktfFjseCm9eMq00vATjX0Emco8Aub7rgU1KsXY3NPZBbyoEbDjWFgAW1ejJGQEgEPq41H9dfMdklMMvAgMBAAECgYEAiKkFEWgFwEwM/qdHEUk67s67qEanujGBPye0lKQtzRL1C+kl33VhyMSeycbj6nlVvZCDgQvvz+4KLkOWpgObXCV4MRJcr8v1uKagpGn1IEY0pBdjQGq/HLFjME3dIqPoxQdN+NgyLx2Q9/C8O3/c8fF9zMSKZK+gfYlvWM0RgJkCQQDnj+8A4b5jq7f7d/AhRf9hLbIYv4wMv9ZBFjdJItg27lv2VwAcVg+6LY0fOv1laOciCzg+JY+mscVGwa+0dNu1AkEAyc/at6N52wV40VMcd6yGcjFN8awEZnWZKaorH2qAp7x7RGeZv2CUVyc1fx5G5OmdASaOr9Kl9BHjZfqmKvMZ0wJAYEAyvzIWOkXBVtR2ys3WpiUhVyofY5lFzI4Yctz59HNFZHKrSBv5s5CFjUqu0z00fbEeMq7YNBjGgG+3whGk1QJBAL4Z46d0gIbGIsbbiUdZ5tOOiiOKrCJvyDUtLK3NPx/CNkN7a6KQsWtfbc8ukA112gWJcoNA5D6C0zfu4bQ3hy0CQBJA+DlKHJgLvo56ypQK0lWD6tUlxIT5pvWYuxYB8fKEANGlQvgc2Ancp3xGwyJdQSRweLkLP4xhZHyfNnEAbAQ=";
    private final static String alipay_public_key="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
    private final static String app_id="2016072001643878";
    private final static String wx_app_id="wx6527ab831ef9f8f0";//公众号id
    private final static String wx_mch_id="1409441702";//商户号
    private final static String wx_app_secret_key="eo2MwtDcK6y3HaWzkbTpMvW8tMMxIbo4";

//    private final static String private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCn4BKXk/DkYfRTSCxXr3hrFjLZ3cHmzpXF0yhmin6dkEdenGZYvUXdwlSaYhzu96E1EFg8pvt6c+E1SNuOGeYmIsQk6EKDQ0iwp0ZU0rbA6KrXa4veOKBQVevoEt/6s71ZuAIuGDatAuQt1qUArFtBbF0V7G0fLLLS9KtLBeRDrHZm0UqN7FzpoQKT6b1OUmwtiBkEpwhgcyOad3wsRG0bUYAQg7CXyLHGm/YgTOLyZjMSh7bRrY3x68TfwwsmTy1i7S5CgrlPaGoFdWDgwgM9+LClB9LxF3kWDzLP7bEsaEsBtFLxyE05P4lyPjmJSReRZaWqDqBqIe5l+gBnJc8RAgMBAAECggEAXACsj8/vbGrWqTHbw26SMzYmRkqHcwPzB0jzUkdCnv2sb4iNesK4YJGZJkbxDnI+MkOz6AGnvvN1EGvOj/FXtbzF/Ggh06hzVQVHwUL+D3kz7/2r2Oz1xzFfqX92qwIImvwCjdqNVIJKJWkGYvkSxicqWNKK91Pa6UxX+DTRZdhif+y5PGGnoRvytWP7mKS0xx9mcebz/pQd60w+g7XE8vJzXvz3DWVq8sIiMorqwjIiwdj1RKTuRYwVRRG6a/sr+LSFj0XyQiKfn6RdkCQNYu2Ah+FM/kfEmL1RZZZhWX6dvwdhdOceWZ3UGGw10sxiCJPr1yzZqoWZggeRMqO/sQKBgQDTuf1qtlGtDqqsiAmxBGRFo/S8fng8GheUHozPXl4a/1HrPTuhhq7gy445+4hySfXLiJo+b998vjtZ9/hcsG0ngTkhlFJEDqmjdq9TRHw/9SP2zNVfHIUl23gdxNnkmjYPgYNsC5JbBi8IMbZ0j8jSyE0XZUEXXiNSXdnZ+0uvhQKBgQDK+qp7zJtzEsRNU7VyX0SayfNJeFaZUPSdIzEeLOLuMo3flT+BaVJ9+qsN7WgqLEGyr0GwQCWzcVqnNeNjoWZCuSa1XMK3867cjSmzr4xSXr3oTJOqeu/yP3V5kE6WaOZD7ZOX9a4251yOzo8Jq3lvQHCZbDTFledNXiBBYE5JHQKBgQC7K6CrxaFOq7iV7W0j4A4EwnyIZgVEnIxblOZGK9ZlZ3Cz1N3yXW4bq9d/yq1E270+wPQaXivjywgM8PnOdukIE2S+GrDfGVc9+p3jrpdosv2oIExAWKYcYXg+WNeHEAzaiFP2IASyEzVguRHi2XlfXVNJa2mvjrkLB4ye91hhQQKBgAWmNFh9uVXaj9wXGFW2kRI9zKee0coylpxI/nT4ithWT9yJKttPMxyOfXRFx0BlOu7eMdXV8zarUPqWZV3AgDgbLxidHE5CWfuJcj+uAxnHyUFN4E4+Xa9AVOStP4KwKLh7REBWdiLRDaJr3U9HlnmiMYATouRc57Mfi4ZGR+eBAoGBAK7zS3mbaYMNVMxnycBmKsyzfyjsYkXZaa5/byv3Q4Op4FDBY9goMnH2TlxVAe+FCPfOB8k5FrwgNhyQzJrONfeIBksLspxPQmaqPLBZN0YG6cwe0U2TV5LYBhFP4HdLsThGnMSIXEUx0psgaFbgsjmNgAyGBlgjIgKOYvtpFGqd";
//    private final static String alipay_public_key ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp40F1REujZz4oIE9lUudNXc1pzi4cc6HR86bW35E4N0bokEC2EcPuwLDgnGN0JkuFRQUPI2Ijd80pQl5vwNwKycY6oXypVhpqPhaNTauotT/FqzZkBE3rklPH8yb6DSfNUhvekHLeVmBzqEpzZUpy/DDSY+3kVROMbgXMSp+qhlmYb18Lgvfo7c00UosY9UvobNwPDs1bVYQJug5rimiSbqdjh8nbeIiU+b/9i+b2R3N+TDvc+3AfWeBuWebxygmNVjk5iPUniuXqlWCQBUihtGba+iAnNBJFQ3EuN0d4TYosvHnTfdhTfXf93KyzkTfz80KOWC9diudTq+3EkUlqwIDAQAB";
//    private final static String app_id = "2017041806789390";
//    private final static String wx_app_id = "wxd6d79c4ca0fef838";//公众号id
//    private final static String wx_mch_id = "1439584802";//商户号
//    private final static String wx_app_secret_key = "41ccde2f22417d9717c4c213f91c3b5c";

    private final static String wx_pay_notify_url = "https://api.laihuipinche.com/wx_pay/notify";//运行环境
    private final static String alipay_notify_url = "https://api.laihuipinche.com/alipay/notify";

   /* //测试通知地址
    private final static String wx_pay_notify_url="http://laihuiapi.cyparty.com/wx_pay/notify";
    private final static String alipay_notify_url="http://laihuiapi.cyparty.com/alipay/notify";*/

    public static String getWx_app_id() {
        return wx_app_id;
    }

    public static String getAlipay_public_key() {
        return alipay_public_key;
    }

    public static String getWx_mch_id() {
        return wx_mch_id;
    }

    public static String getWx_app_secret_key() {
        return wx_app_secret_key;
    }

    public static String getApp_id() {
        return app_id;
    }

    public static String getPrivate_key() {
        return private_key;
    }

    public static String getWx_pay_notify_url() {
        return wx_pay_notify_url;
    }

    public static String getAlipay_notify_url() {
        return alipay_notify_url;
    }
}
