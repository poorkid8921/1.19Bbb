package expansions.scoreboard.util.buffer;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class ByteBufNetOutput implements NetOutput {
    private final ByteBuf buf;

    public ByteBufNetOutput(ByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public void writeByte(int b) {
        this.buf.writeByte(b);
    }

    @Override
    public void writeVarInt(int i) {
        while ((i & ~0x7F) != 0) {
            this.writeByte((i & 0x7F) | 0x80);
            i >>>= 7;
        }

        this.writeByte(i);
    }

    @Override
    public void writeString(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        this.writeVarInt(bytes.length);
        this.buf.writeBytes(bytes);
    }
}