/**
 * problem: https://leetcode.com/problems/candy/description/
 * There are n children standing in a line. Each child is assigned a rating value given in the integer array ratings.
    You are giving candies to these children subjected to the following requirements:
    Each child must have at least one candy.
    Children with a higher rating get more candies than their neighbors.
    Return the minimum number of candies you need to have to distribute the candies to the children.

    

    Example 1:

    Input: ratings = [1,0,2]
    Output: 5
    Explanation: You can allocate to the first, second and third child with 2, 1, 2 candies respectively.
    Example 2:

    Input: ratings = [1,2,2]
    Output: 4
    Explanation: You can allocate to the first, second and third child with 1, 2, 1 candies respectively.
    The third child gets 1 candy because it satisfies the above two conditions.
    

    Constraints:

    n == ratings.length
    1 <= n <= 2 * 104
    0 <= ratings[i] <= 2 * 104
 */

 /**
  * Concept lies in below lines, 
    1. Each child must have at least one candy.
    Children with a higher rating get more candies than their neighbors.

    i.e 
    Every child can have 2 neighbour. left and right
    so we would need to allocate candy twice for current children, 1st by comparing left side neighbour, 2nd by compairing right side neighbour
    2. Children with a higher rating get more candies than their neighbors. this gives condition that
    while comparing with left check if ratings[current] > ratings[left], then current candidate must have more candies then left candidate. 
    similar case for right. 
  */
package com.dsa.practice.prep.Array;
class Solution {
    public int candy(int[] ratings) {
       int[] ltr = new int[ratings.length];
       int[] rtl = new int[ratings.length];
        ltr[0] = 1;
        rtl[ratings.length-1] = 1;
        for(int i=1 ; i < ratings.length; i++){
            if(ratings[i] > ratings[i-1]){
                ltr[i] = ltr[i-1]+1;
            }else{
                ltr[i] = 1;
            }
        }
        
         for(int i=ratings.length-2; i >= 0; i--){
            if(ratings[i] > ratings[i+1]){
                rtl[i] = rtl[i+1]+1;
            }else{
                rtl[i] = 1;
            }
        }
        int sum = 0;
        for(int i=0; i < ratings.length; i++){
            sum += Math.max(ltr[i],rtl[i]); // minimum no of candies will be max b/w ltr and rtl
        }
        return sum;
    }
}
