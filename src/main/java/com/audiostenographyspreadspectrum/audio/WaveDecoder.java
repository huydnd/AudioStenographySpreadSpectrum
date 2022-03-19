package com.audiostenographyspreadspectrum.audio;

import javafx.scene.control.Alert;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

public class WaveDecoder{
    private final float MAX_VALUE = 3.051851E-5F;
    private final EndianDataInputStream in;
    private final int channels;
    private final float sampleRate;

    public WaveDecoder(InputStream stream) throws Exception {
        if (stream == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File rỗng");
            alert.setContentText("Vui lòng chọn lại file!");
            alert.showAndWait();
            throw new IllegalArgumentException("Input stream must not be null");
        } else {
            this.in = new EndianDataInputStream(new BufferedInputStream(stream, 1048576));
            if (!this.in.read4ByteString().equals("RIFF")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("File không thuộc định dạng WAV");
                alert.setContentText("Vui lòng chọn lại file!");
                alert.showAndWait();
                throw new IllegalArgumentException("not a wav");
            } else {
                this.in.readIntLittleEndian();
                if (!this.in.read4ByteString().equals("WAVE")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("File không có thẻ WAVE");
                    alert.setContentText("Vui lòng chọn lại file!");
                    alert.showAndWait();
                    throw new IllegalArgumentException("expected WAVE tag");
                } else if (!this.in.read4ByteString().equals("fmt ")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("File không có thẻ fmt");
                    alert.setContentText("Vui lòng chọn lại file!");
                    alert.showAndWait();
                    throw new IllegalArgumentException("expected fmt tag");
                } else if (this.in.readIntLittleEndian() != 16) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("File đã bị mã hóa");
                    alert.setContentText("Vui lòng chọn lại file!");
                    alert.showAndWait();
                    throw new IllegalArgumentException("expected wave chunk size to be 16");
                } else if (this.in.readShortLittleEndian() != 1) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("File đã bị mã hóa");
                    alert.setContentText("Vui lòng chọn lại file!");
                    alert.showAndWait();
                    throw new IllegalArgumentException("expected format to be 1");
                } else {
                    this.channels = this.in.readShortLittleEndian();
                    this.sampleRate = (float)this.in.readIntLittleEndian();
                    if (this.sampleRate != 44100.0F) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("File không chạy tần số 44100Hz ");
                        alert.setContentText("Vui lòng chọn lại file!");
                        alert.showAndWait();
                        throw new IllegalArgumentException("Not 44100 sampling rate");
                    } else {
                        this.in.readIntLittleEndian();
                        this.in.readShortLittleEndian();
                        int fmt = this.in.readShortLittleEndian();
                        if (fmt != 16) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("File không hỗ trợ định dạng tín hiệu 16-bit");
                            alert.setContentText("Vui lòng chọn lại file!");
                            alert.showAndWait();
                            throw new IllegalArgumentException("Only 16-bit signed format supported");
                        } else if (!this.in.read4ByteString().equals("data")) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("File không có tab data");
                            alert.setContentText("Vui lòng chọn lại file!");
                            alert.showAndWait();
                            throw new RuntimeException("expected data tag");
                        } else {
                            this.in.readIntLittleEndian();
                        }
                    }
                }
            }
        }
    }

    public int readSamples(float[] samples) {
        int readSamples = 0;

        for(int i = 0; i < samples.length; ++i) {
            float sample = 0.0F;

            try {
                for(int j = 0; j < this.channels; ++j) {
                    int shortValue = this.in.readShortLittleEndian();
                    sample += (float)shortValue * 3.051851E-5F;
                }

                sample /= (float)this.channels;
                samples[i] = sample;
                ++readSamples;
            } catch (Exception var7) {
                break;
            }
        }

        return readSamples;
    }

    public static void main(String[] args) throws FileNotFoundException, Exception {
        WaveDecoder decoder = new WaveDecoder(new FileInputStream("audio/sample.wav"));
        float[] samples = new float[102400];

        while(decoder.readSamples(samples) > 0) {
            System.out.println(Arrays.toString(samples));
        }

    }
}
