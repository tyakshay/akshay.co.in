/**
 * Problem: https://leetcode.com/problems/merge-sorted-array
 * Description: 
 * You are given two integer arrays nums1 and nums2, sorted in non-decreasing order, and two integers m and n, representing the number of elements in nums1 and nums2 respectively.
Merge nums1 and nums2 into a single array sorted in non-decreasing order.
The final sorted array should not be returned by the function, but instead be stored inside the array nums1. To accommodate this, nums1 has a length of m + n,
 where the first m elements denote the elements that should be merged, and the last n elements are set to 0 and should be ignored. nums2 has a length of n.

Example 1:

Input: nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3
Output: [1,2,2,3,5,6]
Explanation: The arrays we are merging are [1,2,3] and [2,5,6].
The result of the merge is [1,2,2,3,5,6] with the underlined elements coming from nums1.
Example 2:

Input: nums1 = [1], m = 1, nums2 = [], n = 0
Output: [1]
Explanation: The arrays we are merging are [1] and [].
The result of the merge is [1].
Example 3:

Input: nums1 = [0], m = 0, nums2 = [1], n = 1
Output: [1]
Explanation: The arrays we are merging are [] and [1].
The result of the merge is [1].
Note that because m = 0, there are no elements in nums1. The 0 is only there to ensure the merge result can fit in nums1.
 
Constraints:

nums1.length == m + n
nums2.length == n
0 <= m, n <= 200
1 <= m + n <= 200
-109 <= nums1[i], nums2[j] <= 109
 * 
 */

 class Solution {
    
    public void swap(int[] arr, int i, int j){
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
    
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        // merge second array into 1st one
        for(int i = m; i < (m+n) ; i++){
           nums1[i] = nums2[i- m];   
        }
        // now sort the first array
        /**
         * As first part of the array (till m pointer) is already sorted, so we can start from m
         * Iterations:
            Outer loop (i = 3):

            j = 2: Compare 3 (nums1[2]) with 1 (nums1[3]).
            Swap: [1, 2, 1, 3, 2, 4, 6]
            j = 1: Compare 2 (nums1[1]) with 1 (nums1[2]).
            Swap: [1, 1, 2, 3, 2, 4, 6]
            j = 0: Compare 1 (nums1[0]) with 1 (nums1[1]).
            No Swap: [1, 1, 2, 3, 2, 4, 6]
            Array after i = 3: [1, 1, 2, 3, 2, 4, 6]

            Outer loop (i = 4):

            j = 3: Compare 3 (nums1[3]) with 2 (nums1[4]).
            Swap: [1, 1, 2, 2, 3, 4, 6]
            j = 2: Compare 2 (nums1[2]) with 2 (nums1[3]).
            No Swap: [1, 1, 2, 2, 3, 4, 6]
            Array after i = 4: [1, 1, 2, 2, 3, 4, 6]

            Outer loop (i = 5):

            j = 4: Compare 3 (nums1[4]) with 4 (nums1[5]).
            No Swap: [1, 1, 2, 2, 3, 4, 6]
            Array after i = 5: [1, 1, 2, 2, 3, 4, 6]

            Outer loop (i = 6):

            j = 5: Compare 4 (nums1[5]) with 6 (nums1[6]).
            No Swap: [1, 1, 2, 2, 3, 4, 6]
            Array after i = 6: [1, 1, 2, 2, 3, 4, 6]
         */
        for(int i = m; i < (m+n) ; i++){
            for(int j = i-1; j >= 0 ; j--){
                if(nums1[j] > nums1[j+1]){
                    swap(nums1, j , j+1);
                }else{
                    break;
                }
            }
        }
    }
}