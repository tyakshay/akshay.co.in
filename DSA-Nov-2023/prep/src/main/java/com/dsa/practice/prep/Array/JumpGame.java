import java.util.Arrays;

/**
 * problem: https://leetcode.com/problems/jump-game/
 * Intuition
The problem requires determining if it's possible to reach the last index starting from the first index. My initial thought is to work backwards, treating the problem as finding if we can "jump" to the beginning rather than reaching the end. This reverse perspective simplifies the task.
    You are given an integer array nums. You are initially positioned at the array's first index, and each element in the array represents your maximum jump length at that position.
    Return true if you can reach the last index, or false otherwise.
    Example 1:

    Input: nums = [2,3,1,1,4]
    Output: true
    Explanation: Jump 1 step from index 0 to 1, then 3 steps to the last index.
    Example 2:

    Input: nums = [3,2,1,0,4]
    Output: false
    Explanation: You will always arrive at index 3 no matter what. Its maximum jump length is 0, which makes it impossible to reach the last index.
    

    Constraints:

    1 <= nums.length <= 104
    0 <= nums[i] <= 105

Approach
    Start from the last index and initialize it as the target index to reach.
    Iterate backward through the array.
    For each position, check if it's possible to jump to the current target using the value at that position (nums[i]).
    If possible, update the target to the current position.
    Finally, check if the target reaches the first index (0), which means we can jump from the start to the end.
    This approach is efficient as we only pass through the array once and keep updating the target.

    Time complexity:
    O(n), where n is the length of the array, since we iterate through the array once.

    Space complexity:
    O(1), as we use only a few variables for tracking indices and perform the calculations in-place.
   - for images : https://leetcode.com/problems/jump-game/description/comments/1565175
 */

 class Solution {
    public boolean canJump(int[] nums) {
      int n = nums.length; 
      int target = n-1; 
      for(int i = n-2; i>=0; i--){
         if( i + nums[i] >= target){
            target = i;
         }
      }
      return target  == 0;   
    }
}

/**
 * JumpGame II
 * Problem : https://leetcode.com/problems/jump-game-ii/description/
 * You are given a 0-indexed array of integers nums of length n. You are initially positioned at nums[0].

   Each element nums[i] represents the maximum length of a forward jump from index i. In other words, if you are at nums[i], you can jump to any nums[i + j] where:

   0 <= j <= nums[i] and
   i + j < n
   Return the minimum number of jumps to reach nums[n - 1]. The test cases are generated such that you can reach nums[n - 1].

   

   Example 1:

   Input: nums = [2,3,1,1,4]
   Output: 2
   Explanation: The minimum number of jumps to reach the last index is 2. Jump 1 step from index 0 to 1, then 3 steps to the last index.
   Example 2:

   Input: nums = [2,3,0,1,4]
   Output: 2
 
 */

 public int jump(int[] nums) {
   // If no jumps needed
   if (nums.length <= 1) return 0;
   
   int jumps = 0;       // Jump counter
   int maxReach = 0;    // Farthest position reachable
   int nextMaxReach = 0;// Next possible max reach
   
   for (int i = 0; i < nums.length - 1; i++) {
       // Update how far we can reach
       nextMaxReach = Math.max(nextMaxReach, i + nums[i]);
       
       // If we've used all jumps in current range
       if (i == maxReach) {
           jumps++;     // Take a jump
           maxReach = nextMaxReach;
       }
   }
   
   return jumps;
}

/**
        0  1  2  3  4
   arr [2, 3, 1, 1, 4]
   i = 0 
   nextMaxReach , max(0 [initial value], 0 + 2 [nums[i]]) = 2
   if(i== maxReach) true 
   jump++ = jump = 1 , nextMaxReach = maxReach = 2.
   Note: at 0 index, we took first jump
   From here, we need to check till 2 index how much max jump we can take

   i = 1
   nextMaxReach = max(2, 1 + 3) = 4
   if(i == maxReach) false;
   Note: nextMaxReach is updated here to 4.. 

   i = 2 
   nextMaxReach = max(4, 2 + 1) = 4
   if(i == maxReach) true;
   jump++ = 2
   maxReach = nextMaxReach = 4
   Note: this is maxReach of index 0 i.e arr[0] = 2, after reaching here we found out that maxReach is 4, so incresing jump here
   instead at index 1, because we need to visit all possible jump, then decide which jump will take us farthest/desired point. 

   i = 3 
   nextMaxReach = 4
   if(i == maxReach) false
 */

 class JumpGameIIDP {
   public int jump(int[] nums) {
       // Handle base cases
       int n = nums.length;
       if (n <= 1) return 0;
       
       // DP array to store minimum jumps to reach each index
       int[] dp = new int[n];
       
       // Initialize with maximum possible value
       Arrays.fill(dp, Integer.MAX_VALUE);
       
       // Start position always requires 0 jumps
       dp[0] = 0;
       
       // Compute minimum jumps for each position
       for (int i = 0; i < n; i++) {
           // For each possible jump from current position
           for (int j = 1; j <= nums[i] && i + j < n; j++) {
               // Update minimum jumps to reach next position
               dp[i + j] = Math.min(dp[i + j], dp[i] + 1);
           }
       }
       
       // Return minimum jumps to reach last index
       return dp[n - 1];
   }
   
   // Alternative bottom-up DP approach
   public int jumpAlternative(int[] nums) {
       int n = nums.length;
       if (n <= 1) return 0;
       
       // DP array to track minimum jumps
       int[] dp = new int[n];
       
       // Initialize with max value
       Arrays.fill(dp, Integer.MAX_VALUE - 1);
       dp[0] = 0;
       
       // Bottom-up computation
       for (int i = 1; i < n; i++) {
           // Check all previous positions
           for (int j = 0; j < i; j++) {
               // If we can jump from j to i
               if (j + nums[j] >= i) {
                   dp[i] = Math.min(dp[i], dp[j] + 1);
               }
           }
       }
       
       return dp[n - 1];
   }
 
}

/**
 
### Dynamic Programming Approach Explained

#### Key Concepts
1. **State Definition**: 
  - `dp[i]` represents the minimum number of jumps required to reach index `i`
  - Initial value set to `Integer.MAX_VALUE`

2. **Transition Relationship**:
  - For each index, explore all possible jumps
  - Update minimum jumps to reach subsequent positions

#### Approach Breakdown

1. **First DP Solution**:
  - Iterate through each position
  - For each position, calculate possible jumps
  - Update minimum jumps to reach next positions
  - Time Complexity: O(n²)
  - Space Complexity: O(n)

2. **Alternative Bottom-Up Approach**:
  - Start from first index
  - For each index, check all previous positions
  - Update minimum jumps if a shorter path is found
  - Time Complexity: O(n²)
  - Space Complexity: O(n)

### Comparative Analysis

| Approach | Time Complexity | Space Complexity | Pros | Cons |
|----------|-----------------|------------------|------|------|
| Greedy   | O(n)            | O(1)             | Most efficient | Less intuitive |
| DP (First) | O(n²)         | O(n)             | More straightforward | Less performant |
| DP (Bottom-Up) | O(n²)     | O(n)             | Comprehensive | Performance overhead |

### Learning Insights

1. DP solutions trade time efficiency for code clarity
2. Multiple approaches exist for the same problem
3. Always consider time and space complexity
4. Choose solution based on specific constraints

### Recommended Practice

1. Implement both solutions
2. Compare performance
3. Understand trade-offs
4. Experiment with different input sizes
 */