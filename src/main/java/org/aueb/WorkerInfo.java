package org.aueb;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class WorkerInfo {
    //    private Worker worker;
    private final long worker_id;
    private final DataInputStream inputStream;

    private final DataOutputStream outputStream;

    private final Socket socket;

    public WorkerInfo(int id, Socket socket, DataInputStream inputStream, DataOutputStream outputStream) {
        this.worker_id = id;
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public Long getWorker_id() {
        return worker_id;
    }

    public DataInputStream getInputStream() {
        return inputStream;
    }

    public DataOutputStream getOutputStream() {
        return outputStream;
    }

    public Socket getSocket() {
        return socket;
    }

}
