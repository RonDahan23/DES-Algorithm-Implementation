import java.util.Hashtable;

public class DESImplementation {

    public static Void Encrypt(String originalKey) {
        String M = hexToBinary(originalKey);
        System.out.println(generatePermutedKey(M));

        return null;

    }

    public static String hexToBinary(String hex) {
        StringBuilder binaryBuilder = new StringBuilder();
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            String binary = Integer.toBinaryString(Character.digit(c, 16));
            binary = String.format("%4s", binary).replace(' ', '0');
            binaryBuilder.append(binary);
        }
        return binaryBuilder.toString();
    }

    public static String generatePermutedKey(String originalKey) {
        int[] pc1Table = {
                57, 49, 41, 33, 25, 17, 9,
                1, 58, 50, 42, 34, 26, 18,
                10, 2, 59, 51, 43, 35, 27,
                19, 11, 3, 60, 52, 44, 36,
                63, 55, 47, 39, 31, 23, 15,
                7, 62, 54, 46, 38, 30, 22,
                14, 6, 61, 53, 45, 37, 29,
                21, 13, 5, 28, 20, 12, 4
        };

        StringBuilder permutedKeyBuilder = new StringBuilder();
        for (int i = 0; i < pc1Table.length; i++) {
            int pc1Index = pc1Table[i] - 1;
            permutedKeyBuilder.append(originalKey.charAt(pc1Index));
        }

        return permutedKeyBuilder.toString();
    }

}