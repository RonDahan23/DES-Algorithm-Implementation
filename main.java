import java.math.BigInteger;
import java.util.*;

public class main {

    public static void main(String[] args) {
        DESImplementation a = new DESImplementation();
        String M = "hiiiiiii";

        a.EncodeEach64BitBlockOfData(M);

        String Key = "test1243";
        // System.out.println(Key.length());

        // a.Encrypt(Key, Text);

        // System.out.println(a.CreateTheKey(Key));

    }

}
