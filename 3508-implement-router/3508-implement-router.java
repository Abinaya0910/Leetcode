import java.util.*;

class Router {
    private int memoryLimit;
    private Deque<int[]> queue; // FIFO queue for packets
    private Set<String> packetSet; // for duplicate detection
    private Map<Integer, TreeMap<Integer, Integer>> destMap; // destination -> (timestamp -> count)

    public Router(int memoryLimit) {
        this.memoryLimit = memoryLimit;
        this.queue = new ArrayDeque<>();
        this.packetSet = new HashSet<>();
        this.destMap = new HashMap<>();
    }

    public boolean addPacket(int source, int destination, int timestamp) {
        String key = source + "#" + destination + "#" + timestamp;
        if (packetSet.contains(key)) {
            return false; // duplicate packet
        }

        // If memory full, evict oldest
        if (queue.size() == memoryLimit) {
            int[] oldest = queue.pollFirst();
            String oldKey = oldest[0] + "#" + oldest[1] + "#" + oldest[2];
            packetSet.remove(oldKey);

            // update destMap
            TreeMap<Integer, Integer> tm = destMap.get(oldest[1]);
            tm.put(oldest[2], tm.get(oldest[2]) - 1);
            if (tm.get(oldest[2]) == 0) {
                tm.remove(oldest[2]);
            }
        }

        // Add new packet
        int[] packet = new int[]{source, destination, timestamp};
        queue.offerLast(packet);
        packetSet.add(key);

        // update destMap
        destMap.putIfAbsent(destination, new TreeMap<>());
        TreeMap<Integer, Integer> tm = destMap.get(destination);
        tm.put(timestamp, tm.getOrDefault(timestamp, 0) + 1);

        return true;
    }

    public int[] forwardPacket() {
        if (queue.isEmpty()) {
            return new int[]{}; // no packets left
        }

        int[] packet = queue.pollFirst();
        String key = packet[0] + "#" + packet[1] + "#" + packet[2];
        packetSet.remove(key);

        // update destMap
        TreeMap<Integer, Integer> tm = destMap.get(packet[1]);
        tm.put(packet[2], tm.get(packet[2]) - 1);
        if (tm.get(packet[2]) == 0) {
            tm.remove(packet[2]);
        }

        return packet;
    }

    public int getCount(int destination, int startTime, int endTime) {
        if (!destMap.containsKey(destination)) {
            return 0;
        }
        TreeMap<Integer, Integer> tm = destMap.get(destination);

        // Submap of timestamps within [startTime, endTime]
        NavigableMap<Integer, Integer> subMap = tm.subMap(startTime, true, endTime, true);
        int count = 0;
        for (int val : subMap.values()) {
            count += val;
        }
        return count;
    }
}
