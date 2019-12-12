package server;

import object.Room;
import messagesfromserver.MessageAboutAttack;
import messagesfromserver.NewConnectionToRoom;
import messagesfromserver.NewField;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Connection implements Runnable {
    private DataInputStream in;
    private ObjectOutputStream objectOut;
    private ObjectInputStream objectIn;
    private Socket client;
    private String name;

    public Connection(Socket client) throws IOException {
        this.client = client;

        InputStream is = client.getInputStream();
        this.in = new DataInputStream(is);
        this.objectIn = new ObjectInputStream(is);

        OutputStream os = client.getOutputStream();
        this.objectOut = new ObjectOutputStream(os);
    }
    public String getName() {
        return name;
    }

    public ObjectOutputStream getObjectOut() {
        return objectOut;
    }

    public Socket getSocket() {
        return client;
    }


    @Override
    public void run() {
        while (true) {
            try {
                if (in.available() > 0) {
                    Object input = objectIn.readObject();

                    if (input instanceof Room) {
                        Server.rooms.add((Room)input);
                    }
                    else if (input instanceof NewConnectionToRoom) {
                        Room room = Server.rooms.get(((NewConnectionToRoom)input).getRoomIndex());
                        room.newConnection(((NewConnectionToRoom) input).getName());
                    }
                    else if (input instanceof NewField) {
                        String name = ((NewField) input).getName();
                        String field = ((NewField) input).getField();

                        Room foundRoom = null;
                        int roomIndex = 0;
                        int nameIndex = -1;

                        for (Room room: Server.rooms) {
                            if (foundRoom == null) {
                                for (int i = 0; i < room.getNames().size(); i++) {
                                    if (name.equals(room.getNames().get(i))) {
                                        foundRoom = room;
                                        nameIndex = i;
                                    }
                                }
                                roomIndex++;
                            }
                        }
                        String allReady = Server.rooms.get(roomIndex - 1).submitField(name, field);
                        System.out.println("soo " + allReady);
                        if (allReady.equals("Ready")) {
                            for (Connection conn : Server.clients) {
                                if (conn.getName().equals(foundRoom.getNames().get(1 - nameIndex))) {
                                    conn.getObjectOut().writeObject("Ready");
                                }
                            }
                        }
                        objectOut.writeObject(allReady);
                        System.out.println("written");
                    }
                    else if (input instanceof MessageAboutAttack) {
                        String turnName = ((MessageAboutAttack) input).getTurnName();
                        int attackIndex = ((MessageAboutAttack) input).getAttackIndex();

                        Room foundRoom = null;
                        int nameIndex = -1;

                        for (Room room: Server.rooms) {
                            if (foundRoom == null) {
                                for (int i = 0; i < room.getNames().size(); i++) {
                                    if (turnName.equals(room.getNames().get(i))) {
                                        foundRoom = room;
                                        nameIndex = i;
                                    }
                                }
                            }
                        }

                        String field = foundRoom.getField(1 - nameIndex);
                        if (field.charAt(attackIndex) == '1') {
                            objectOut.writeObject("hit");
                        }
                        else {
                            objectOut.writeObject("miss");
                        }
                        for (Connection conn : Server.clients) {
                            if (conn.getName().equals(foundRoom.getNames().get(1 - nameIndex))) {
                                conn.getObjectOut().writeObject("new hit on " + attackIndex);
                            }
                        }
                    }
                    else if (input instanceof String) {
                        if (input.toString().equals("getRooms()")) {
                            ArrayList<Room> newRooms = new ArrayList<>();
                            for (Room room: Server.rooms) {
                                Room newRoom = new Room();
                                newRoom.setConnectedCount(room.getConnectedCount());
                                newRoom.setNames(room.getNames());
                                newRoom.setOwner(room.getOwner());
                                newRoom.setPlayerFields(room.getPlayerFields());
                                newRooms.add(newRoom);
                            }
                            objectOut.writeObject(newRooms);
                        }
                        else if (input.toString().contains("name:")) {
                            name = input.toString().split(":")[1];
                        }
                        else if (input.toString().contains("dead")) {
                            String name = input.toString().split(" ")[0];
                            int nameIndex = -1;
                            Room foundRoom = null;
                            for (Room room: Server.rooms) {
                                if (foundRoom == null) {
                                    for (int i = 0; i < room.getNames().size(); i++) {
                                        if (name.equals(room.getNames().get(i))) {
                                            foundRoom = room;
                                            nameIndex = i;
                                        }
                                    }
                                }
                            }
                            for (Connection conn : Server.clients) {
                                if (conn.getName().equals(foundRoom.getNames().get(1 - nameIndex))) {
                                    conn.getObjectOut().writeObject("hit");
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    conn.getObjectOut().writeObject("victory");
                                }
                            }
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


}
