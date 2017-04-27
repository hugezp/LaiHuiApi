package com.cyparty.laihui.utilities;

import com.swetake.util.Qrcode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by lh2 on 2017/4/27.
 */
public class QRCodeUtil {

    static int width = 90;
    static int height = 90;

    public static void create_image(String sms_info, String pathName) throws Exception {
        try {
            Qrcode testQrcode = new Qrcode();
            testQrcode.setQrcodeErrorCorrect('M');
            testQrcode.setQrcodeEncodeMode('B');
            testQrcode.setQrcodeVersion(7);
            String testString = sms_info;
            byte[] d = testString.getBytes("UTF-8");
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
            Graphics2D g = bi.createGraphics();
            g.setBackground(Color.WHITE);
            g.clearRect(0, 0, width, height);
            g.setColor(Color.BLACK);

            // 限制最大字节数为119
            if (d.length > 0 && d.length < 120) {
                boolean[][] s = testQrcode.calQrcode(d);
                for (int i = 0; i < s.length; i++) {
                    for (int j = 0; j < s.length; j++) {
                        if (s[j][i]) {
                            g.fillRect(j * 2, i * 2, 2, 2);
                        }
                    }
                }
            }
            g.dispose();
            bi.flush();
            File f = new File(pathName);
            if (!f.exists()) f.createNewFile();
            ImageIO.write(bi, "png", f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
