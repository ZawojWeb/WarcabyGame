package com.client.controllers;

import com.client.ClientCore;
import com.client.game.ChineseCheckersBoard;
import com.client.game.ChineseCheckersBoardAdapter;
import com.client.game.ChineseCheckersBoardBuilder;

import com.client.game.MouseMoveHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class GameViewController {

    int playerCount;
    int playerNumber;
    private MouseMoveHandler mmh;

    @FXML
    public StackPane gameBoard;
    @FXML
    public Button skipRound;
    static Group group = new Group();

    @FXML
    public void initialize() {
        ClientCore.getInstance().setGameController(this);
    }

    public void startGameView(int playerCount, int playerNumber){
        this.playerCount = playerCount;
        this.playerNumber = playerNumber;
        gameBoard.getChildren().add(gameBoardLoader());
        mmh.setPlayerNumber(playerNumber);
        gameBoard.setAlignment(Pos.CENTER);
    }

    @FXML
    public void skipRound() {
        System.out.println("Skip round");
    }

    public Group gameBoardLoader() {
        try {
            ChineseCheckersBoard board = new ChineseCheckersBoardBuilder().setSize(5).setNumberOfPlayers(playerCount).build();
            ChineseCheckersBoardAdapter BoardAdapter = new ChineseCheckersBoardAdapter(board);
            mmh = BoardAdapter.getMouseMoveHandler();

            com.client.game.Field[][] fields = BoardAdapter.getFields();

            for (com.client.game.Field[] a : fields) {
                for (com.client.game.Field c : a) {
                    if (c != null)
                        group.getChildren().add(c);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return group;
    }

    public MouseMoveHandler getMouseMoveHandler() {
        return mmh;
    }
}
