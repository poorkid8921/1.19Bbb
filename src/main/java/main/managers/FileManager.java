package main.managers;

import com.google.common.io.ByteArrayDataOutput;

import java.io.*;

public class FileManager {
    public void writeFile(String data, String file) {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
            out.writeChars(data);
        } catch (IOException ignored) {
        }
    }

    public void writeFile(byte[] data, String file) {
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(data);
        } catch (IOException ignored) {
        }
    }

    public String readFile(String file) {
        StringBuilder content = new StringBuilder();
        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                content.append(new String(buffer, 0, bytesRead));
            }
        } catch (IOException ignored) {
        }
        return content.toString();
    }
}