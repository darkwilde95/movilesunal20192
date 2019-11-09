package co.edu.unal.tictactoe;

import java.util.Random;

public class Room {
    public String roomId;
    public String creatorPlayer;
    public String guestPlayer;
    public int move;
    public boolean open;
    public boolean turn; //true -> creator, false -> guest

    public Room () {}

    public Room(String roomId, String creatorPlayer) {
        this.roomId = roomId;
        this.creatorPlayer = creatorPlayer;
        this.guestPlayer = null;
        this.move = -1;
        this.open = true;
        this.turn = new Random().nextBoolean();
    }

    public void nextTurn() {
        this.turn = !this.turn;
    }
}
