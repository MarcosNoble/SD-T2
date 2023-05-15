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
    }
    @Override

    public void deliverMsg(String senderName, String msg) throws RemoteException {

    }
    private void run() throws IOException{

    }

    public static void main(String[] args) {
        try {
            UserChat user = new UserChat();
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
            user.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            user.frame.setVisible(true);

        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }



    }
}
