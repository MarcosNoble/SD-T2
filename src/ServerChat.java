import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


public class ServerChat extends UnicastRemoteObject implements IServerChat, ActionListener {
    public static ArrayList<String> roomList;
    private final JList<String> list;
    private JButton  closeRoomButton;
    private JFrame frame;
    private JTextPane messageArea;
    public  IRoomChat roomStub;

    protected ServerChat() throws RemoteException {
        this.roomList = new ArrayList<>();

        list = new JList<String>(roomList.toArray(new String[0]));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(list);
        closeRoomButton = new JButton("Fechar sala");
        closeRoomButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        buttonPanel.add(closeRoomButton);
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.add(listScrollPane, BorderLayout.CENTER);
        optionsPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(optionsPanel, BorderLayout.WEST);

        messageArea = new JTextPane();
        messageArea.setEditable(false);
        messageArea.setPreferredSize(new Dimension(300, 200));
        JScrollPane textScrollPane = new JScrollPane(messageArea);

        mainPanel.add(textScrollPane, BorderLayout.CENTER);
        frame = new JFrame();
        frame.setTitle("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public ArrayList<String> getRooms() throws RemoteException {
        return this.roomList;
    }

    @Override
    public void createRoom(String roomName) throws RemoteException {
        try {
            Naming.rebind("rmi://localhost:2020/"+roomName, new RoomChat(roomName));
            roomList.add(roomName);
            refreshList();
            appendToMessageArea("Sala "+ roomName + " criada.");
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void appendToMessageArea(String message) {
            messageArea.setText(messageArea.getText() + message+ "\n");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeRoomButton) {
            String selectedOption = list.getSelectedValue();
            if (selectedOption == null) {
                JOptionPane.showMessageDialog(frame, "Escolha uma sala");
            } else {
                try {
                    roomStub = (IRoomChat) Naming.lookup("rmi://localhost:2020/"+selectedOption);
                    roomList.remove(selectedOption);
                    roomStub.closeRoom();
                    Naming.unbind("rmi://localhost:2020/"+selectedOption);
                    refreshList();
                    appendToMessageArea("Sala "+ selectedOption +" fechada.");
                } catch (RemoteException| MalformedURLException|NotBoundException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void refreshList(){
            list.setListData(roomList.toArray(new String[0]));
    }

    public static void main(String[] args) throws RemoteException {
        try {
            LocateRegistry.createRegistry(2020);
            Naming.rebind("rmi://localhost:2020/server", new ServerChat());
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
