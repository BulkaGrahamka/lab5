package org.example.common;

import java.util.*;

/**
 * Klasa Board odpowiada WYŁĄCZNIE za stan planszy i reguły Go.
 * Serwer korzysta z niej jako jedynego źródła prawdy.
 */
public class Board {
    private final int size;
    private final int[][] grid;
    private int currentPlayer = 1;
    private final int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};

    public Board(int size) {
        this.size = size;
        grid = new int[size][size];
        for (int i = 0; i < size; i++) {
            Arrays.fill(grid[i], 0);
        }
    }

    public synchronized int playMove(int row, int col, int player) {
        if (player != currentPlayer) return -1;
        if (!inBounds(row, col) || grid[row][col] != 0) return -1;

        int enemy = (player == 1) ? 2 : 1;
        grid[row][col] = player;
        int captured = 0;
        // zbicia przeciwnika
        for (int[] dir : dirs) {
           int nRow = row + dir[0];
           int nCol = col + dir[1];
           if (!inBounds(nRow, nCol)) continue;
           if (grid[nRow][nCol] == enemy) {
               if (!hasLiberties(nRow, nCol, grid)) {
                   captured += removeGroup(nRow, nCol, enemy); //zliczamy punkty
               }
           }
        }

        // zakaz samobójstwa
        if (!hasLiberties(row, col, grid)) {
            if (captured == 0) { //samobojstwo
                grid[row][col] = 0;
                return -2;
            }
        }

        currentPlayer = enemy;
        return captured;
    }

    private int removeGroup(int row, int col, int player) {
        if (!inBounds(row, col)) return 0;
        if (grid[row][col] == 0) return 0;
        
        int removed = 0;
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{row, col});
        grid[row][col] = -player; //oznaczenie pola zeby nie liczyc go ponownie

        while (!stack.isEmpty()) {
            int[] pop = stack.pop();
            int pRow = pop[0];
            int pCol = pop[1];
            removed++;
            for (int[] dir : dirs) {
                int nRow = pRow + dir[0];
                int nCol = pCol + dir[1];
                if (!inBounds(nRow, nCol)) continue;
                if (grid[nRow][nCol] == player) {
                    stack.push(new int[]{nRow, nCol});
                    grid[nRow][nCol] = -player;
                }
            }
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == -player) {
                    grid[i][j] = 0;
                }
            }
        }

        return removed;
    }
    
    private boolean hasLiberties(int row, int col, int[][] boardCopy) {
        if (!inBounds(row, col)) return false;
        int color = boardCopy[row][col];
        if (color == 0) return true;

        boolean[][] visited = new boolean[size][size];

        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{row, col});
        visited[row][col] = true;

        while (!stack.isEmpty()) {
            int[] pop = stack.pop();
            int pRow = pop[0];
            int pCol = pop[1];
            for (int[] dir : dirs) {
                int nRow = pRow + dir[0];
                int nCol = pCol + dir[1];
                if (!inBounds(nRow, nCol)) continue;
                if (boardCopy[nRow][nCol] == 0) return true;
                if (!visited[nRow][nCol] && boardCopy[nRow][nCol] == color) {
                    visited[nRow][nCol] = true;
                    stack.push(new int[]{nRow, nCol});
                }
            }
        }
        return false;
    }

    private boolean inBounds(int row, int col) {
        return row >= 0 && col >= 0 && row < size && col < size;
    }

    /**
     * ASCII plansza – gotowa do wysłania do klienta
     */
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        for (int col = 0; col < size; col++) {
            sb.append(String.format("%2d", col));
        }
        for (int row = 0; row < size; row++) {
            sb.append(String.format("%2d", row));
            for (int col = 0; col < size; col++) {
                char ch = '.';
                if (grid[row][col] == 1) ch = 'C';
                if (grid[row][col] == 2) ch = 'B';
                sb.append(" ").append(ch);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
