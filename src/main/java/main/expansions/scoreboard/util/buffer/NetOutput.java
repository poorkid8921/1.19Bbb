package main.expansions.scoreboard.util.buffer;

public interface NetOutput {
    void writeByte(int b);

    void writeVarInt(int i);

    void writeString(String s);
}