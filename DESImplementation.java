import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DESImplementation {

    // ------------------------------"Create The KEY"------------------------------

    public static String[] CreateTheKey(String OriginalKey) // main
    {
        

        if (OriginalKey.length() != 8) {
            System.out.println("Error: Key length is not 64 bits");
            return null; // or return an appropriate value or handle the error case
        }

        String Key = strTobin(OriginalKey);

        Key = Key.replace(" ", "");
        
        String permutedKey = generatePermutedKey56(Key);
        String C0 = permutedKey.substring(0, 28);
        String D0 = permutedKey.substring(28, 56);

        String[] K = Do16LeftShift(C0, D0);
        K = generatePermutedKey48(K);
        printer(K);

        return K;
    }

    /* Convert string to binary string */
    public static String strTobin(String str) {

        byte[] bytes = str.getBytes();
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            int val = b;
            for (int i = 0; i < 8; i++) {
                binary.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
            binary.append(' ');
        }
        return binary.toString();
    }

    public static String generatePermutedKey56(String binaryKey) {

        String permutatedKey = "";
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

        for (int i : pc1Table) {
            permutatedKey += binaryKey.charAt(i - 1);
        }

        return permutatedKey;
    }

    public static int getLeftShifts(int iteration) {
        int[] shifts = { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 };

        return shifts[iteration % 16];
    }

    public static String leftShift(String input, int shiftCount) {

        shiftCount %= input.length();
        String rotated = input.substring(shiftCount) + input.substring(0, shiftCount);
        return rotated;
    }

    public static String[] Do16LeftShift(String C0, String D0) {
        String[] C = new String[17];
        String[] D = new String[17];
        String[] K = new String[16];
        int x = 0;
        C[0] = C0;
        D[0] = D0;

        for (int i = 1; i <= 16; i++, x++) {

            int leftShifts = getLeftShifts(x);
            C[i] = leftShift(C[i - 1], leftShifts);
            D[i] = leftShift(D[i - 1], leftShifts);

            K[i - 1] = C[i] + D[i];
        }

        return K;
    }

    public static String[] generatePermutedKey48(String[] K) {
        Integer[] PC2 = {
                14, 17, 11, 24, 1, 5, 3, 28,
                15, 6, 21, 10, 23, 19, 12, 4,
                26, 8, 16, 7, 27, 20, 13, 2,
                41, 52, 31, 37, 47, 55, 30, 40,
                51, 45, 33, 48, 44, 49, 39, 56,
                34, 53, 46, 42, 50, 36, 29, 32 };

        for (int i = 0; i < K.length; i++) {

            StringBuilder permutedKeyBuilder = new StringBuilder();

            for (int x = 0; x < PC2.length; x++) {
                int pc2Index = PC2[x] - 1;
                permutedKeyBuilder.append(K[i].charAt(pc2Index));
            }
            K[i] = permutedKeyBuilder.toString();

        }
        return K;
    }

    // ------------------------------"Create The KEY"------------------------------

    // ------------------------------"Create The M"------------------------------

    public static void EncodeEach64BitBlockOfData(String M) // main
    {

        List<String> blocks = performBlockDivision(M);
        String[] BinaryBlocks = new String[blocks.size()];

        BinaryBlocks = InsertListToArray(blocks, BinaryBlocks);
        BinaryBlocks = IpTableImplementation(BinaryBlocks);

        String[] LeftK = ReturnLeftSideArray(BinaryBlocks);
        String[] RightK = ReturnRightSideArray(BinaryBlocks);

        printer(LeftK);
        System.out.println();
        System.out.println();
        printer(RightK);

    }

    public static String[] InsertListToArray(List<String> blocks, String[] BinaryBlocks) {

        for (int i = 0; i < blocks.size(); i++) {
            String block = blocks.get(i);
            String binaryBlock = convertToBinary(block);
            BinaryBlocks[i] = binaryBlock;
        }
        return BinaryBlocks;

    }

    public static String[] IpTableImplementation(String[] BinaryBlocks) { // good morning ron, start here!

        Integer[] IPTable = {
                58, 50, 42, 34, 26, 18, 10, 2,
                60, 52, 44, 36, 28, 20, 12, 4,
                62, 54, 46, 38, 30, 22, 14, 6,
                64, 56, 48, 40, 32, 24, 16, 8,
                57, 49, 41, 33, 25, 17, 9, 1,
                59, 51, 43, 35, 27, 19, 11, 3,
                61, 53, 45, 37, 29, 21, 13, 5,
                63, 55, 47, 39, 31, 23, 15, 7 };

        for (int i = 0; i < BinaryBlocks.length; i++) {
            StringBuilder permutedKeyBuilder = new StringBuilder();
            for (int x = 0; x < IPTable.length; x++) {
                int IPTableIndex = IPTable[x] - 1;
                permutedKeyBuilder.append(BinaryBlocks[i].charAt(IPTableIndex));
            }
            BinaryBlocks[i] = permutedKeyBuilder.toString();

        }

        return BinaryBlocks;

    }

    public static String convertToBinary(String input) {
        StringBuilder binaryBuilder = new StringBuilder();
        for (char c : input.toCharArray()) {
            String binaryChar = String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0');
            binaryBuilder.append(binaryChar);
        }
        return binaryBuilder.toString().trim();
    }

    public static List<String> performBlockDivision(String M) {
        List<String> blocks = new ArrayList<>();

        // Step 1: Padding
        int paddingSize = 8 - (M.length() % 8);
        if (paddingSize < 8) {
            StringBuilder paddedM = new StringBuilder(M);
            for (int i = 0; i < paddingSize; i++) {
                paddedM.append(' ');
            }
            M = paddedM.toString();
        }

        // Step 2: Block Division
        int numBlocks = M.length() / 8;
        for (int i = 0; i < numBlocks; i++) {
            int startIndex = i * 8;
            int endIndex = (i + 1) * 8;
            String block = M.substring(startIndex, endIndex);
            blocks.add(block);
        }

        return blocks;
    }

    public static String[] ReturnLeftSideArray(String[] BinaryBlocks) {
        String[] LeftK = new String[BinaryBlocks.length];
        ;
        for (int i = 0; i < BinaryBlocks.length; i++) {
            LeftK[i] = BinaryBlocks[i].substring(0, 32);

        }
        return LeftK;
    }

    public static String[] ReturnRightSideArray(String[] BinaryBlocks) {
        String[] RightK = new String[BinaryBlocks.length];
        ;
        for (int i = 0; i < BinaryBlocks.length; i++) {
            RightK[i] = BinaryBlocks[i].substring(32, 64);

        }
        return RightK;
    }
    // ------------------------------"Create The M"------------------------------

    public static void printer(String a[]) {
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + ",");
        }

    }

}
