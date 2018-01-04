import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private ServerSocket server;
    private Board board;

    Server(int port) throws IOException{
        this.server = new ServerSocket(port);
        this.board = new Board();
    }

    public void run() {
        try {
            Socket client = server.accept();
            PrintWriter out =
                    new PrintWriter(client.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(client.getInputStream()));
            System.out.println("Waiting for player two...");

            Socket client2 = server.accept();
            PrintWriter out2 =
                    new PrintWriter(client2.getOutputStream(), true);
            BufferedReader in2 = new BufferedReader(
                    new InputStreamReader(client2.getInputStream()));
            System.out.println("Player two connected");

            // Server sends message to the clients to confirm their connection
            out.println(Protocol.CONNECTED);
            out2.println(Protocol.CONNECTED);

            // Thread for Client 1
            Thread t1 = new Thread() {
                public void run() {
                    String line;
                    try {
                        while ((line = in.readLine()) != null) {
                            if (line.equals(Protocol.MAKE_MOVE)) {
                                String move = in.readLine();
                                String[] l = move.split(" ");
                                board.makeMove(Integer.parseInt(l[0]), Integer.parseInt(l[1]), "X");
                                out2.println(Protocol.MOVE_MADE);
                                out2.println(move);
                                switch(board.getStatus()) {
                                    case "X":
                                        out2.println(Protocol.GAME_LOST);
                                        out.println(Protocol.GAME_WON);
                                        break;
                                    case "O":
                                        out2.println(Protocol.GAME_WON);
                                        out.println(Protocol.GAME_LOST);
                                        break;
                                    case "tie":
                                        out2.println(Protocol.GAME_TIED);
                                        out.println(Protocol.GAME_TIED);
                                        break;
                                }

                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(0);
                    }
                }

            };
            t1.start();

            // Thread for Client 2
            Thread t2 = new Thread() {
                public void run() {
                    String line;
                    try {
                        while ((line = in2.readLine()) != null) {
                            if (line.equals(Protocol.MAKE_MOVE)) {
                                String move = in2.readLine();
                                String[] l = move.split(" ");
                                board.makeMove(Integer.parseInt(l[0]), Integer.parseInt(l[1]), "O");
                                switch(board.getStatus()) {
                                    case "X":
                                        out2.println(Protocol.GAME_LOST);
                                        out.println(Protocol.GAME_WON);
                                        break;
                                    case "O":
                                        out2.println(Protocol.GAME_WON);
                                        out.println(Protocol.GAME_LOST);
                                        break;
                                    case "tie":
                                        out2.println(Protocol.GAME_TIED);
                                        out.println(Protocol.GAME_TIED);
                                        break;
                                }
                                out.println(Protocol.MOVE_MADE);
                                out.println(move);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(0);
                    }
                }
            };
            t2.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void onDisconnect(Event event) {
        System.out.println("Client disconnected");
    }
}
