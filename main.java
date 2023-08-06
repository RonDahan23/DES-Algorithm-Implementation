
public class main {

    public static void main(String[] args) {

        DESImplementation a = new DESImplementation();
        String M = "Test123!";
        String Key = "RonTest1";

        a.Encrypt(M, Key);

        a.Decrypt("B2ED8F68E5B2C5EE", Key);

        System.out.println();

    }

}
