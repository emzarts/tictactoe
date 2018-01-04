import java.util.Arrays;

public class Board {

    private String[][] board = new String[3][3];
    private String status = "Tie";

    Board() {
        for (int i = 0; i < this.board.length; i++) {
            for (int j = 0; j < this.board.length; j++) {
                this.board[i][j] = "-";
            }
        }
    }

    public boolean isValidMove(int row, int col) {
        return (row < board.length && col < board.length && board[row][col].equals("-"));
    }

    public void makeMove(int row, int col, String s) {
        if (isValidMove(row,col))
            this.board[row][col] = s;
    }

    public String getTile(int row, int col) {
        return board[row][col];
    }
    public String getStatus() {
        String[] l = new String[9];
        boolean tie = true;
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                l[count] = board[i][j];
                count++;
                if (board[i][j].equals("-")) tie = false;
            }
        }

        // diagonal from 0,0
        if (l[0].equals("X") && l[4].equals("X") && l[8].equals("X")) return "X";
        if (l[0].equals("O") && l[4].equals("O") && l[8].equals("O")) return "O";
        // diagonal from 0, 2
        if (l[2].equals("X") && l[4].equals("X") && l[6].equals("X")) return "X";
        if (l[2].equals("O") && l[4].equals("O") && l[6].equals("O")) return "O";
        // first row
        if (l[0].equals("X") && l[1].equals("X") && l[2].equals("X")) return "X";
        if (l[0].equals("O") && l[1].equals("O") && l[2].equals("O")) return "O";
        // second row
        if (l[3].equals("X") && l[4].equals("X") && l[5].equals("X")) return "X";
        if (l[3].equals("O") && l[4].equals("O") && l[5].equals("O")) return "O";
        // third row
        if (l[6].equals("X") && l[7].equals("X") && l[8].equals("X")) return "X";
        if (l[6].equals("O") && l[7].equals("O") && l[8].equals("O")) return "O";
        // first column
        if (l[0].equals("X") && l[3].equals("X") && l[6].equals("X")) return "X";
        if (l[0].equals("O") && l[3].equals("O") && l[6].equals("O")) return "O";
        // second column
        if (l[1].equals("X") && l[4].equals("X") && l[7].equals("X")) return "X";
        if (l[1].equals("O") && l[4].equals("O") && l[7].equals("O")) return "O";
        // third column
        if (l[2].equals("X") && l[5].equals("X") && l[8].equals("X")) return "X";
        if (l[2].equals("O") && l[5].equals("O") && l[8].equals("O")) return "O";

        if (tie) return "tie";
        return "NONE";
    }

    @Override
    public String toString() {
        return Arrays.toString(board[0]) + "\n" + Arrays.toString(board[1]) + "\n" + Arrays.toString(board[2]);
    }
}
