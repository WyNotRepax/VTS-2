package hsosstp.server;

import java.io.*;

public class Session {

    private static int nextSessionId = 0;

    public final int sessionId;
    private final int chunkSize;
    private final RandomAccessFile in;

    public Session(RandomAccessFile inFile, int chunkSize) {
        sessionId = nextSessionId++;
        this.chunkSize = chunkSize;
        in = inFile;
    }

    public byte[] getChunk(int chunkNumber) throws IOException {

        byte[] buff = new byte[chunkSize];
        in.seek(chunkNumber * chunkSize);
        int n = in.read(buff);
        if (n == -1) {
            return new byte[0];
        }
        byte[] data = new byte[n];
        System.arraycopy(buff, 0, data, 0, n);
        return data;
    }
}
