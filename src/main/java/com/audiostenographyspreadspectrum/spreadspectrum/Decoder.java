package com.audiostenographyspreadspectrum.spreadspectrum;

import com.audiostenographyspreadspectrum.audio.BinaryTool;
import com.audiostenographyspreadspectrum.audio.Common;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class Decoder {
    private final File encodeAudioFile;
    private final long key;
    private String decodedMessage;

    public String getDecodedMessage() {
        return this.decodedMessage;
    }

    public void setDecodedMessage(String decodedMessage) {
        this.decodedMessage = decodedMessage;
    }

    public Decoder(File encodeAudioFile, long key) {
        this.encodeAudioFile = encodeAudioFile;
        this.key = key;
    }

    public void decode() throws Exception {
        ArrayList<Float> samples = Common.getWaveFromAudio(this.encodeAudioFile);
        int[] pnSequence = Common.pnSequenceKey(this.key, samples.size());
        System.out.println(pnSequence.length);
        float lowest_value = 0.0F;
        Map<Float, Integer> map = new Hashtable<>();

        int i;
        for(i = 0; i < samples.size(); ++i) {
            if (map.containsKey(samples.get(i))) {
                map.put(samples.get(i), (Integer)map.get(samples.get(i)) + 1);
                if ((Integer)map.get(samples.get(i)) > 10) {
                    lowest_value = (Float)samples.get(i);
                    break;
                }
            } else {
                map.put(samples.get(i), 1);
            }
        }

        System.out.println("low: " + lowest_value);
        System.out.println("decode wave: ");

        for(i = 0; i < 10; ++i) {
            System.out.print(samples.get(i) + " ");
        }

        System.out.println();
        int[] spreadSequences = new int[samples.size()];

        for(i = 0; i < spreadSequences.length; ++i) {
            spreadSequences[i] = (Float)samples.get(i) == lowest_value ? -1 : 1;
        }

        int[] data = new int[samples.size()];

        int first_char;
        for(first_char = 0; first_char < spreadSequences.length; ++first_char) {
            data[first_char] = spreadSequences[first_char] * pnSequence[first_char];
            System.out.print(data[first_char] + " ");
        }

        System.out.println();
        first_char = data[0];
        int number_per_byte = 0;

        for(i = 0; i < data.length && data[i] == first_char; ++i) {
            ++number_per_byte;
        }

        System.out.println("message count: " + samples.size() / number_per_byte);
        System.out.println("number per pyte: " + number_per_byte);
        int[] message_byte = new int[data.length / number_per_byte];
        int m = 0;

        try {
            for(i = 0; i < data.length; ++i) {
                if (i % number_per_byte == 0) {
                    message_byte[m] = data[i] == -1 ? 0 : 1;
                    System.out.print(message_byte[m]);
                    ++m;
                }
            }
        } catch (Exception ignored) {
        }

        this.decodedMessage = BinaryTool.binaryToString(message_byte);
        System.out.println("decoded message: " + this.decodedMessage);
    }
}
