package object;

import java.io.Serializable;
import java.util.ArrayList;

public class Room implements Serializable {
    public String getField(int nameIndex) {
        return playerFields.get(nameIndex);
    }

    public String getOwner() {
        return owner;
    }

    public int getConnectedCount() {
        return connectedCount;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ArrayList<String> getPlayerFields() {
        return playerFields;
    }

    public void setConnectedCount(int connectedCount) {
        this.connectedCount = connectedCount;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public void setPlayerFields(ArrayList<String> playerFields) {
        this.playerFields = playerFields;
    }
    private String owner;
    private int connectedCount;
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> playerFields = new ArrayList<>();

    public Room() {
        connectedCount = 0;
    }

    public void newConnection(String name) {
        if (names.size() == 0) {
            owner = name;
        }
        names.add(name);
        playerFields.add("");
        connectedCount += 1;
    }


    public ArrayList<String> getNames() {
        return names;
    }

    public String submitField(String name, String field) {
        playerFields.set(names.indexOf(name), field);
        System.out.println(name + " " + field);
        if (!playerFields.get(1 - names.indexOf(name)).equals("")) {
            return "Ready";
        }
        return "Not rdy";
    }


}
