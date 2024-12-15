/**
 * problem : https://leetcode.com/problems/zigzag-conversion/
 * statement : 
 * The string "PAYPALISHIRING" is written in a zigzag pattern on a given number of rows like this: (you may want to display this pattern in a fixed font for better legibility)

    P   A   H   N
    A P L S I I G
    Y   I   R
    And then read line by line: "PAHNAPLSIIGYIR"

    Write the code that will take a string and make this conversion given a number of rows:

    string convert(string s, int numRows);

    Intuition : 
    zigzag meaning changing direction. 
    we are given no of rows.. so when we are on first row, go down, when we are on last row go up. 
    traverse input string in above manner.. 
    another trick to use array of stringbuilder, because stringbuilder has efficient append operation.. 
    and in each string builder we can store result of each string. at last append all of them to get answer

 * 
 */

 class Solution {
    public String convert(String s, int numRows) {
      if (numRows == 1) return s;

      // Create rows
      StringBuilder[] rows = new StringBuilder[numRows];
      for (int i = 0; i < numRows; i++) {
          rows[i] = new StringBuilder();
      }

      // Variables for traversal
      int currentRow = 0;
      boolean goingDown = false;

      // Traverse the string
      for (char c : s.toCharArray()) {
          rows[currentRow].append(c);
          if (currentRow == 0 || currentRow == numRows - 1) {
              goingDown = !goingDown; // Change direction
          }
          currentRow += goingDown ? 1 : -1;
      }
      StringBuilder result = new StringBuilder();
      for (StringBuilder row : rows) {
          result.append(row);
      }

      return result.toString();
  }
}