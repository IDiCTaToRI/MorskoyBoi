package messagesfromserver;

import java.io.Serializable;

public class NewConnectionToRoom implements Serializable {
    private int roomIndex;
    private String name;

    public NewConnectionToRoom(int roomIndex, String name) {
        this.roomIndex = roomIndex;
        this.name = name;
    }

    public int getRoomIndex() {
        return roomIndex;
    }

    public String getName() {
        return name;
    }
}
