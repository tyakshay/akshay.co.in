/**
 * problem : https://leetcode.com/problems/rotate-array/description/
 * Rotate Array : Given an integer array nums, rotate the array to the right by k steps, where k is non-negative.
    Example 1:

    Input: nums = [1,2,3,4,5,6,7], k = 3
    Output: [5,6,7,1,2,3,4]
    Explanation:
    rotate 1 steps to the right: [7,1,2,3,4,5,6]
    rotate 2 steps to the right: [6,7,1,2,3,4,5]
    rotate 3 steps to the right: [5,6,7,1,2,3,4]
    Example 2:

    Input: nums = [-1,-100,3,99], k = 2
    Output: [3,99,-1,-100]
    Explanation: 
    rotate 1 steps to the right: [99,-1,-100,3]
    rotate 2 steps to the right: [3,99,-1,-100]
    

    Constraints:

    1 <= nums.length <= 105
    -231 <= nums[i] <= 231 - 1
    0 <= k <= 105
 */

 /**
  * concept : 
  1. Rotate whole array : 
   [7,6,5,4,3,2,1]
  2. Now rotate first k element 
   [5,6,7,4,3,2,1]
  3. then rotate elements after K
   [5,6,7,1,2,3,4]

  */
package com.dsa.practice.prep.Array;
class Solution {
    public void rotate(int[] nums, int k) {
        int n = nums.length;
         k = k % n;
        rotate(nums, 0, n-1);
        rotate(nums, 0, k-1);
        rotate(nums, k,n-1);
    }

    void rotate(int[] arr, int left, int right){
        while(left < right){
            int temp = arr[left];
            arr[left] = arr[right];
            arr[right] = temp;
            left++;
            right--;
        }
    }
}

/**
 * Time Complexity : O(n) 
 * Space Complexity : O(1)
 */