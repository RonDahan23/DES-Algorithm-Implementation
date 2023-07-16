import java.math.BigInteger;
import java.util.*;

public class main {

    public static void main(String[] args) {
        DESImplementation a = new DESImplementation();
        String M = "hi love you";

        a.EncodeEach64BitBlockOfData(M);

        String Key = "0123456789ABCDEF";

        // a.Encrypt(Key, Text);
    }

}
