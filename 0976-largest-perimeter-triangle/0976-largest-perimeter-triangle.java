import java.util.Arrays;

class Solution {
    public int largestPerimeter(int[] nums) {
        // Sort the array in ascending order
        Arrays.sort(nums);
        
        // Traverse from the largest side backwards
        for (int i = nums.length - 1; i >= 2; i--) {
            int a = nums[i];
            int b = nums[i - 1];
            int c = nums[i - 2];
            
            // Check triangle inequality
            if (b + c > a) {
                return a + b + c; // Found the largest perimeter
            }
        }
        
        return 0; // No valid triangle
    }
}
