import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class PTUI_Client {

    private PrintWriter out;
    private BufferedReader in;
    private Board board;
    private String me;

    private PTUI_Client(String host, int port, String me) throws IOException {
        Socket socket = new Socket(host,port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        this.board = new Board();
        this.me = me;
    }

    private void run() throws IOException {
        if (in.readLine().equals(Protocol.CONNECTED))
            System.out.println("Connection successful\n" + board);
        else System.exit(0);

        if (me.equals("X")) getMove();

        String line;
        while ((line = in.readLine()) != null) {
            switch(line) {
                case Protocol.MOVE_MADE:
                    String move = in.readLine();
                    String[] l = move.split(" ");
                    String p;
                    if (me.equals("X")) p = "O";
                    else p = "X";
                    board.makeMove(Integer.parseInt(l[0]), Integer.parseInt(l[1]), p);
                    System.out.println("\n" + board);
                    System.in.read(new byte[System.in.available()]); // Clears System.in
                    getMove();
                    break;
                case Protocol.GAME_WON:
                    System.out.println("You won!");
                    System.exit(1);
                    break;
                case Protocol.GAME_LOST:
                    System.out.println("You lost :(");
                    System.exit(1);
                    break;
                case Protocol.GAME_TIED:
                    System.out.println("It was a tie");
                    System.exit(1);
                    break;
            }
        }
    }

    private void getMove() {
        Scanner sc = new Scanner(System.in);
        String line;
        boolean cont = true;
        while (cont) {
            System.out.print("Your turn <row> <col>: ");
            line = sc.nextLine();
            String[] l = line.split(" ");
            if (line.equals("q")) System.exit(1);
            if (l.length != 2) System.out.println("<row> <col>");
            else {
                try {
                    if (board.isValidMove(Integer.parseInt(l[0]), Integer.parseInt(l[1]))) {
                        board.makeMove(Integer.parseInt(l[0]), Integer.parseInt(l[1]), me);
                        System.out.println(board);
                        cont = false;
                        out.println(Protocol.MAKE_MOVE);
                        out.println(line);
                    } else System.out.println("That move was not valid, please type <row> <col>");
                } catch (NumberFormatException e) {
                    System.out.println("That move was not valid, please type <row> <col>");
                    cont = false;
                }
            }
        }
    }

    private static void connect(String host, int port, String s) throws IOException {
        System.out.println("Connecting to Tic Tac Toe game on port " + port);
        PTUI_Client client = new PTUI_Client(host, port, s);
        client.run();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to Tic Tac Toe!\nWould you like to:\n\ta) Start a new game\n\tb) Join a game\n\tc) Exit");
        boolean begin = false;
        while (!begin) {
            begin = true;

            String host = "localhost";
            int port = 0;
            String st = "?";
            switch (sc.nextLine().toLowerCase()) {
                case "a":
                    try {
                        System.out.print("Start a Tic Tac Toe game on what port? ");
                        port = sc.nextInt();
                        //System.out.print("What host? ");
                        //Scanner scan = new Scanner(System.in);
                        //host = scan.nextLine(); // 192.168.1.11
                        Server s = new Server(port);
                        st = "X";
                        s.start();
                    } catch (InputMismatchException e) {
                        System.out.println("That is not a valid port number");
                        begin = false;
                    } catch (IOException e) {
                        System.out.println("Cannot connect to that port");
                        begin = false;
                    }
                    break;
                case "b":
                    try {
                        System.out.print("Join a Tic Tac Toe game on what port? ");
                        port = sc.nextInt();
                        //System.out.print("What host? ");
                        //Scanner scan = new Scanner(System.in);
                        //host = scan.nextLine(); // 192.168.1.11
                        st = "O";
                    } catch (InputMismatchException e) {
                        System.out.println("That is not a valid port number");
                        begin = false;
                    }
                    break;
                case "c":
                    System.exit(0);
                    break;
                default:
                    begin = false;
                    System.out.println("Please type a, b, or c");
                    break;
            }
            try {
                connect(host, port, st);
            } catch (IOException e) {
                System.out.println("Cannot connect to that port");
                begin = false;
            }
        }
    }
}
