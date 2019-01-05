package com.slowfrog.qwop.genetic;

import java.util.ArrayList;
import java.util.List;

/*
 * This class describing a borderless "wrap-around" 2D array was modified from
 * 		http://stackoverflow.com/questions/9058217/2d-array-class-in-java-with-wrap-around-edges
 */
public class WrapGrid<T> {

    final List<T> array; // holds objects in grid
    final int rows;  // number of rows in grid
    final int cols;  // number of cols in grid
    final int conNum; // number of connections for each point on grid (either 4 or 8)
    private final int length; // total number of objects in grid - maybe just get this from ArrayList

    WrapGrid(int row, int col, int numCon) {
        rows = row;
        cols = col;
        length = rows * cols;
        conNum = numCon;
        array = new ArrayList<T>(length);
    }

    int getLen() {
        return length;
    }

    int getNumRows() {
        return rows;
    }

    int getNumCols() {
        return cols;
    }

    void add(T t) {
        array.add(t);
    }

    // sets object i in flattened out array
    public void set(int i, T t) {
        array.set(i, t);
    }

    // returns object i in flattened out array
    // for faster access when user just needs to iterate through all objects
    // in grid without respect to position in 2D grid
    public T get(int i) {
        return array.get(i);
    }

    // returns the row position of i in grid - adjusted for wrap around edges
    private int modRow(int i) {
        return getPos(i, rows);
    }

    // returns the column position of j in grid - adjusted for wrap around edges
    private int modCol(int j) {
        return getPos(j, cols);
    }

    private int getPos(int idx, int total) {
        if (idx < 0) {
            return idx + total;
        } else if (idx >= total) {
            return idx % total;
        } else return idx;
    }

    // sets object at (i,j) value from store adjusted for wrap around edges
    public void set(int i, int j, T t) {
        array.set(modRow(i) * cols + modCol(j), t);
    }

    // gets object at (i,j) value from store adjusted for wrap around edges
    T get(int i, int j) {
        return array.get(modRow(i) * cols + modCol(j));
    }

    // returns distance on the grid between two objects at (y1,x1) and (y2,x2)
    public int dist(int y1, int x1, int y2, int x2) {
        int y = distFirst(y1, y2);
        int x = distSecond(x1, x2);
        if (conNum == 4) // taxicab distance
        {
            return y + x;
        } else { //if(conNum == 8) supremum distance
            return Math.max(y, x);
        }
    }

    // returns distance on the grid between the first coordinates y1 & y2 of two objects
    private int distFirst(int y1, int y2) {
        int dist = Math.abs(modRow(y2) - modRow(y1));
        return Math.min(dist, rows - dist);
    }

    // returns distance on the grid between the second coordinates x1 & x2 of two objects
    private int distSecond(int x1, int x2) {
        int dist = Math.abs(modCol(x2) - modCol(x1));
        return Math.min(dist, cols - dist);
    }

}
