package com.dsa.practice.prep.Array;

/**
 * problem: https://leetcode.com/problems/remove-duplicates-from-sorted-array/
 * description: 
 * Given an integer array nums sorted in non-decreasing order, remove the duplicates in-place such that each unique element appears only once. The relative order of the elements should be kept the same. Then return the number of unique elements in nums.
    Consider the number of unique elements of nums to be k, to get accepted, you need to do the following things:
    Change the array nums such that the first k elements of nums contain the unique elements in the order they were present in nums initially. The remaining elements of nums are not important as well as the size of nums.
    Return k.
    Example 1:

    Input: nums = [1,1,2]
    Output: 2, nums = [1,2,_]
    Explanation: Your function should return k = 2, with the first two elements of nums being 1 and 2 respectively.
    It does not matter what you leave beyond the returned k (hence they are underscores).
    Example 2:

    Input: nums = [0,0,1,1,1,2,2,3,3,4]
    Output: 5, nums = [0,1,2,3,4,_,_,_,_,_]
    Explanation: Your function should return k = 5, with the first five elements of nums being 0, 1, 2, 3, and 4 respectively.
    It does not matter what you leave beyond the returned k (hence they are underscores).
    

    Constraints:

    1 <= nums.length <= 3 * 104
    -100 <= nums[i] <= 100
    nums is sorted in non-decreasing order.

 * 
 */
class Solution {
    public int removeDuplicates(int[] nums) {
        if (nums.length == 0) return 0;

        // first element is always unique
        int uniqueIndex = 0;

        // pointer j will loop through array to find duplicates
        for (int j = 1; j < nums.length; j++) {
            // check if duplicate. Not duplicate then change the element after the curr unique element
            if (nums[j] != nums[uniqueIndex]) {
                nums[uniqueIndex + 1] = nums[j];
                uniqueIndex++;
            }
        }

        return uniqueIndex + 1;   
    }     
}

/**
 * Extended questions
 * Given an integer array nums sorted in non-decreasing order, remove some duplicates in-place such that each unique element appears at most twice. The relative order of the elements should be kept the same.
Since it is impossible to change the length of the array in some languages, you must instead have the result be placed in the first part of the array nums. More formally, if there are k elements after removing the duplicates, then the first k elements of nums should hold the final result. It does not matter what you leave beyond the first k elements.
Return k after placing the final result in the first k slots of nums.
Do not allocate extra space for another array. You must do this by modifying the input array in-place with O(1) extra memory.
 */
// as array is sorted by default, then we can always look 2 element behind as atmost 2 duplicates are allowed. 
class Solution2 {
    public int removeDuplicates(int[] nums) {
        int ind=2;
        for(int i=2;i<nums.length;i++)
        {
            if(nums[ind-2]!=nums[i])
            {
                nums[ind]=nums[i];
                ind++;
            }
        }
        return ind;
    }
}
