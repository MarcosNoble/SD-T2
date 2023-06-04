import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class UserChat extends UnicastRemoteObject implements IUserChat, ActionListener {
    private final JList<String> list;
    private JButton  createNewRoomButton, refreshListButton, joinRoomButton, leaveRoomButton, closeRoomButton;
    JTextField textField;
    private JFrame frame;
    private JTextPane messageArea;
    private StyledDocument doc;
    private Style style;
    public  String nome;
    public  IServerChat serverStub;
    public  IRoomChat roomStub;
    public UserChat user;


    public ArrayList<String> roomList;
    public UserChat() throws RemoteException{
        nome = JOptionPane.showInputDialog(frame, "Escolha o seu nome");
        this.roomList = new ArrayList<>();
        try {
            serverStub = (IServerChat)  Naming.lookup("rmi://localhost:2020/server");
            System.out.println("server stub criado");
        } catch (MalformedURLException | NotBoundException e) {
            throw new RuntimeException(e);
        }
        textField = new JTextField(50);
        textField.setEditable(true);
        textField.addActionListener(this);
        list = new JList<String>(roomList.toArray(new String[0]));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(list);
        // Create the Create New Room button
        createNewRoomButton = new JButton("Create New Room");
        createNewRoomButton.addActionListener(this);
        // Create the Refresh List button
        refreshListButton = new JButton("Refresh List");
        refreshListButton.addActionListener(this);
        //Create Join Button
        joinRoomButton = new JButton(("Join"));
        joinRoomButton.addActionListener(this);
        //Leave Button
        leaveRoomButton = new JButton("Leave");
        leaveRoomButton.addActionListener(this);
        closeRoomButton = new JButton("msg teste");
        closeRoomButton.addActionListener(this);
        // Add the components to the panel
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        buttonPanel.add(createNewRoomButton);
        buttonPanel.add(refreshListButton);
        buttonPanel.add(joinRoomButton);
        buttonPanel.add(leaveRoomButton);
        buttonPanel.add(closeRoomButton);

        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.add(listScrollPane, BorderLayout.CENTER);
        optionsPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(optionsPanel, BorderLayout.WEST);

        // Create the text window that displays the selected option
        messageArea = new JTextPane();
        messageArea.setEditable(false);
        messageArea.setPreferredSize(new Dimension(300, 200));
        JScrollPane textScrollPane = new JScrollPane(messageArea);

        mainPanel.add(textScrollPane, BorderLayout.CENTER);

        // Set up the frame
        frame = new JFrame();
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.setTitle("Options");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        // Initialize the document and style
        doc = messageArea.getStyledDocument();
        style = messageArea.addStyle("Color Style", null);

    }
    private void appendToMessageArea(String message, Color color) {
        try {
            if (color != null) {
                StyleConstants.setForeground(style, Color.BLUE);
            }else{
                StyleConstants.setForeground(style, Color.black);
            }
            doc.insertString(doc.getLength(), message, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == joinRoomButton) {
            // Get the selected item from the list
            String selectedOption = list.getSelectedValue();
            if (selectedOption == null) {
                JOptionPane.showMessageDialog(frame, "Please select an option.");
            } else {
                // Do something with the selected option
                joinRoom(selectedOption);
                //appendToMessageArea("entrando na sala "+ selectedOption + "\n", Color.BLUE);
            }
        } else if (e.getSource() == createNewRoomButton) {
            // Do something when Create New Room button is clicked
            String roomName = JOptionPane.showInputDialog(frame, "Escolha o nome da sala:");
            if(roomList != null && roomName != null && !roomName.isEmpty()) {
                if (!roomList.contains(roomName)) {
                    try {
                        JOptionPane.showMessageDialog(frame, "Sala " + roomName + " criada");
                        serverStub.createRoom(roomName);
                        refreshList();
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, " Sala ja existe");
                }
            }
        } else if (e.getSource() == refreshListButton) {
            refreshList();
        } else if (e.getSource() == leaveRoomButton) {
            try {
                roomStub.leaveRoom(this.nome);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }else if (e.getSource()== closeRoomButton){
            try {
                roomStub.sendMsg(this.nome, "Mensagem teste");
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        }else if (e.getSource() == textField){
            try {
                roomStub.sendMsg(this.nome, textField.getText());
                textField.setText("");
            } catch (RemoteException ex) {
                    ex.printStackTrace();
            }
        }
    }
    public void refreshList(){
        try {
            this.roomList = serverStub.getRooms();
            list.setListData(roomList.toArray(new String[0]));
        } catch (RemoteException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void joinRoom(String nomeSala){
        try {
            roomStub = (IRoomChat) Naming.lookup("rmi://localhost:2020/"+nomeSala);
            roomStub.joinRoom(this.nome,this);
        } catch (RemoteException|MalformedURLException|NotBoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void deliverMsg(String senderName, String msg) throws RemoteException {
        if(senderName.equals("SYSTEM")) {
            appendToMessageArea(msg + "\n", Color.BLUE);
        }else if (senderName.equals("SYSTEMCLOSE")){
            appendToMessageArea(msg + "\n", Color.BLUE);
            refreshList();
        }else{
            appendToMessageArea(senderName+ ": "+ msg + "\n", null);
        }
    }

    public void setName(){

    }

    public static void main(String[] args) throws RemoteException {
        UserChat user = new UserChat();
        user.user= user;
    }
}
