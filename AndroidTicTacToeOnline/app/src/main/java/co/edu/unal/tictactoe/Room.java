package co.edu.unal.tictactoe;

import java.util.Random;

public class Room {
    public String roomId;
    public String creatorPlayer;
    public String guestPlayer;
    public int move;
    public boolean open;
    public boolean nextTurn; //true -> creator, false -> guest
    public boolean ready;

    public Room () {}

    public Room(String roomId, String creatorPlayer) {
        this.roomId = roomId;
        this.creatorPlayer = creatorPlayer;
        this.guestPlayer = null;
        this.move = -1;
        this.open = true;
        this.nextTurn = new Random().nextBoolean();
        this.ready = false;
    }

    public void changeTurn() {
        this.nextTurn = !this.nextTurn;
    }
}
