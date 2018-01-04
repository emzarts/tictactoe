import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

// Todo add a cancel button
// Todo help button/ menu
// Todo test stuff third move crashes??
// Todo both start a new game on the same port... BAD
// Todo fix when one client closes the other crashes

public class GUI_Client extends Application {

    @Override
    public void start(Stage s) {

        // Set up the button grid representing the tic tac toe board
        GridPane board = new GridPane();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button btn = new Button();
                btn.setStyle("-fx-border-color: Lightgray; -fx-text-fill: Gray; -fx-border-width: 1; -fx-background-color: transparent; -fx-font-size: 80; -fx-font-family: Monospaced;");
                btn.setMaxSize(100, 100);
                btn.setPrefSize(100,100);
                btn.setPadding(new Insets(0));
                btn.setUserData(new int[] {i, j});
                board.add(btn, i, j);
            }
        }
        final Group root = new Group(board);
        final Scene scene = new Scene(root, 300, 300, Color.WHITE);

        // sets up port screen

        // start new game
        BorderPane spo = new BorderPane();
        spo.setStyle("-fx-background-color: transparent");
        TextField txtport = new TextField();
        txtport.setOnAction(e -> {
            try {
                System.out.println("start a new game");
                int port = Integer.parseInt(txtport.getText());
                Server server = new Server(port);
                server.start();
                connect("localhost", port, "X", s, scene);
            } catch (IOException| NumberFormatException m) {
                System.out.println("something bad happened");
            }
        });
        Text st = new Text("Start game on what port? ");
        st.setFont(Font.font("Monospaced", 20));
        st.setFill(Color.GRAY);
        txtport.setStyle("-fx-border-width: 1; -fx-border-color: Gray; -fx-background-color: transparent;");
        VBox form = new VBox(st, txtport);
        spo.setCenter(form);

        // join a game
        TextField txtport2 = new TextField();
        txtport2.setOnAction(e -> {
                    try {
                        connect("localhost", Integer.parseInt(txtport2.getText()), "O", s, scene);
                    } catch (IOException m) {
                        System.out.println("something bad happened");
                    }
                });
        BorderPane jpo = new BorderPane();
        jpo.setStyle("-fx-background-color: transparent");
        Text st2 = new Text("Join game on what port? ");
        st2.setFont(Font.font("Monospaced", 20));
        st2.setFill(Color.GRAY);
        txtport2.setStyle("-fx-border-width: 1; -fx-border-color: Gray; -fx-background-color: transparent;");
        VBox formj = new VBox(st2, txtport2);
        jpo.setCenter(formj);
        final Scene startportscreen = new Scene(spo, 300, 300, Color.WHITE);
        final Scene joinportscreen  = new Scene(jpo, 300, 300, Color.WHITE);

        // Sets up the game initialization page
        Button start = new Button("Start a new game");
        start.setStyle("-fx-text-fill: Gray; -fx-border-width: 1; -fx-background-color: transparent; -fx-font-size: 21; -fx-font-family: Monospaced;");
        start.setPrefSize(300, 150);
        Button join = new Button("Join an existing game");
        join.setStyle("-fx-text-fill: Gray; -fx-border-width: 1; -fx-background-color: transparent; -fx-font-size: 21; -fx-font-family: Monospaced;");
        join.setPrefSize(300, 150);

        start.setOnMouseEntered(e -> start.setStyle("-fx-cursor: hand; -fx-underline: true; -fx-text-fill: Gray; -fx-border-width: 1; -fx-background-color: transparent; -fx-font-size: 21; -fx-font-family: Monospaced;"));
        start.setOnMouseExited(e -> start.setStyle("-fx-text-fill: Gray; -fx-border-width: 1; -fx-background-color: transparent; -fx-font-size: 21; -fx-font-family: Monospaced;"));

        join.setOnMouseEntered(e -> join.setStyle("-fx-cursor: hand; -fx-underline: true; -fx-text-fill: Gray; -fx-border-width: 1; -fx-background-color: transparent; -fx-font-size: 21; -fx-font-family: Monospaced;"));
        join.setOnMouseExited(e -> join.setStyle("-fx-text-fill: Gray; -fx-border-width: 1; -fx-background-color: transparent; -fx-font-size: 21; -fx-font-family: Monospaced;"));

        start.setOnMousePressed(e -> s.setScene(startportscreen));
        join.setOnMousePressed(e -> s.setScene(joinportscreen));

        VBox options = new VBox(start, join);
        Group ls = new Group(options);
        final Scene loginscreen = new Scene(ls, 300, 300, Color.GREEN);

        s.setTitle("Tic Tac Toe");
        s.getIcons().add(new Image("file:icons/icon1.png"));
        s.setScene(loginscreen);
        s.show();
    }

    private static void connect(String host, int port, String s, Stage stage, Scene sc) throws IOException {
        TicTacGUI client = new TicTacGUI(host, port, s, stage, sc);
        client.run();
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
