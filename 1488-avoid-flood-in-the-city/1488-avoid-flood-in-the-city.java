import java.util.*;

class Solution {
    public int[] avoidFlood(int[] rains) {
        int n = rains.length;
        int[] ans = new int[n];
        HashMap<Integer, Integer> fullLakes = new HashMap<>(); // lake -> last day it was filled
        TreeSet<Integer> dryDays = new TreeSet<>(); // indices of dry days

        for (int i = 0; i < n; i++) {
            if (rains[i] == 0) {
                dryDays.add(i);
                ans[i] = 1; // default, will change later if needed
            } else {
                int lake = rains[i];
                ans[i] = -1; // raining day
                if (fullLakes.containsKey(lake)) {
                    // find the next dry day after it was last filled
                    Integer dryDay = dryDays.higher(fullLakes.get(lake));
                    if (dryDay == null) {
                        return new int[0]; // impossible to avoid flood
                    }
                    ans[dryDay] = lake; // dry that lake
                    dryDays.remove(dryDay);
                }
                fullLakes.put(lake, i);
            }
        }
        return ans;
    }
}
