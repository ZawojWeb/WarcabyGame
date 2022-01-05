package com.client;
import com.client.controllers.DashboardController;
import com.messages.*;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class ConnectionListener extends Thread{
    ObjectInputStream in;
    ObjectOutputStream out;
    MessageHolder currentMessage;
    public boolean creatingGame = false;

    public ConnectionListener(Socket clientSocket) throws Exception{
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }
    @Override
    public void run() {
        while(true) {
            try {
                currentMessage = (MessageHolder) in.readObject();
                messageHandler(currentMessage);
            } catch (Exception exception) {
                break;
            }
        }
    }
    public void close(){
        try {
            out.close();
            in.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void messageHandler(MessageHolder message){
        switch (message.getMessageType()) {
            case "Registered" -> ClientCore.getInstance().getRegisteryController().accountCreatedSuccesfullyNotification();
            case "Register failed" -> ClientCore.getInstance().getRegisteryController().errorNotification();
            case "Logged in" -> {
                RegisterMessage rm = (RegisterMessage) message;
                ClientCore.getInstance().setLogin(rm.getLogin());
                ClientCore.getInstance().setAvatar(rm.getAvatar());
                ClientCore.getInstance().getLoginIntoLauncherController().LoadDashboardScene();
            }
            case "Login fail" -> ClientCore.getInstance().getLoginIntoLauncherController().ErrorNotification();
            case "LobbyInfo" -> {
                LobbyInfoMessage lm = (LobbyInfoMessage) message;
                ClientCore.getInstance().setLobbyInfo(lm);
                ClientCore.getInstance().getDashboardController().LoadLobby();
            }
            case "lobby list info" -> {
                LobbyListMessage llm = (LobbyListMessage) message;
                LinkedList<dummyLobbyClass> lobbys = llm.getLobbys();
                ClientCore.getInstance().getDashboardController().changeLobbyList(lobbys);
                ClientCore.getInstance().getDashboardController().initDashboardGames();
            }
            case "gameBeginning" -> {
                creatingGame = true;
                gameBeginningMessage gbm = (gameBeginningMessage) message;
                try {
                    ClientCore.getInstance().getLobbyController().loadGameScene(gbm.getPlayerCount(), gbm.getPlayerNumber());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            case "your turn" -> {
                ClientCore.getInstance().myTurn = true;
            }
            case "move" -> {
                MoveMessage mm = (MoveMessage) message;
                ClientCore.getInstance().getGameController().getMouseMoveHandler().executeMove(mm.getPawnX(), mm.getPawnY(), mm.getMoveX(), mm.getMoveY());
            }
        }
    }

    public ObjectOutputStream getOut() {
        return out;
    }
}
