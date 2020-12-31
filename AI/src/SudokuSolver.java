import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SudokuSolver {

    public static void main(String[] args) {
        Scanner in = null;
        try {
            in = new Scanner(new File("src/resources/sudoku-"+5+".txt"));
        } catch (FileNotFoundException e) {
           e.printStackTrace();
           System.exit(0);
        }
        //in = new Scanner(System.in);

        // Read input
        int[][] sudoku = new int[9][9];
        for (int i = 0; i < 9; i++) {
            String line = in.nextLine();
            for (int j = 0; j < 9; j++) {
                int number = Integer.parseInt(line.charAt(j) + "");
                if (number > 0) {
                    sudoku[i][j] = number;
                }
            }
        }
        ArrayList<Integer>[][] sudokuCSP = CSPify(sudoku);
        ArrayList<Integer>[][] result = solve(sudokuCSP);
        if (result != null) {
            printSolvedSudoku(sudokuCSP);
        }

    }

    private static ArrayList<Integer>[][] solve(ArrayList<Integer>[][] sudokuCSP) {
        sudokuAC(sudokuCSP);
        if (isSolved(sudokuCSP)) {
            return sudokuCSP;
        }
        if (isUnsolvable(sudokuCSP)) {
            return null;
        }
        int var = select(sudokuCSP);
        int i = var / 9;
        int j = var % 9;
        ArrayList<Integer>[][] copy = copy(sudokuCSP);
        for (int value : sudokuCSP[i][j]) {

            copy[i][j].clear();
            if(areTheSame(sudokuCSP, copy, var)) {
                System.out.println("same");
            }
            copy[i][j].add(value);
            ArrayList<Integer>[][] result = solve(copy);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private static boolean areTheSame(ArrayList<Integer>[][] sudokuCSP, ArrayList<Integer>[][] copy, int var) {
        for(int i = 0; i < sudokuCSP.length; i++) {
            for(int j = 0; j < sudokuCSP[i].length; j++) {
                if(i * 9 + j != var) {
                    for (int k = 0; k < sudokuCSP[i][j].size(); k++) {
                        if(sudokuCSP[i][j].get(k) != copy[i][j].get(k)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private static ArrayList<Integer>[][] copy(ArrayList<Integer>[][] sudokuCSP) {
        ArrayList<Integer>[][] copy = new ArrayList[sudokuCSP.length][sudokuCSP[0].length];
        for (int i = 0; i < sudokuCSP.length; i++) {
            for (int j = 0; j < sudokuCSP[i].length; j++) {
                copy[i][j] = new ArrayList<>();
                for (int k = 0; k < sudokuCSP[i][j].size(); k++) {
                    copy[i][j].add(sudokuCSP[i][j].get(k));
                }
            }
        }
        return copy;
    }

    private static int select(ArrayList<Integer>[][] sudokuCSP) {
        int minSize = Integer.MAX_VALUE;
        int minVar = -1;
        for (int i = 0; i < sudokuCSP.length; i++) {
            for (int j = 0; j < sudokuCSP[i].length; j++) {
                if (sudokuCSP[i][j].size() > 1 && sudokuCSP[i][j].size() < minSize) {
                    minSize = sudokuCSP[i][j].size();
                    minVar = i * 9 + j;
                }
            }
        }

        return minVar;
    }

    private static boolean isUnsolvable(ArrayList<Integer>[][] sudokuCSP) {
        for (int i = 0; i < sudokuCSP.length; i++) {
            for (int j = 0; j < sudokuCSP[i].length; j++) {
                if (sudokuCSP[i][j].size() < 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isSolved(ArrayList<Integer>[][] sudokuCSP) {
        for (int i = 0; i < sudokuCSP.length; i++) {
            for (int j = 0; j < sudokuCSP[i].length; j++) {
                if (sudokuCSP[i][j].size() != 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void sudokuAC(ArrayList<Integer>[][] sudokuCSP) {
        ArrayList<Arc> queue = initializeArcQueue(sudokuCSP);
        while (!queue.isEmpty()) {
            Arc arc = queue.remove(0);
            if (removeInconsistentValues(arc, sudokuCSP)) {
                ArrayList<Integer> neighbors = getNeighbors(arc.var1);
                for (int i = 0; i < neighbors.size(); i++) {
                    queue.add(new Arc(neighbors.get(i), arc.var1));
                }
            }
        }

    }

    private static boolean removeInconsistentValues(Arc arc, ArrayList<Integer>[][] sudokuCSP) {
        boolean removed = false;
        int var1Row = arc.var1 / 9;
        int var1Column = arc.var1 % 9;
        int var2Row = arc.var2 / 9;
        int var2Column = arc.var2 % 9;
        for (int i = 0; i < sudokuCSP[var1Row][var1Column].size(); i++) {
            int x = sudokuCSP[var1Row][var1Column].get(i);
            if (sudokuCSP[var2Row][var2Column].contains(x) && sudokuCSP[var2Row][var2Column].size() == 1) {
                sudokuCSP[var1Row][var1Column].remove(Integer.valueOf(x));
                removed = true;
                i--;
            }
        }
        return removed;
    }

    private static ArrayList<Arc> initializeArcQueue(ArrayList<Integer>[][] sudokuCSP) {
        ArrayList<Arc> initialQueue = new ArrayList<>();
        for (int i = 0; i < sudokuCSP.length; i++) {
            for (int j = 0; j < sudokuCSP[i].length; j++) {
                int varNumber = i * 9 + j;
                ArrayList<Integer> neighbors = getNeighbors(i * 9 + j);
                for (int k = 0; k < neighbors.size(); k++) {
                    Arc arc = new Arc(varNumber, neighbors.get(k));
                    initialQueue.add(arc);
                }
            }
        }
        return initialQueue;
    }

    // evaluates the given sudoku snapshot and creates a CSP formulation out of it.
    private static ArrayList[][] CSPify(int[][] sudoku) {
        // Initialize constraint pools
        ArrayList[] rows = new ArrayList[9];
        ArrayList[] columns = new ArrayList[9];
        ArrayList[] blocks = new ArrayList[9];
        for (int i = 0; i < 9; i++) {
            rows[i] = new ArrayList<Integer>();
            columns[i] = new ArrayList<Integer>();
            blocks[i] = new ArrayList<Integer>();
        }

        // fill row & column constraint pools
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudoku[i][j] > 0) {
                    rows[i].add(sudoku[i][j]);
                }
                if (sudoku[j][i] > 0) {
                    columns[i].add(sudoku[j][i]);
                }
            }
        }
        // fill block constraint pool
        for (int i = 0; i < 3; i++) { // for each block
            for (int j = 0; j < 3; j++) {
                int startRow = i * 3;
                int startColumn = j * 3;
                // save the numbers for this block
                for (int m = startRow; m < startRow + 3; m++) {
                    for (int n = startColumn; n < startColumn + 3; n++) {
                        if (sudoku[m][n] > 0) {
                            blocks[i * 3 + j].add(sudoku[m][n]);
                        }
                    }
                }
            }
        }


        // add sets of possible values to 2-dimensional set array
        ArrayList<Integer>[][] sudokuCSP = new ArrayList[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                sudokuCSP[i][j] = new ArrayList();
                if (sudoku[i][j] == 0) {
                    for (int k = 1; k <= 9; k++) {
                        if (!rows[i].contains(k) && !columns[j].contains(k) && !blocks[mapIndicesToBlockIndex(i, j)].contains(k)) {
                            sudokuCSP[i][j].add(k);
                        }
                    }
                } else {
                    sudokuCSP[i][j].add(sudoku[i][j]);
                }
            }
        }
        return sudokuCSP;

    }

    private static int mapIndicesToBlockIndex(int i, int j) {
        if (i < 3 && j < 3) {
            return 0;
        } else if (i < 3 && j < 6) {
            return 1;
        } else if (i < 3 && j < 9) {
            return 2;
        } else if (i < 6 && j < 3) {
            return 3;
        } else if (i < 6 && j < 6) {
            return 4;
        } else if (i < 6 && j < 9) {
            return 5;
        } else if (i < 9 && j < 3) {
            return 6;
        } else if (i < 9 && j < 6) {
            return 7;
        } else {
            return 8;
        }
    }

    // represents an Arc - a dependency between two variables
    static class Arc {
        int var1; // the first variable
        int var2; // the second variable

        public Arc(int var1, int var2) {
            this.var1 = var1;
            this.var2 = var2;
        }
    }

    // returns a list of Integers - each integer represents one cell in the sudoku:
    // 0 -> first row first cell
    // 9 -> second row first cell
    // etc
    private static ArrayList<Integer> getNeighbors(int var) {
        ArrayList<Integer> neighbors = new ArrayList<>();
        // retrieve correct array indices
        int i = var / 9;
        int j = var % 9;

        // get all row neighbors - dont get confused, you need to iterate through the columns to get the row neighbors
        for (int column = 0; column < 9; column++) {
            if (column != j && !neighbors.contains(i * 9 + column)) {
                neighbors.add(i * 9 + column);
            }
        }

        // get all column neighbors - dont get confused, you need to iterate through the rows to get the column neighbors
        for (int row = 0; row < 9; row++) {
            if (row != i && !neighbors.contains(row * 9 + j)) {
                neighbors.add(row * 9 + j);
            }
        }

        // get all block neighbors
        int startRow = (i / 3) * 3; //makes sense because of integer division
        int startColumn = (j / 3) * 3;
        for (int row = startRow; row < startRow + 3; row++) {
            for (int column = startColumn; column < startColumn + 3; column++) {
                if (row != i && column != j && !neighbors.contains(row * 9 + column)) {
                    neighbors.add(row * 9 + column);
                }
            }
        }

        return neighbors;
    }

    private static void printSudokuCSP(ArrayList[][] sudoku) {
        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku[i].length; j++) {
                System.out.print("{");
                for (int k = 0; k < sudoku[i][j].size(); k++) {
                    System.out.print(sudoku[i][j].get(k));
                    if (k != sudoku[i][j].size() - 1) {
                        System.out.print(",");
                    }
                }
                System.out.print("}");
            }
            System.out.println();
        }
    }

    private static void printSolvedSudoku(ArrayList[][] sudoku) {
        for (int i = 0; i < sudoku.length; i++) {
            for (int j = 0; j < sudoku[i].length; j++) {
                System.out.print(sudoku[i][j].get(0));
            }
            System.out.println();
        }
    }
}
