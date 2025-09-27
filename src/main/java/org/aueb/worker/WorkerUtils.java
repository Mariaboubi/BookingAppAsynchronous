package org.aueb.worker;

import org.aueb.util.Constants;
import org.aueb.util.SocketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class WorkerUtils {
    private static final Logger logger = LoggerFactory.getLogger(WorkerUtils.class); // Logger for the WorkerUtils class
    /**
     * Generates a hash code for a given item name, which can be used to distribute tasks among workers.
     * @param itemName The name of the item to hash.
     * @return An integer hash code.
     */
    public static int hashFunction(String itemName) {
        return itemName.hashCode();
    }

    /**
     * Selects a worker based on the hash of an item. This method helps in distributing tasks evenly across workers.
     * @param item The item based on which a worker needs to be selected.
     * @param workerInfoMap A map of worker IDs to WorkerInfo objects.
     * @return The ID of the worker selected for this item.
     */
    public static long selectWorker(String item, Map<Integer, WorkerInfo> workerInfoMap) {
        int workerId = Math.abs(hashFunction(item)) % Constants.NUM_WORKER_NODES;
        return workerInfoMap.get(workerId).getWorker_id();
    }

    /**
     * Establishes connections to all workers specified by their server ports and stores their connection info.
     * This method is typically used at startup to initialize connections to all workers in the system.
     * @param ports A list of ports, one for each worker, where the worker servers are listening.
     * @param workerInfoMap A map to store WorkerInfo objects indexed by worker ID for later access.
     */
    public static void connectToWorkers(ArrayList<Integer> ports, LinkedHashMap<Integer, WorkerInfo> workerInfoMap) {
        for (int i = 0; i < ports.size(); i++) {
            logger.info("Connecting to worker " + i + " on port " + ports.get(i));
            Socket workerSocket = SocketUtils.createSocket("localhost", ports.get(i));
            workerInfoMap.put(i, new WorkerInfo(i, workerSocket,
                    SocketUtils.createDataInputStream(workerSocket),
                    SocketUtils.createDataOutputStream(workerSocket)));
        }
    }
}
