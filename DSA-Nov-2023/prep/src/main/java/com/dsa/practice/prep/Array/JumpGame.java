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

