import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TicTacGUI {

    private PrintWriter out;
    private BufferedReader in;
    private Board board;
    private String me;
    private Stage stage;
    private Scene sc;
    private Button[] buttons = new Button[9];
    private Socket socket;

    public TicTacGUI(String host, int port, String me, Stage stage, Scene sc) throws IOException {
        this.socket = new Socket(host,port);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        this.board = new Board();
        this.me = me;
        this.stage = stage;
        this.sc = sc;
    }

    public void run() {
        System.out.println("RUNNNNN"); //TODO
        BorderPane w = new BorderPane();
        w.setStyle("-fx-background-color: transparent;");
        Text txt = new Text("Waiting for player two...");
        txt.setFill(Color.GRAY);
        txt.setFont(Font.font("Monospaced", 20));
        w.setTop(txt);
        Scene waiting = new Scene(w, 300, 300, Color.WHITE);
        stage.setScene(waiting);
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: transparent");
        final Group ro = (Group) sc.getRoot();
        GridPane grid = (GridPane) ro.getChildren().get(0);
        root.setCenter(grid);
        root.setBottom(new Text("Connecting. . ."));
        Scene s = new Scene(root,300, 310, Color.WHITE);
        Runnable r1 = new Runnable() {
            public void run() {
                try {
                    if (in.readLine().equals(Protocol.CONNECTED))
                        Platform.runLater(()->stage.setScene(s));
                    else System.exit(0);

                } catch (IOException e) {
                    System.out.println("something went wrong");
                }
            }
        };
        Thread t1 = new Thread(r1);
        t1.start();

        root.setBottom(new Text("Waiting for opponent. . ."));
        root.setDisable(true);
        if (me.equals("X")) {
            root.setBottom(new Text("Your turn. . ."));
            root.setDisable(false);
        }


        int i = 0;
        for (Node n : grid.getChildren()) {
            Button b = (Button) n;
            b.setOnAction(e -> {
                int[] data = (int[]) b.getUserData();
                if (board.isValidMove(data[1], data[0])) {
                    board.makeMove(data[1], data[0], me);
                    out.println(Protocol.MAKE_MOVE);
                    out.println(data[1] + " " + data[0]);
                    b.setText(me);
                    root.setBottom(new Text("Waiting for opponent. . ."));
                    root.setDisable(true);
                }
            });
            buttons[i] = b;
            i++;
        }

        Runnable r = new Runnable() {
            public void run() {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        switch (line) {
                            case Protocol.MOVE_MADE:
                                System.out.println("MOVE MADE"); //TODO
                                String move = in.readLine();
                                System.out.println("MOVE " + move); // TODO
                                String[] l = move.split(" ");
                                String p;
                                if (me.equals("X")) p = "O";
                                else p = "X";
                                board.makeMove(Integer.parseInt(l[0]), Integer.parseInt(l[1]), p);
                                Platform.runLater(() -> refresh(Integer.parseInt(l[0]), Integer.parseInt(l[1])));
                                root.setDisable(false);
                                Platform.runLater(() -> root.setBottom(new Text("Your turn. . .")));
                                System.in.read(new byte[System.in.available()]); // Clears System.in
                                System.out.println("DONE WITH  MOVE");
                                break;
                            case Protocol.GAME_WON:
                                Platform.runLater(() ->root.setBottom(new Text("You won!")));
                                root.setDisable(true);
                                break;
                            case Protocol.GAME_LOST:
                                in.close();
                                socket.close();
                                root.setDisable(true);
                                Platform.runLater(() -> root.setBottom(new Text("You lost :(")));
                                break;
                            case Protocol.GAME_TIED:
                                Platform.runLater(() ->root.setBottom(new Text("It was a tie")));
                                root.setDisable(true);
                                break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("game ended");
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
    }

    private void refresh(int row, int col) {
        buttons[(col * 3) + row].setText(this.board.getTile(row, col));
    }
}
