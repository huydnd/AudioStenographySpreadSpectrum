package com.audiostenographyspreadspectrum.audio;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class AudioDevice {
    private static final int BUFFER_SIZE = 1024;
    private final SourceDataLine out;
    private final byte[] buffer = new byte[2048];
    private ArrayList<Byte[]> buffer_list = new ArrayList<>();

    public SourceDataLine getOut() {
        return this.out;
    }

    public ArrayList<Byte[]> getBuffer_list() {
        return this.buffer_list;
    }

    public void setBuffer_list(ArrayList<Byte[]> buffer_list) {
        this.buffer_list = buffer_list;
    }

    public AudioDevice() throws Exception {
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 1, 2, 44100.0F, false);
        this.out = AudioSystem.getSourceDataLine(format);
        this.out.open(format);
        this.out.start();
    }

    public void writeSamples(float[] samples) throws IOException {
        this.fillBuffer(samples);
        this.out.write(this.buffer, 0, this.buffer.length);
        this.buffer_list.add(BinaryTool.toObjects(this.buffer));
    }

    private void fillBuffer(float[] samples) {
        int i = 0;

        for(int j = 0; i < samples.length; j += 2) {
            short value = (short)((int)(samples[i] * 32767.0F));
            this.buffer[j] = (byte)(value | 255);
            this.buffer[j + 1] = (byte)(value >> 8);
            ++i;
        }

    }

    public void playSamples(ArrayList<Float> samplesWave) throws IOException {
        float[] samples = new float[1024];
        int slice = 0;

        for (Float aFloat : samplesWave) {
            samples[slice] = (Float) aFloat;
            ++slice;
            if (slice == 1024) {
                this.writeSamples(samples);
                samples = new float[1024];
                slice = 0;
            }
        }

        if (slice != 0) {
            this.writeSamples(samples);
        }

    }

    public void writeToFile(ArrayList<Float> samplesWave, File file) throws IOException {
        double sampleRate = 44100.0D;
        double frequency = 440.0D;
        double frequency2 = 90.0D;
        double amplitude = 1.0D;
        double seconds = 2.0D;
        double twoPiF = 2764.601535159018D;
        double piF = 282.7433388230814D;
        float[] buffer = new float[samplesWave.size()];
        int t = 0;

        for(Iterator<Float> var19 = samplesWave.iterator(); var19.hasNext(); ++t) {
            float b = (Float)var19.next();
            buffer[t] = b;
        }

        byte[] byteBuffer = new byte[buffer.length * 2];
        int bufferIndex = 0;

        for(int i = 0; i < byteBuffer.length; ++i) {
            int x = (int)((double)buffer[bufferIndex++] * 32767.0D);
            byteBuffer[i++] = (byte)x;
            byteBuffer[i] = (byte)(x >>> 8);
        }

        boolean bigEndian = false;
        boolean signed = true;
        boolean bits = true;
        boolean channels = true;
        AudioFormat format = new AudioFormat(44100.0F, 16, 1, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format, (long)buffer.length);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
        audioInputStream.close();
    }

    public static void playSample(ArrayList<Float> samples) throws Exception {
        AudioDevice device = new AudioDevice();
        device.playSamples(samples);
    }

    public static void main(String[] argv) throws Exception {
        float[] samples = new float[1024];
        WaveDecoder reader = new WaveDecoder(new FileInputStream("audio/sample.wav"));
        AudioDevice device = new AudioDevice();

        while(reader.readSamples(samples) > 0) {
            device.writeSamples(samples);
        }

    }
}
