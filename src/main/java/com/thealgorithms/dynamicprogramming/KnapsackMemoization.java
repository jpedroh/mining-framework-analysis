package com.thealgorithms.dynamicprogramming;

/**
 * Recursive Solution for 0-1 knapsack with memoization
 * This method is basically an extension to the recursive approach so that we
 * can overcome the problem of calculating redundant cases and thus increased
 * complexity. We can solve this problem by simply creating a 2-D array that can
 * store a particular state (n, w) if we get it the first time.
 */
public class KnapsackMemoization {
  int knapSack(int capacity, int[] weights, int[] profits, int numOfItems) {
    int[][] 
<<<<<<< /usr/src/app/output/thealgorithms/java/5b2622164831552c25a3c8ec56a8a05788448d30/src/main/java/com/thealgorithms/dynamicprogramming/KnapsackMemoization.java/left.java
    dp = new int[N + 1][W + 1]
=======
    dpTable = new int[numOfItems + 1][capacity + 1]
>>>>>>> /usr/src/app/output/thealgorithms/java/5b2622164831552c25a3c8ec56a8a05788448d30/src/main/java/com/thealgorithms/dynamicprogramming/KnapsackMemoization.java/right.java
    ;
    for (int i = 0; i < numOfItems + 1; i++) {
      for (int j = 0; j < capacity + 1; j++) {
        dpTable[i][j] = -1;
      }
    }
    return solveKnapsackRecursive(capacity, weights, profits, numOfItems, dpTable);
  }

  int solveKnapsackRecursive(int capacity, int[] weights, int[] profits, int numOfItems, int[][] dpTable) {
    if (numOfItems == 0 || capacity == 0) {
      return 0;
    }
    if (dpTable[numOfItems][capacity] != -1) {
      return dpTable[numOfItems][capacity];
    }
    if (weights[numOfItems - 1] > capacity) {
      dpTable[numOfItems][capacity] = solveKnapsackRecursive(capacity, weights, profits, numOfItems - 1, dpTable);
      return dpTable[numOfItems][capacity];
    } else {
      return dpTable[numOfItems][capacity] = Math.max((profits[numOfItems - 1] + solveKnapsackRecursive(capacity - weights[numOfItems - 1], weights, profits, numOfItems - 1, dpTable)), solveKnapsackRecursive(capacity, weights, profits, numOfItems - 1, dpTable));
    }
  }
}