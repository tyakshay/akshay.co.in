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