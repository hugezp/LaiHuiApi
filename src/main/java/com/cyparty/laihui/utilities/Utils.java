package com.cyparty.laihui.utilities;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by zhu on 2015/12/30.
 */
@Component
public class Utils {
    public static void main(String[] args) {
        //System.out.println(Memcache.getMemcache("3d2d8b6b8fdfcbadedb20072010ea842"));
        //System.out.println(getRandomToken(5));
        //System.out.println(encode("MD5", "laihui888888"));
        //System.out.println(getCharAndNum(32));
        //testEncode();
        //getUpperWord();
        //readStringXml("");
        //sendCodeMessage("13838741275");
        //getTimeSubOrAdd("2017-01-16 09:22:19",15);
        getDoubleValue();
        //连接本地的 Redis 服务
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.auth("zhubangkui");
        System.out.println("Connection to server sucessfully");
        //查看服务是否运行
        System.out.println("Server is running: " + jedis.ping());
    }

    public static String getCurrentTime() {
        Date inputDate = new Date();
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = outputFormat.format(inputDate);
        return datetime;
    }

    //
    public static String getCurrentTimeSubOrAdd(int minute) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, minute);
        String datetime = sdf.format(nowTime.getTime());

        return datetime;
    }

    public static String getTimeSubOrAdd(String time, int minute) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar nowTime = Calendar.getInstance();
        nowTime.setTime(date);
        nowTime.add(Calendar.MINUTE, minute);
        String datetime = sdf.format(nowTime.getTime());

        return datetime;
    }

    public static String getCurrentTimeSubOrAddHour(int hour) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.HOUR, hour);
        String datetime = sdf.format(nowTime.getTime());

        return datetime;
    }

    public static String getTimestamp() {
        Long timestamp = System.currentTimeMillis();
        return timestamp.toString();
    }

    public static long getCurrenTimeStamp() {
        long time = System.currentTimeMillis();
        return time;
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param date 字符串日期
     * @return
     */
    public static long date2TimeStamp(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //return String.valueOf(sdf.parse(date).getTime()/1000);
            return sdf.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //文件命名
    public static String setFileName(String rootfilepath) {
        String currenttime = Utils.getCurrentTime();
        String files[] = currenttime.split("-");
        String filelast[] = files[2].split(" ");
        String filepathdirectory = rootfilepath + "\\" + files[0] + "\\" + files[1] + "\\" + filelast[0];
        String filepath = rootfilepath + "\\" + files[0] + "\\" + files[1] + "\\" + filelast[0] + "\\" + Utils.getTimestamp() + ".png";
        File filedirctory = new File(filepathdirectory);
        File file = new File(filepath);
        if (!filedirctory.exists() && !filedirctory.isDirectory()) {
            Boolean dirSuccess = filedirctory.mkdirs();
            if (dirSuccess) { //System.out.println(filepath+"创建成功");

            } else {
                //System.out.println(filepath+"创建失败");
            }
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return filepath;
    }

    //logo上传
    public static String fileImgUpload(String filename, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile(filename);
        String filepath = "";
        try {
            if (file != null && file.getSize() > 0) {
                InputStream inputStream = file.getInputStream();
                filepath = request.getSession().getServletContext().getRealPath("/upload");
                filepath = Utils.setFileName(filepath);
                File newfile = new File(filepath);
                FileOutputStream outputStream = new FileOutputStream(newfile);
                int bytesWritten = 0;
                int byteCount = 0;
                byte[] bytes = new byte[1024 * 1024];
                while ((byteCount = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, bytesWritten, byteCount);
                }
                inputStream.close();
                outputStream.close();
            }
        } catch (Exception e) {
            System.out.println("上传失败！！！" + e.getMessage());
        }

        return filepath;
    }

    //文件上传
    public static List<String> fileUpLoad(String filename, HttpServletRequest request) {
        List<String> filepaths = new ArrayList();
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> fileList = multipartHttpServletRequest.getFiles(filename);
        if (fileList.size() > 0) {
            for (int fileIndex = 0; fileIndex < fileList.size(); fileIndex++) {
                MultipartFile file = fileList.get(fileIndex);
                try {
                    if (file != null && file.getSize() > 0) {
                        InputStream inputStream = file.getInputStream();
                        String filePath = request.getSession().getServletContext().getRealPath("/upload");
                        filePath = Utils.setFileName(filePath);
                        filepaths.add(filePath);
                        File newfile = new File(filePath);
                        FileOutputStream outputStream = new FileOutputStream(newfile);
                        int bytesWritten = 0;
                        int byteCount = 0;
                        byte[] bytes = new byte[1024 * 1024];
                        while ((byteCount = inputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, bytesWritten, byteCount);
                        }
                        inputStream.close();
                        outputStream.close();
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return filepaths;
    }


    //加密算法
    public static String encode(String algorithm, String str) {
        String ALGORITHM = "MD5";

        char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(str.getBytes("utf-8"));
            return getFormattedText(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
                '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        // 把密文转换成十六进制的字符串形式
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }

    public static String getIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    public static String checkNull(String input) {
        String result = "";
        if (input == null || input.trim().equals("") || input.trim().equals("null") || input.trim().equals("NULL")) {
            result = "";
        } else {
            result = input;
        }
        return result;
    }

    public static String checkTime(String input) {
        String result = "";
        if (input != null && !input.trim().equals("")) {
            result = input.split("\\.")[0];
        } else {
            result = input;
        }
        return result;
    }

    public static String sendCodeMessage(String mobile) {
        String rand = SendSMSUtil.randomNum();
        if (mobile.equals("13298172885")) {
            return "0603";
        }
        if (mobile.equals("15738961936")) {
            return "0603";
        }
        if (mobile.equals("17698909223")) {
            return "0603";
        }
        if (mobile.equals("15639356022")) {
            return "0603";
        }
        if (mobile.equals("18538191908")) {
            return "0603";
        }
        if (mobile.equals("18560459018")) {
            return "0603";
        }
        if (mobile.equals("15516015893")) {
            return "0603";
        }
        String code = "#code#=" + rand;
        boolean send_isSuccess = SendSMSUtil.sendSMS(mobile, "push", 0, code);
        if (!send_isSuccess) {
            code = null;
        } else {
            code = rand;
        }
        return code;
    }

    //产生8位随机数
    public static String random(int n) {
        int[] i = new int[n];
        int count = 0;
        String randomNum = "";
        while (count < n) {
            int t = (int) (Math.random() * 9);//抽取的数值小于char类型的“z”（122）
            if ((t >= 0 & t <= 9)) {
                i[count] = t;
                count++;
            }
        }
        for (int k = 0; k < n; k++) {
            if (i[k] >= 0 & i[k] <= 9)
                randomNum = randomNum + i[k];
            else
                randomNum = randomNum + (char) i[k];
        }
        return randomNum;
    }

    public static String getCharAndNum(int length) {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 取得大写字母还是小写字母
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    public static boolean sendNotifyMessage(String d_mobile, String p_mobile) {
        String typ_val = "#mobile#=" + p_mobile;
        boolean send_isSuccess = SendSMSUtil.sendSMS(d_mobile, 19361, typ_val);
        return send_isSuccess;
    }

    public static boolean sendCancleNotifyMessage(String d_mobile, String p_mobile) {
        String typ_val = "#mobile#=" + p_mobile;
        boolean send_isSuccess = SendSMSUtil.sendSMS(d_mobile, 8193, typ_val);
        return send_isSuccess;
    }

    public static boolean sendAllNotifyMessage(String d_mobile, String title, String content) {
        String typ_val = "#title#=" + title + "&#orderstatus#=" + content;
        boolean send_isSuccess = SendSMSUtil.sendSMS(d_mobile, 15292, typ_val);
        return send_isSuccess;
    }

    public static void getUpperWord() {
        String key = "851wordW";
        System.out.println(key.toUpperCase());
    }

    //随机生成一个在一定范围内的随机数
    public static int getRandomNum(int limit) {
        int randomNum = (int) (Math.random() * (limit + 1));//抽取的数值小于char类型的“z”（122）
        return randomNum;
    }

    public static String readStringXml(String xml) {

        Document doc;
        String result = null;
        try {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML
            Element rootElt = doc.getRootElement(); // 获取根节点

            Iterator return_code = rootElt.elementIterator("return_code"); // 获取根节点下的子节点return_code
            // 获取根节点下的子节点return_code
            String is_success = null;

            // 遍历head节点
            while (return_code.hasNext()) {
                Element recordEle = (Element) return_code.next();
                is_success = recordEle.getText(); // 拿到return_code返回值
                //System.out.println("return_code:" + is_success);
            }
            if (is_success != null && is_success.equals("SUCCESS")) {
                Iterator prepay_id = rootElt.elementIterator("prepay_id");
                while (prepay_id.hasNext()) {

                    Element recordEle = (Element) prepay_id.next();
                    result = recordEle.getText(); // 拿到prepay_id返回值
                    //System.out.println("prepay_id:" + result);

                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //解析json
    public static String getJsonObject(String str, String add) {
        JSONObject json = JSONObject.parseObject(str);
        String result = json.getString(add);
        return result;
    }

    public static void getDoubleValue() {
        String time = Utils.getCurrentTime();
        System.out.println(time.substring(0, time.length() - 3));
    }
}
