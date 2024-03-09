package main.utils.arenas;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Utils {
    public static boolean isMatch(byte[] pattern, byte[] input, int pos) {
        if (pos + (pattern.length - 1) > input.length) return false;
        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] != input[pos + i]) {
                return false;
            }
        }
        return true;
    }

    public static List<byte[]> split(byte[] pattern, byte[] input) {
        List<byte[]> l = new LinkedList<>();
        int blockStart = 0;
        for (int i = 0; i < input.length; i++) {
            if (isMatch(pattern, input, i)) {
                l.add(Arrays.copyOfRange(input, blockStart, i));
                blockStart = i + pattern.length;
                i = blockStart;
            }
        }
        l.add(Arrays.copyOfRange(input, blockStart, input.length));
        return l;
    }

    public static byte[] compress(byte[] bytes) {
        Deflater compresser = new Deflater();
        compresser.setInput(bytes);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bytes.length);
        compresser.finish();
        byte[] buffer = new byte[1024];
        while (!compresser.finished()) {
            int count = compresser.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        compresser.end();
        return outputStream.toByteArray();
    }

    public static byte[] decompress(byte[] bytes) {
        Inflater decompresser = new Inflater();
        decompresser.setInput(bytes);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bytes.length);
            decompresser.setInput(bytes);
            byte[] buffer = new byte[1024];
            while (!decompresser.finished()) {
                int count = decompresser.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            return outputStream.toByteArray();
        } catch (DataFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}