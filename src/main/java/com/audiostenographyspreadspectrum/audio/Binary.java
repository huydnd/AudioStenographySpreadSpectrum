package com.audiostenographyspreadspectrum.audio;

public class Binary {
    private int[] contents;

    public Binary(int[] contents) {
        this.contents = contents;
    }

    public Binary(String stringRep) {
        this.contents = new int[stringRep.length()];

        for(int i = 0; i < stringRep.length(); ++i) {
            this.contents[i] = Integer.parseInt(stringRep.substring(i, i + 1));
        }

    }

    public void append(Binary toAppend) {
        int[] appended = new int[this.contents.length + toAppend.contents.length];
        System.arraycopy(this.contents, 0, appended, 0, this.contents.length);
        System.arraycopy(toAppend.contents, 0, appended, this.contents.length, toAppend.contents.length);
        this.contents = appended;
    }

    public int[] getIntArray() {
        return this.contents;
    }

    public byte[] getByteArray() {
        String stringRep = this.getStringRep();
        byte[] bytes = new byte[stringRep.length() / 8];

        for(int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte)Integer.parseInt(stringRep.substring(i * 8, (i + 1) * 8), 2);
        }

        return bytes;
    }

    public String getStringRep() {
        StringBuilder stringRep = new StringBuilder();

        for (int content : this.contents) {
            stringRep.append(content);
        }

        return stringRep.toString();
    }

    public String toString() {
        return this.getStringRep();
    }

    public int length() {
        return this.getStringRep().length();
    }
}
