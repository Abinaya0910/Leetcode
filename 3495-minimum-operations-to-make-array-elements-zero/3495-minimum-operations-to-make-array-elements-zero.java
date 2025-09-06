import java.util.*;

class Solution {
    public long minOperations(int[][] queries) {
        long ans = 0;
        for (int[] q : queries) {
            long l = q[0], r = q[1];
            long totalSteps = prefix(r) - prefix(l - 1);
            ans += (totalSteps + 1) / 2; // ceil division
        }
        return ans;
    }

    // prefix(n) = sum of steps(x) for x = 1..n
    private long prefix(long n) {
        if (n <= 0) return 0;

        long res = 0;
        long start = 1;
        int k = 1;

        while (start <= n) {
            long end = start * 4 - 1; // end of this block
            if (end > n) end = n;
            long count = end - start + 1;
            res += count * k;  // each number in this block needs k steps

            start *= 4;
            k++;
        }

        return res;
    }

    // main method for quick testing
    public static void main(String[] args) {
        Solution sol = new Solution();

        int[][] queries1 = {{1,2}, {2,4}};
        System.out.println(sol.minOperations(queries1)); // Expected 3

        int[][] queries2 = {{2,6}};
        System.out.println(sol.minOperations(queries2)); // Expected 4
    }
}
