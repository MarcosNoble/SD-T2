import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;


public class UserChat2 extends UnicastRemoteObject implements IUserChat, ActionListener {
//    JFrame frame = new JFrame("Chatter");
//    JTextField textField = new JTextField(50);
//    JTextArea messageArea = new JTextArea(16, 50);
    static String userName;
    public ArrayList<String> roomList;


    private JComboBox<String> roomComboBox;

    // implement the ActionListener interface
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == roomComboBox) {
            String selectedRoom = (String) roomComboBox.getSelectedItem();
            // do something with the selected room
        }
    }

    // implement the IUserChat interface
    @Override
    public void deliverMsg(String senderName, String msg) throws RemoteException {
        // do something with the message
    }

    // method to create the GUI with the room list
    public void createGUI() {
        JFrame frame = new JFrame("Chat Room");
        roomComboBox = new JComboBox<>(roomList.toArray(new String[0]));
        roomComboBox.addActionListener(this);
        frame.add(roomComboBox);
        frame.pack();
        frame.setVisible(true);
    }

    // method to add a new room to the list
    public void addRoom(String roomName) {
        roomList.add(roomName);
        roomComboBox.addItem(roomName);
    }

    // method to remove a room from the list
    public void removeRoom(String roomName) {
        roomList.remove(roomName);
        roomComboBox.removeItem(roomName);
    }
    /////





    public UserChat2()throws RemoteException{
        userName = "placeholder";
//        textField.setEditable(false);
//        messageArea.setEditable(false);
//        frame.getContentPane().add(textField, BorderLayout.SOUTH);
//        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
//        frame.pack();

        // Send on enter then clear to prepare for next message
//        textField.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                //out.println(textField.getText());
//                textField.setText("");
//            }
//        });
    }

    private void run() throws IOException{

    }

    public static void main(String[] args) {
        try {
            UserChat2 user = new UserChat2();
            ServerChat serverStub = (ServerChat) Naming.lookup("rmi://localhost:2020/server");
            //RoomChat roomStub = (RoomChat) Naming.lookup("rmi://localhost:2020/rooms");
            user.roomList = serverStub.getRooms();
            if(user.roomList == null){
                serverStub.createRoom("placeholder");
                RoomChat roomStub = (RoomChat) Naming.lookup("rmi://localhost:2020/rooms");
                roomStub.joinRoom(userName, user);
            }else{
                System.out.println(user.roomList);
                RoomChat roomStub = (RoomChat) Naming.lookup("rmi://localhost:2020/rooms");
                roomStub.joinRoom(userName, user);
            }
            try {
                user.run();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
//            user.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//            user.frame.setVisible(true);


        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }



    }
}
