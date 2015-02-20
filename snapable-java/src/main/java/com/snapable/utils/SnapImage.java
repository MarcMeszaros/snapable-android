package com.snapable.utils;

import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

import java.io.*;

public class SnapImage implements TypedOutput, TypedInput {

    private byte[] bytes;

    public SnapImage(byte[] bytes) {
        this.bytes = bytes;
    }

    public SnapImage(File file) {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream(2048);
            FileInputStream in = new FileInputStream(file);
            int b;
            while ((b = in.read()) >= 0 ) {
                result.write(b);
            }
            in.close();
            this.bytes = result.toByteArray();
        } catch (FileNotFoundException e) {
            //
        } catch (IOException e) {
            //
        }
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public String fileName() {
        return "image.jpg";
    }

    @Override
    public String mimeType() {
        return "image/jpeg";
    }

    @Override
    public long length() {
        return bytes.length;
    }

    @Override
    public InputStream in() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(bytes);
    }
}
