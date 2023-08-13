import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class DESImplementation {

    // ------------------------------"Create The KEY"------------------------------

    public static String[] CreateTheKeyForE(String OriginalKey) {

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

        return K;
    }

    public static String[] CreateTheKeyForD(String OriginalKey) {

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

        String[] ReverseK = new String[K.length];
        int x = 0;
        for (int i = K.length - 1; i >= 0; i--, x++) {
            ReverseK[x] = K[i];
        }

        return ReverseK;
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

    // ------------------------------"Create The Encryption
    // Text"------------------------------

    public static String Encrypt(String M, String Key) {
        String[] allKeys = CreateTheKeyForE(Key);

        M = stringToHex(M);

        String[] BinaryBlocks = hexToBinaryArray(M);
        BinaryBlocks = IpTableImplementation(BinaryBlocks);

        String[] LeftK = ReturnLeftSideArray(BinaryBlocks);
        String[] RightK = ReturnRightSideArray(BinaryBlocks);

        String[] EncryptText = Do16Rounds(LeftK, RightK, allKeys);

        EncryptText = FPTableImplementation(EncryptText);

        String hexOutputs = binaryToHex(EncryptText);

        return hexOutputs;

    }

    public static String stringToHex(String input) {
        StringBuilder hexBuilder = new StringBuilder();
        for (char c : input.toCharArray()) {
            String hex = Integer.toHexString(c);
            if (hex.length() == 1) {
                hexBuilder.append("0"); // Add leading zero if needed
            }
            hexBuilder.append(hex);
        }
        return hexBuilder.toString().toUpperCase();
    }

    public static String[] hexToBinaryArray(String hexString) {
        StringBuilder binaryStringBuilder = new StringBuilder();

        for (char c : hexString.toCharArray()) {
            String binary = Integer.toBinaryString(Character.digit(c, 16));
            binaryStringBuilder.append("0000", 0, 4 - binary.length()).append(binary);
        }

        String binaryString = binaryStringBuilder.toString();

        // Calculate the total length needed for padding
        int totalLength = (binaryString.length() + 63) / 64 * 64;

        // Perform zero-padding to the right
        while (binaryString.length() < totalLength) {
            binaryString += "0";
        }

        // Create the array and populate it with 64-bit binary strings
        int numElements = totalLength / 64;
        String[] binaryArray = new String[numElements];
        for (int i = 0; i < numElements; i++) {
            binaryArray[i] = binaryString.substring(i * 64, (i + 1) * 64);
        }

        return binaryArray;
    }

    public static String[] InsertListToArray(List<String> blocks, String[] BinaryBlocks) {

        for (int i = 0; i < blocks.size(); i++) {
            String block = blocks.get(i);
            String binaryBlock = strTobin(block);
            binaryBlock = binaryBlock.replace(" ", "");
            BinaryBlocks[i] = binaryBlock;
        }
        return BinaryBlocks;

    }

    public static String[] IpTableImplementation(String[] BinaryBlocks) {

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

    public static List<String> performBlockDivision(String M) {
        List<String> blocks = new ArrayList<>();

        // Step 1: Padding
        int paddingSize = 64 - (M.length() % 64);
        if (paddingSize < 64) {
            StringBuilder paddedM = new StringBuilder(M);
            for (int i = 0; i < paddingSize; i++) {
                paddedM.append(' ');
            }
            M = paddedM.toString();
        }

        // Step 2: Block Division
        int numBlocks = M.length() / 64;
        for (int i = 0; i < numBlocks; i++) {
            int startIndex = i * 64;
            int endIndex = (i + 1) * 64;
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

    public static String[] Do16Rounds(String[] LeftK, String[] RightK, String[] allKeys) {

        String LastL = "";
        String LastR = "";

        String[] AllEncText = new String[LeftK.length];

        for (int i = 0; i < LeftK.length || i < RightK.length; i++) {

            LastL = LeftK[i];
            LastR = RightK[i];

            for (int x = 0; x < allKeys.length; x++) {
                String L1 = LastR;
                String R1 = XorFunction(LastL,
                        PTableImplementation(Sbox(XorFunction(ETableImplementation(LastR), allKeys[x]))));

                // Update LastL and LastR for the next iteration
                LastL = L1;
                LastR = R1;

            }
            AllEncText[i] = (LastL + LastR);

        }

        for (int i = 0; i < AllEncText.length; i++) {

            String RotateR = AllEncText[i].substring(0, 32);
            String RotateL = AllEncText[i].substring(32, 64);
            AllEncText[i] = RotateL + RotateR;

        }

        return AllEncText;

    }

    public static String ETableImplementation(String R) {

        String E = "";

        int[] E_TABLE = {
                32, 1, 2, 3, 4, 5,
                4, 5, 6, 7, 8, 9,
                8, 9, 10, 11, 12, 13,
                12, 13, 14, 15, 16, 17,
                16, 17, 18, 19, 20, 21,
                20, 21, 22, 23, 24, 25,
                24, 25, 26, 27, 28, 29,
                28, 29, 30, 31, 32, 1 };

        for (int i : E_TABLE) {
            E += R.charAt(i - 1);
        }

        return E;
    }

    public static String XorFunction(String E, String K) {
        String ans = "";

        for (int i = 0; i < E.length() || i < K.length(); i++) // xor between K and E
        {
            // If the Character matches
            if (E.charAt(i) == K.charAt(i))
                ans += "0";
            else
                ans += "1";
        }

        return ans;

    }

    public static String Sbox(String XorAns) {

        int[][] S1 = {
                { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7 },
                { 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8 },
                { 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0 },
                { 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 } };

        int[][] S2 = {
                { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10 },
                { 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5 },
                { 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15 },
                { 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 } };

        int[][] S3 = {
                { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8 },
                { 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1 },
                { 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7 },
                { 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 } };

        int[][] S4 = {
                { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15 },
                { 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9 },
                { 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4 },
                { 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 } };

        int[][] S5 = {
                { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9 },
                { 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6 },
                { 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14 },
                { 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 } };

        int[][] S6 = {
                { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11 },
                { 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8 },
                { 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6 },
                { 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 } };

        int[][] S7 = {
                { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1 },
                { 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6 },
                { 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2 },
                { 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 } };

        int[][] S8 = {
                { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7 },
                { 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2 },
                { 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8 },
                { 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } };

        String RB1 = XorAns.substring(0, 6);
        String row1 = String.valueOf(RB1.substring(0, 1) + RB1.substring(5, 6));
        String col1 = String.valueOf(RB1.substring(1, 5));
        int target = S1[Integer.parseInt(row1, 2)][Integer.parseInt(col1, 2)];
        String binaryTarget = String.format("%4s", Integer.toBinaryString(target)).replace(' ', '0');

        String RB2 = XorAns.substring(6, 12);
        row1 = String.valueOf(RB2.substring(0, 1) + RB2.substring(5, 6));
        col1 = String.valueOf(RB2.substring(1, 5));
        target = S2[Integer.parseInt(row1, 2)][Integer.parseInt(col1, 2)];
        binaryTarget += String.format("%4s", Integer.toBinaryString(target)).replace(' ', '0');

        String RB3 = XorAns.substring(12, 18);
        row1 = String.valueOf(RB3.substring(0, 1) + RB3.substring(5, 6));
        col1 = String.valueOf(RB3.substring(1, 5));
        target = S3[Integer.parseInt(row1, 2)][Integer.parseInt(col1, 2)];
        binaryTarget += String.format("%4s", Integer.toBinaryString(target)).replace(' ', '0');

        String RB4 = XorAns.substring(18, 24);
        row1 = String.valueOf(RB4.substring(0, 1) + RB4.substring(5, 6));
        col1 = String.valueOf(RB4.substring(1, 5));
        target = S4[Integer.parseInt(row1, 2)][Integer.parseInt(col1, 2)];
        binaryTarget += String.format("%4s", Integer.toBinaryString(target)).replace(' ', '0');

        String RB5 = XorAns.substring(24, 30);
        row1 = String.valueOf(RB5.substring(0, 1) + RB5.substring(5, 6));
        col1 = String.valueOf(RB5.substring(1, 5));
        target = S5[Integer.parseInt(row1, 2)][Integer.parseInt(col1, 2)];
        binaryTarget += String.format("%4s", Integer.toBinaryString(target)).replace(' ', '0');

        String RB6 = XorAns.substring(30, 36);
        row1 = String.valueOf(RB6.substring(0, 1) + RB6.substring(5, 6));
        col1 = String.valueOf(RB6.substring(1, 5));
        target = S6[Integer.parseInt(row1, 2)][Integer.parseInt(col1, 2)];
        binaryTarget += String.format("%4s", Integer.toBinaryString(target)).replace(' ', '0');

        String RB7 = XorAns.substring(36, 42);
        row1 = String.valueOf(RB7.substring(0, 1) + RB7.substring(5, 6));
        col1 = String.valueOf(RB7.substring(1, 5));
        target = S7[Integer.parseInt(row1, 2)][Integer.parseInt(col1, 2)];
        binaryTarget += String.format("%4s", Integer.toBinaryString(target)).replace(' ', '0');

        String RB8 = XorAns.substring(42, 48);
        row1 = String.valueOf(RB8.substring(0, 1) + RB8.substring(5, 6));
        col1 = String.valueOf(RB8.substring(1, 5));
        target = S8[Integer.parseInt(row1, 2)][Integer.parseInt(col1, 2)];
        binaryTarget += String.format("%4s", Integer.toBinaryString(target)).replace(' ', '0');

        return binaryTarget;
    }

    public static String PTableImplementation(String StringFromSbox) {
        String P = "";

        int[] PTable = { 16, 7, 20, 21,
                29, 12, 28, 17,
                1, 15, 23, 26,
                5, 18, 31, 10,
                2, 8, 24, 14,
                32, 27, 3, 9,
                19, 13, 30, 6,
                22, 11, 4, 25 };

        for (int i : PTable) {
            P += StringFromSbox.charAt(i - 1);
        }

        return P;

    }

    public static String[] FPTableImplementation(String[] EncryptText) {

        int[] FPTable = {
                40, 8, 48, 16, 56, 24, 64, 32,
                39, 7, 47, 15, 55, 23, 63, 31,
                38, 6, 46, 14, 54, 22, 62, 30,
                37, 5, 45, 13, 53, 21, 61, 29,
                36, 4, 44, 12, 52, 20, 60, 28,
                35, 3, 43, 11, 51, 19, 59, 27,
                34, 2, 42, 10, 50, 18, 58, 26,
                33, 1, 41, 9, 49, 17, 57, 25 };

        for (int i = 0; i < EncryptText.length; i++) {

            String FP = "";

            for (int x : FPTable) {
                FP += EncryptText[i].charAt(x - 1);
            }

            EncryptText[i] = FP;
        }

        return EncryptText;

    }

    public static String binaryToHex(String[] binaryNumber) {

        String CyperText = "";
        for (int i = 0; i < binaryNumber.length; i++) {
            BigInteger decimalNumber = new BigInteger(binaryNumber[i], 2); // Convert binary to decimal using BigInteger
            String hexNumber = decimalNumber.toString(16); // Convert decimal to hexadecimal

            binaryNumber[i] = hexNumber.toUpperCase();

        }
        for (int i = 0; i < binaryNumber.length; i++) {
            CyperText += binaryNumber[i];

        }

        return CyperText;
    }

    public static String binaryArrayToText(String[] binaryDataArray) {
        String Result = "";
        String[] textArray = new String[binaryDataArray.length];

        for (int j = 0; j < binaryDataArray.length; j++) {
            String binaryData = binaryDataArray[j];
            StringBuilder text = new StringBuilder();
            int length = binaryData.length();

            for (int i = 0; i < length; i += 8) {
                String chunk = binaryData.substring(i, Math.min(length, i + 8));
                int decimalValue = Integer.parseInt(chunk, 2);
                char character = (char) decimalValue;
                text.append(character);
            }

            textArray[j] = text.toString();
        }
        for (int i = 0; i < textArray.length; i++) {
            Result += textArray[i];

        }

        return Result;
    }
    // ------------------------"Create The Encryption
    // Text"---------------------------

    // ---------------------------------"Decryption"---------------------------------

    public static String Decrypt(String M, String Key) {

        String[] ReverseAllKeys = CreateTheKeyForD(Key);

        String[] BinaryBlocks = hexToBinaryArray(M);

        BinaryBlocks = IpTableImplementation(BinaryBlocks);

        String[] LeftK = ReturnLeftSideArray(BinaryBlocks);
        String[] RightK = ReturnRightSideArray(BinaryBlocks);

        String[] DecryptText = Do16Rounds(LeftK, RightK, ReverseAllKeys);

        DecryptText = FPTableImplementation(DecryptText);

        String Output = binaryArrayToText(DecryptText);

        return Output;

    }

    // ------------------------------"Decryption"------------------------------

}
