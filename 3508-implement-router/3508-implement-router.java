import java.util.*;

class Router {
    private int memoryLimit;
    private Deque<int[]> queue; // FIFO
    private Set<String> packetSet; // to check duplicates
    private Map<Integer, TreeMap<Integer, Integer>> destMap; // destination -> (timestamp -> prefixCount)

    public Router(int memoryLimit) {
        this.memoryLimit = memoryLimit;
        this.queue = new ArrayDeque<>();
        this.packetSet = new HashSet<>();
        this.destMap = new HashMap<>();
    }

    public boolean addPacket(int source, int destination, int timestamp) {
        String key = source + "#" + destination + "#" + timestamp;
        if (packetSet.contains(key)) return false; // duplicate

        // Evict oldest if full
        if (queue.size() == memoryLimit) {
            int[] oldest = queue.pollFirst();
            String oldKey = oldest[0] + "#" + oldest[1] + "#" + oldest[2];
            packetSet.remove(oldKey);
            removeFromDestMap(oldest[1], oldest[2]);
        }

        // Add packet
        int[] packet = new int[]{source, destination, timestamp};
        queue.offerLast(packet);
        packetSet.add(key);

        addToDestMap(destination, timestamp);
        return true;
    }

    public int[] forwardPacket() {
        if (queue.isEmpty()) return new int[]{};
        int[] packet = queue.pollFirst();
        String key = packet[0] + "#" + packet[1] + "#" + packet[2];
        packetSet.remove(key);
        removeFromDestMap(packet[1], packet[2]);
        return packet;
    }

    public int getCount(int destination, int startTime, int endTime) {
        if (!destMap.containsKey(destination)) return 0;
        TreeMap<Integer, Integer> tm = destMap.get(destination);

        // find prefix sum <= endTime
        Map.Entry<Integer, Integer> endEntry = tm.floorEntry(endTime);
        if (endEntry == null) return 0;
        int prefixEnd = endEntry.getValue();

        // find prefix sum < startTime
        Map.Entry<Integer, Integer> startEntry = tm.lowerEntry(startTime);
        int prefixStart = (startEntry == null) ? 0 : startEntry.getValue();

        return prefixEnd - prefixStart;
    }

    // --- Helpers ---
    private void addToDestMap(int destination, int timestamp) {
        destMap.putIfAbsent(destination, new TreeMap<>());
        TreeMap<Integer, Integer> tm = destMap.get(destination);

        // get last prefix
        int prev = tm.isEmpty() ? 0 : tm.lastEntry().getValue();
        tm.put(timestamp, prev + 1);
    }

    private void removeFromDestMap(int destination, int timestamp) {
        TreeMap<Integer, Integer> tm = destMap.get(destination);
        if (tm == null) return;

        // We must "rebuild" suffix prefix sums after removal
        // Strategy: remove timestamp entry and rebuild from there
        if (!tm.containsKey(timestamp)) return;

        // Current value at timestamp
        int current = tm.get(timestamp);

        // Need to shift down all subsequent prefix values
        NavigableMap<Integer, Integer> tail = tm.tailMap(timestamp, true);
        List<Integer> keys = new ArrayList<>(tail.keySet());
        for (int k : keys) {
            tm.put(k, tm.get(k) - 1);
        }

        // If count at timestamp now equals the previous prefix, just clean up
        if (!tm.isEmpty() && tm.firstEntry().getValue() == 0) {
            tm.remove(tm.firstKey());
        }
    }
}
