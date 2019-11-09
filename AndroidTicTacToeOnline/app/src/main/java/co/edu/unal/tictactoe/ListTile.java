package co.edu.unal.tictactoe;

public class ListTile {
    public String creatorPlayer;
    public String roomId;

    public ListTile(String roomId, String creatorPlayer) {
        this.creatorPlayer = creatorPlayer;
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return this.creatorPlayer;
    }
}
