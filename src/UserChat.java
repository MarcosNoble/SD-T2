import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class UserChat extends UnicastRemoteObject implements IUserChat{
//    protected UserChat() throws RemoteException {
//        super();
//    }
    JFrame frame = new JFrame("Chatter");
    JTextField textField = new JTextField(50);
    JTextArea messageArea = new JTextArea(16, 50);
    static String userName;
    public ArrayList<String> roomList;
    public RoomChat roomStub;
    public ServerChat serverStub;
    public UserChat()throws RemoteException{
        userName = "placeholder";
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();

        // Send on enter then clear to prepare for next message
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //out.println(textField.getText());
                textField.setText("");
            }
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    @Override

    public void deliverMsg(String senderName, String msg) throws RemoteException {

    }
//    private void run() throws IOException{
//
//    }

    public void askJoin() throws RemoteException{
        try {
            serverStub = (ServerChat) Naming.lookup("rmi://localhost:2020/server");
            this.roomList = serverStub.getRooms();
            roomStub = (RoomChat) Naming.lookup("rmi://localhost:2020/rooms");
            roomStub.joinRoom(userName, this);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {
            UserChat user = new UserChat();
//            serverStub = (ServerChat) Naming.lookup("rmi://localhost:2020/server");

            //RoomChat roomStub = (RoomChat) Naming.lookup("rmi://localhost:2020/rooms");
//            user.roomList = serverStub.getRooms();
//            if(user.roomList == null){
//                serverStub.createRoom("placeholder");
//            }else{
//                System.out.println(user.roomList);
//                roomStub = (RoomChat) Naming.lookup("rmi://localhost:2020/rooms");
//                roomStub.joinRoom(userName, this);
//            }

//            try {
//                user.run();
//                roomStub
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//
        } catch (/*NotBoundException |*//* MalformedURLException | */RemoteException e) {
            e.printStackTrace();
        }




    }
}
