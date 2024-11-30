package com.dsa.practice.prep.Array;
/**
 * problem : https://leetcode.com/problems/best-time-to-buy-and-sell-stock/
 * You are given an array prices where prices[i] is the price of a given stock on the ith day.

    You want to maximize your profit by choosing a single day to buy one stock and choosing a different day in the future to sell that stock.
    Return the maximum profit you can achieve from this transaction. If you cannot achieve any profit, return 0.
    Example 1:

    Input: prices = [7,1,5,3,6,4]
    Output: 5
    Explanation: Buy on day 2 (price = 1) and sell on day 5 (price = 6), profit = 6-1 = 5.
    Note that buying on day 2 and selling on day 1 is not allowed because you must buy before you sell.
    Example 2:
    Input: prices = [7,6,4,3,1]
    Output: 0
    Explanation: In this case, no transactions are done and the max profit = 0.
 */
/**
 * consider that last point as a selling point, and trace from right to left, 
 * At every point check if current point can be selling point i.e if currentElement > sellingPointProfitSoFar, then update sellingPointSoFar
 * then check if at currentIndx we can get maximum profile by considering current point as buying point.
 * 
 * we can do this problem other way around like consider current point as buying point, and travese the array from left to right, and update the buying point if needed 
 * and calculate profile
 * 
 */
class Solution {
    public int maxProfit(int[] prices) {
        int buyDayProfit = prices[0];
        int max = 0;
        for(int i = 1; i < prices.length ; i++){
            if(prices[i] < buyDayProfit){
                buyDayProfit = prices[i];
            }
            max = Math.max(max, prices[i] - buyDayProfit);
        }
        return max; 
    }
}

/**
 * Case-2 : Buy & Sell Stock II 
 * problem : https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/description/
 * 122. Best Time to Buy and Sell Stock II
You are given an integer array prices where prices[i] is the price of a given stock on the ith day.
On each day, you may decide to buy and/or sell the stock. You can only hold at most one share of the stock at any time. However, you can buy it then immediately sell it on the same day.
Find and return the maximum profit you can achieve.

 Example 1:

Input: prices = [7,1,5,3,6,4]
Output: 7
Explanation: Buy on day 2 (price = 1) and sell on day 3 (price = 5), profit = 5-1 = 4.
Then buy on day 4 (price = 3) and sell on day 5 (price = 6), profit = 6-3 = 3.
Total profit is 4 + 3 = 7.
Example 2:

Input: prices = [1,2,3,4,5]
Output: 4
Explanation: Buy on day 1 (price = 1) and sell on day 5 (price = 5), profit = 5-1 = 4.
Total profit is 4.
Example 3:

Input: prices = [7,6,4,3,1]
Output: 0
Explanation: There is no way to make a positive profit, so we never buy the stock to achieve the maximum profit of 0.
 

Constraints:

1 <= prices.length <= 3 * 104
0 <= prices[i] <= 104
 */

 /**
  * Approch : Here we can buy & sell stocks in serial manner. so think of a stock market graph which is increasing and decresing points. 
Every time we see that stock prices is increasing in graph, we will increment selling point, and when we observe that there stock price is falling down, then we will collect profit and move buying point and selling point to current date. 
At last we need to collect profile again, because it might not have dropping point at last. 
  */
class Solution2 {
    public int maxProfit(int[] arr) {
        int b = 0, s = 0, prfilt = 0;
        for(int i = 1 ; i < arr.length ; i++){
            if(arr[i] >= arr[i-1]){
                s++; // keep increasing selling point if graph slop is increasing
            }else{
                prfilt += arr[s] - arr[b];
                b = s = i;
            }
        }
        prfilt += arr[s] - arr[b];
        return prfilt;
    }
}