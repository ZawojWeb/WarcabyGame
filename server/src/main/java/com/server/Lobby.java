package com.server;

import com.messages.LobbyInfoMessage;
import com.messages.MessageHolder;

import java.util.LinkedList;

public class Lobby {
    private String host, name;
    private int numberOfPlayers;
    private final LinkedList<UserCommunicationThread> players;
    Game game;

    public Lobby(){
        numberOfPlayers = 0;
        players = new LinkedList<>();
    }

    public void addPlayer(UserCommunicationThread newPlayer){
        if(numberOfPlayers == 6) return;
        if(numberOfPlayers == 0){
            host = newPlayer.userData.getLogin();
            name = host + "'s game";
        }
        numberOfPlayers++;
        players.add(newPlayer);
        newPlayer.setLobby(this);
        sendLobbyInfo();
    }

    public void removePlayer(UserCommunicationThread playerToRemove){
        if(!players.remove(playerToRemove)) return;
        numberOfPlayers--;
        if(numberOfPlayers==0){
            ServerCore.getInstance().getLobbys().remove(this);
        }else{
            if(playerToRemove.userData.getLogin().equals(host)){
                host = players.get(0).userData.getLogin();
            }
        }

        sendLobbyInfo();
    }

    public void start(){
        try {
            game = new Game(this, numberOfPlayers);
        }catch (Exception ignored){
        }
    }

    private LobbyInfoMessage getLobbyInfo(){
        LobbyInfoMessage info = new LobbyInfoMessage();
        info.setMessageType("LobbyInfo");
        for(UserCommunicationThread uct : players){
            info.getPlayernames().add(uct.userData.getLogin());
            info.getPlayerimages().add(uct.userData.getAvatarNbr());
        }
        info.setGameName(name);
        return info;
    }
    public void sendLobbyInfo(){
        deliverMessages(getLobbyInfo());
    }

    public void deliverMessages(MessageHolder mh){
        for (UserCommunicationThread uct : players) {
            try {
                uct.out.writeObject(mh);
                ServerCore.getInstance().getController().appendOutput(mh.getMessageType());
            }catch(Exception ignored){}
        }
    }
    public String getHost() {
        return host;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfPlayers(){
        return numberOfPlayers;
    }

    public LinkedList<UserCommunicationThread> getPlayers(){
        return players;
    }
}
