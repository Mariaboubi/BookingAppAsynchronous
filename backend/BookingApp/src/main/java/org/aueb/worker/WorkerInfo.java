package org.aueb.worker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * The WorkerInfo class encapsulates the connectivity details and streams of a single worker in a distributed system.
 * This includes the worker's identification number, the socket through which the worker is connected,
 * and both input and output data streams.
 */
public class WorkerInfo {
    private final long worker_id; // unique worker id
    private final DataInputStream inputStream; // input stream from the worker
    private final DataOutputStream outputStream; // output stream to the worker
    private final Socket socket; // socket connection to the worker
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
