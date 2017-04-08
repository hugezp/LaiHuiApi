import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class Test {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        PrivateKey privateKey = readPrivateKey();
        String message = "DmCAAWQO/W2lz6HLoR0LS/sdRrjWC2mbHONuma+zy6yHnKLbqcYhDWQWWqxZHp7AoGsjYGlwUTDi\n" +
                "uXwmZiFP+Stl2Bv/CHNQCCtJocJ6FRjtDun3r7l7kyXopTs3KoQmtccnFFleFJN4y2tPA436zooT\n" +
                "Bfj6f80tL0JkKr4MvNQ=\n";
        byte[] data = encryptedData(message);
        String text = decrypt(privateKey, data);
        System.out.print(text);

    }

    private static String decrypt(PrivateKey privateKey, byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedData = cipher.doFinal(data);

        return new String(decryptedData);
    }

    private static byte[] encryptedData(String base64Text) {
        return Base64.getDecoder().decode(base64Text.getBytes(Charset.forName("UTF-8")));
    }

    private static PrivateKey readPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateKeyData = Files.readAllBytes(
                Paths.get("C:\\Users\\Administrator\\Desktop/pkcs8_private_key.pem"));

        byte[] decodedKeyData = Base64.getDecoder()
                .decode(new String(privateKeyData)
                        .replaceAll("-----\\w+ PRIVATE KEY-----", "")
                        .replace("\n", "")
                        .getBytes());

        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decodedKeyData));
    }
}
