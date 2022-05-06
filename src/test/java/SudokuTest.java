package test.java;

import main.java.Sudoku;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SudokuTest {

    int[][] grid;


    @Test
    void check() {
        row();
    }

    @Test
    void row(){
        Sudoku sudoku = new Sudoku("");
        ArrayList<String> rulesToCheck = new ArrayList<>();
        rulesToCheck.add("row");
        sudoku.setRulesToCheck(rulesToCheck);
        grid = new int[9][9];
        grid[0] = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1};
        grid[1] = new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2};
        grid[2] = new int[]{3, 3, 3, 3, 3, 3, 3, 3, 3};
        grid[3] = new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4};
        grid[4] = new int[]{5, 5, 5, 5, 5, 5, 5, 5, 5};
        grid[5] = new int[]{6, 6, 6, 6, 6, 6, 6, 6, 6};
        grid[6] = new int[]{7, 7, 7, 7, 7, 7, 7, 7, 7};
        grid[7] = new int[]{8, 8, 8, 8, 8, 8, 8, 8, 8};
        grid[8] = new int[]{9, 9, 9, 9, 9, 9, 9, 9, 9};
        Assert.assertEquals(1,sudoku.check(grid));
        int tmp = grid[5][5];
        grid[5][5] = 1;
        Assert.assertEquals(-1,sudoku.check(grid));
        grid[5][5] = tmp;
        tmp = grid[0][0];
        grid[0][0] = 5;
        Assert.assertEquals(-1,sudoku.check(grid));
        grid[0][0] = tmp;
        tmp = grid[0][8];
        grid[0][8] = 5;
        Assert.assertEquals(-1,sudoku.check(grid));
        grid[0][8] = tmp;
        tmp = grid[8][0];
        grid[8][0] = 5;
        Assert.assertEquals(-1,sudoku.check(grid));
        grid[8][0] = tmp;
        tmp = grid[8][8];
        grid[8][8] = 5;
        Assert.assertEquals(-1,sudoku.check(grid));
        grid[8][8] = tmp;
    }

    @Test
    void col(){
        Sudoku sudoku = new Sudoku("");
        ArrayList<String> rulesToCheck = new ArrayList<>();
        rulesToCheck.add("column");
        sudoku.setRulesToCheck(rulesToCheck);
        grid = new int[9][9];
        grid[0] = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        grid[1] = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        grid[2] = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        grid[3] = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        grid[4] = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        grid[5] = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        grid[6] = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        grid[7] = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        grid[8] = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        Assert.assertEquals(1,sudoku.check(grid));
        int tmp = grid[5][5];
        grid[5][5] = 1;
        Assert.assertEquals(-1,sudoku.check(grid));
        grid[5][5] = tmp;
        tmp = grid[0][0];
        grid[0][0] = 5;
        Assert.assertEquals(-1,sudoku.check(grid));
        grid[0][0] = tmp;
        tmp = grid[0][8];
        grid[0][8] = 5;
        Assert.assertEquals(-1,sudoku.check(grid));
        grid[0][8] = tmp;
        tmp = grid[8][0];
        grid[8][0] = 5;
        Assert.assertEquals(-1,sudoku.check(grid));
        grid[8][0] = tmp;
        tmp = grid[8][8];
        grid[8][8] = 5;
        Assert.assertEquals(-1,sudoku.check(grid));
        grid[8][8] = tmp;
    }

    @Test
    void box(){
        Sudoku sudoku = new Sudoku("");
        ArrayList<String> rulesToCheck = new ArrayList<>();
        rulesToCheck.add("box");
        sudoku.setRulesToCheck(rulesToCheck);
        grid = new int[9][9];
        grid[0] = new int[]{1, 4, 7, 1, 4, 7, 1, 4, 7};
        grid[1] = new int[]{2, 5, 8, 2, 5, 8, 2, 5, 8};
        grid[2] = new int[]{3, 6, 9, 3, 6, 9, 3, 6, 9};
        grid[3] = new int[]{1, 4, 7, 1, 4, 7, 1, 4, 7};
        grid[4] = new int[]{2, 5, 8, 2, 5, 8, 2, 5, 8};
        grid[5] = new int[]{3, 6, 9, 3, 6, 9, 3, 6, 9};
        grid[6] = new int[]{1, 4, 7, 1, 4, 7, 1, 4, 7};
        grid[7] = new int[]{2, 5, 8, 2, 5, 8, 2, 5, 8};
        grid[8] = new int[]{3, 6, 9, 3, 6, 9, 3, 6, 9};
        Assert.assertEquals(1,sudoku.check(grid));
        int tmp = grid[4][4];
        grid[4][4] = 1;
        Assert.assertEquals(-1,sudoku.check(grid));
        grid[4][4] = tmp;
        tmp = grid[0][0];
        grid[0][0] = 5;
        Assert.assertEquals(-1,sudoku.check(grid));
        grid[0][0] = tmp;
        tmp = grid[0][8];
        grid[0][8] = 5;
        Assert.assertEquals(-1,sudoku.check(grid));
        grid[0][8] = tmp;
        tmp = grid[8][0];
        grid[8][0] = 5;
        Assert.assertEquals(-1,sudoku.check(grid));
        grid[8][0] = tmp;
        tmp = grid[8][8];
        grid[8][8] = 5;
        Assert.assertEquals(-1,sudoku.check(grid));
        grid[8][8] = tmp;
    }
}