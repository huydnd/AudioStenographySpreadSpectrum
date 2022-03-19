package com.audiostenographyspreadspectrum.audio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class BinaryTool {
    public static final int NUMBER_OF_BITS = 8;

    public BinaryTool() {
    }

    public static Binary charToBinary(char toConvert) {
        StringBuilder stringRep = new StringBuilder(Integer.toBinaryString(toConvert));

        while(stringRep.length() < 8) {
            stringRep.insert(0, "0");
        }

        return new Binary(stringRep.toString());
    }

    public static Binary ASCIIToBinary(String toConvert) {
        if (toConvert.length() == 0) {
            return null;
        } else {
            char[] composite_chars = toConvert.toCharArray();
            Binary converted = charToBinary(composite_chars[0]);

            for(int i = 1; i < composite_chars.length; ++i) {
                converted.append(charToBinary(composite_chars[i]));
            }

            return converted;
        }
    }

    public static String binaryToASCII(Binary toConvert) {
        int[] binaryData = toConvert.getIntArray();
        StringBuilder stringRep = new StringBuilder(binaryData.length / 8);

        for(int i = 0; i < binaryData.length; i += 8) {
            Binary charBinary = new Binary(Arrays.copyOfRange(binaryData, i, i + 8));
            String character = Character.toString(binaryToChar(charBinary));
            System.out.println("cha: " + character);
            stringRep.append(character);
        }

        return stringRep.toString();
    }

    public static char binaryToChar(Binary toConvert) {
        String binaryData = toConvert.getStringRep();
        return (char)Integer.parseInt(binaryData, 2);
    }

    public static Binary fileToBinary(File file) throws IOException {
        Path path = Paths.get(file.getPath());
        byte[] bytes = Files.readAllBytes(path);
        StringBuilder sb = new StringBuilder();

        for (byte by : bytes) {
            sb.append(Integer.toBinaryString(255 & by | 256).substring(1));
        }

        return new Binary(sb.toString());
    }

    public static void writeToFile(Binary toWrite, String outPath) throws IOException {
        Path path = Paths.get(outPath);
        byte[] out = toWrite.getByteArray();
        Files.write(path, out);
    }

    public static Byte[] toObjects(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];
        int i = 0;

        for (byte b : bytesPrim) {
            bytes[i++] = b;
        }

        return bytes;
    }

    public static byte[] toPrimitives(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];

        for(int i = 0; i < oBytes.length; ++i) {
            bytes[i] = oBytes[i];
        }

        return bytes;
    }

    public static int[] convertStringToBinary(String input) {
        boolean c = false;
        StringBuilder str = new StringBuilder();
        char[] chars = input.toCharArray();

        for (char aChar : chars) {
            String s = String.format("%8s", Integer.toBinaryString(aChar)).replaceAll(" ", "0");
            str.append(s);
        }

        int[] result = new int[str.length()];

        for(int i = 0; i < result.length; ++i) {
            result[i] = Integer.parseInt(String.valueOf(str.charAt(i)));
        }

        return result;
    }

    public static String binaryToString(int[] binaryInput) {

        StringBuilder inputBuilder = new StringBuilder();
        for(int i = 0; i < binaryInput.length; ++i) {
            if (i % 8 == 0 && i != 0) {
                inputBuilder.append(" ");
            } else {
                inputBuilder.append(binaryInput[i]);
            }
        }
        String input = inputBuilder.toString();

        System.out.println("input: {" + input + "}");
        StringBuilder str = new StringBuilder();
        String[] binaryStr = input.split(" ");

        for (String s : binaryStr) {
            int charCode = Integer.parseInt(s, 2);
            String character = Character.toString((char) charCode);
            str.append(character);
        }

        return str.toString();
    }

    public static void main(String[] args) throws IOException {
        int[] binary = convertStringToBinary("aiz shit chet tiet cais ");
        System.out.println(binaryToString(binary));
    }
}
