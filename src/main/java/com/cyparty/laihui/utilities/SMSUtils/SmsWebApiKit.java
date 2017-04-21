package com.cyparty.laihui.utilities.SMSUtils;




/**
 * 服务端发起验证请求验证移动端(手机)发送的短信
 * @author Administrator
 *
 */
public class SmsWebApiKit {
	//Android
	//private static String appkey ="1d31fe189149b" ;
	//Ios
	private static String appkey ="1cccf2e4d10d0" ;
	
//	public SmsWebApiKit(String appkey) {
//		super();
//		this.appkey = appkey;
//	}
	private SmsWebApiKit() {}
	private static SmsWebApiKit smsWebApiKit=null;
	//静态工厂方法
	public static SmsWebApiKit getInstance() {
		if (smsWebApiKit == null) {
			smsWebApiKit = new SmsWebApiKit();
		}
		return smsWebApiKit;
	}
	/**
	 * 服务端发起发送短信请求
	 * @return
	 * @throws Exception
	 */
	public  String sendMsg(String phone,String zone) throws Exception{
		
		String address = "https://xxxx";
		MobClient client = null;
		try {
			client = new MobClient(address);
			client.addParam("appkey", appkey).addParam("phone", phone)
					.addParam("zone", zone);
			client.addRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			client.addRequestProperty("Accept", "application/json");
			String result = client.post();
			return result;
		} finally {
			client.release();
		}
	}
	
	/**
	 * 服务端发验证服务端发送的短信
	 * @return
	 * @throws Exception
	 */
	public static String checkcode(String phone,String zone,String code) throws Exception{
		
		String address = "https://webapi.sms.mob.com/sms/verify";
		MobClient client = null;
		try {
			client = new MobClient(address);
			client.addParam("appkey", appkey).addParam("phone", phone)
					.addParam("zone", zone).addParam("code", code);
			//client.addRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			client.addRequestProperty("Accept", "application/json");
			String result = client.post().substring(10,13);

			return result;
		} finally {
			client.release();
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println(SmsWebApiKit.checkcode("15738961936","86","2197"));
		
	}
	
	
	

}
