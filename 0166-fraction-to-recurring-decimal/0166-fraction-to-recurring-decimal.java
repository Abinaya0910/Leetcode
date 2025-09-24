import java.util.*;

public class Solution {
    
    public String fractionToDecimal(int numerator, int denominator) {
        // Handle zero case
        if (numerator == 0) return "0";
        
        StringBuilder result = new StringBuilder();
        
        // If result is negative
        if ((numerator < 0) ^ (denominator < 0)) {
            result.append("-");
        }
        
        // Convert to long (avoid overflow with Integer.MIN_VALUE)
        long num = Math.abs((long) numerator);
        long den = Math.abs((long) denominator);
        
        // Integer part
        result.append(num / den);
        long remainder = num % den;
        
        // If no remainder → exact division
        if (remainder == 0) {
            return result.toString();
        }
        
        // Otherwise, add the decimal point
        result.append(".");
        
        // Map to remember remainders and where each started
        Map<Long, Integer> map = new HashMap<>();
        
        // Long division process
        while (remainder != 0) {
            // If we’ve seen this remainder before → repeating cycle found
            if (map.containsKey(remainder)) {
                int index = map.get(remainder);
                result.insert(index, "(");
                result.append(")");
                break;
            }
            
            // Store the position of this remainder
            map.put(remainder, result.length());
            
            remainder *= 10;
            result.append(remainder / den);
            remainder %= den;
        }
        
        return result.toString();
    }
}
