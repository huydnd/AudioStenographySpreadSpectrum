package com.audiostenographyspreadspectrum.spreadspectrum;

import com.audiostenographyspreadspectrum.audio.BinaryTool;
import com.audiostenographyspreadspectrum.audio.AudioDevice;
import com.audiostenographyspreadspectrum.audio.Common;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Encoder {
    private String message;
    private final File originalAudioFile;
    private final long key;

    public ArrayList<Float> getSamples() {
        return samples;
    }

    public void setSamples(ArrayList<Float> samples) {
        this.samples = samples;
    }

    public ArrayList<Float> getSpreadSignal() {
        return spreadSignal;
    }

    public void setSpreadSignal(ArrayList<Float> spreadSignal) {
        this.spreadSignal = spreadSignal;
    }

    private ArrayList<Float> samples;
    private ArrayList<Float> spreadSignal;

    public Encoder(String message,long key, File originalAudioFile) {
        this.message = message.trim();
        if(this.message.length() % 2 != 0){
            this.message += " ";
        }
        this.key = key;
        this.originalAudioFile = originalAudioFile;
    }

    public void encode() throws Exception {

        int[] message_binary = BinaryTool.convertStringToBinary(message);
        System.out.println("message: ");
        for (int k : message_binary) {
            System.out.print(k);
        }
        System.out.println();

        for(int i =0 ; i < message_binary.length;i++){
            if(message_binary[i] == 0){
                message_binary[i] = -1;
            }
            System.out.print(message_binary[i]);
        }
        System.out.println();

        ArrayList<Float> samples = Common.getWaveFromAudio(originalAudioFile);

        System.out.println("wave: ");
        for(float f : samples){
            System.out.print(f + " ");
        }
        System.out.println();

        System.out.println(samples.size());
        System.out.println(Arrays.toString(message_binary));

        int[] pnSequence = Common.pnSequenceKey(key,samples.size());

        System.out.println("pn random from key: ");
        for(int o : pnSequence){
            System.out.print(o);
        }
        System.out.println();

        System.out.println(pnSequence.length);

        int num_per_character = samples.size() / message_binary.length;

        System.out.println("wave size : " + samples.size());
        System.out.println("message size: " + message_binary.length);
        System.out.println("num per char: " + num_per_character);

        ArrayList<Integer> spreadSequences = new ArrayList<Integer>();

        int current_pn = 0;
        for (int data : message_binary) {

            for (int j = 0; j < num_per_character; j++) {
                int spread = data * pnSequence[current_pn];
                current_pn++;
                spreadSequences.add(spread);
            }
        }

        System.out.println("prev spread sequences size: " + spreadSequences.size());

        if(spreadSequences.size() < samples.size()){
            int diff = samples.size() - spreadSequences.size();
            for(int i = 0; i < diff;i++){
                spreadSequences.add(1);
            }
        }
        System.out.println("spread sequences size: " + spreadSequences.size());
//        for(int i = 0; i < spreadSequences.size();i++){
//            System.out.print(spreadSequences.get(i) + " ");
//        }
//        System.out.println();

        // embed
        for(int i =0 ; i < samples.size();i++){
            if(spreadSequences.get(i) == -1){
                samples.set(i,0.000001f);
            }
        }


        System.out.println("sample waves: ");
        for(int i = 0; i < 10;i++){
            System.out.print(samples.get(i) + " ");
        }
        System.out.println();

//        for(int i = 0 ; i < samples.size();i++){
//            if(spreadSequences.get(i) == 0){
//                samples.set(i,0.0f);
//            }
//        }

        this.samples = samples;
        this.spreadSignal = new ArrayList<>();
        for (Integer spreadSequence : spreadSequences) {
            spreadSignal.add((float) spreadSequence);
        }
    }

    public String outputAudio(Stage stage) throws Exception {
        AudioDevice device = new AudioDevice();

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(stage);

        device.writeToFile(this.samples,file);

        return file.getAbsolutePath();
    }
}
