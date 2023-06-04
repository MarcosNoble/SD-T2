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
    private JList<String> list;
    private JButton  createNewRoomButton, refreshListButton, joinRoomButton, leaveRoomButton;
    private JTextField textField;
    private JFrame frame;
    private JTextPane messageArea;
    private StyledDocument doc;
    private Style style;

    public  String nome;
    public  IServerChat serverStub;
    public  IRoomChat roomStub;
    public UserChat usuario;
    public  String selectedOption;
    public  String salaAtual;
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
        //criar botões
        createNewRoomButton = new JButton("Create New Room");
        createNewRoomButton.addActionListener(this);
        refreshListButton = new JButton("Refresh List");
        refreshListButton.addActionListener(this);
        joinRoomButton = new JButton(("Join"));
        joinRoomButton.addActionListener(this);
        leaveRoomButton = new JButton("Leave");
        leaveRoomButton.addActionListener(this);
        //incluir botões
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        buttonPanel.add(createNewRoomButton);
        buttonPanel.add(refreshListButton);
        buttonPanel.add(joinRoomButton);
        buttonPanel.add(leaveRoomButton);
        //arrumar paineis
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.add(listScrollPane, BorderLayout.CENTER);
        optionsPanel.add(buttonPanel, BorderLayout.SOUTH);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(optionsPanel, BorderLayout.WEST);
        // Janela de texto
        messageArea = new JTextPane();
        messageArea.setEditable(false);
        messageArea.setPreferredSize(new Dimension(300, 200));
        JScrollPane textScrollPane = new JScrollPane(messageArea);
        mainPanel.add(textScrollPane, BorderLayout.CENTER);
        // Frame
        frame = new JFrame();
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.setTitle("ChatSD");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        //documento de estilo
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
            selectedOption = list.getSelectedValue();
            if (selectedOption == null) {
                JOptionPane.showMessageDialog(frame, "Selecione uma sala");
            } else {
                joinRoom(selectedOption);
            }
        } else if (e.getSource() == createNewRoomButton) {
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
                unbind(selectedOption);
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
            refreshList();
            if( !roomList.contains(nomeSala)){
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(frame, "Sala n existe mais");
                });
            }else {
                roomStub = (IRoomChat) Naming.lookup("rmi://localhost:2020/" + nomeSala);
                roomStub.joinRoom(this.nome, this);
                salaAtual = nomeSala;
            }
        } catch (RemoteException|MalformedURLException|NotBoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void deliverMsg(String senderName, String msg) throws RemoteException {
        switch (senderName) {
            case "SYSTEM" -> appendToMessageArea(msg + "\n", Color.BLUE);
            case "SYSTEMClose" -> {
                appendToMessageArea(msg + "\n", Color.BLUE);
                unbind(selectedOption);
                joinRoomButton.setEnabled(true);
                roomList.remove(salaAtual);
                refreshList();
            }
            case "SYSTEMJoin" -> {
                System.out.println("nome ja existe");
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(frame, "Nome ja existe");
                });
                unbind(selectedOption);
            }
            case "SYSTEMJoinSuccess" -> joinRoomButton.setEnabled(false);
            default -> {
                if (msg.equals("Sala fechada pelo servidor.")) {
                    appendToMessageArea(msg + "\n", Color.BLUE);
                    unbind(selectedOption);
                    joinRoomButton.setEnabled(true);
                    roomList.remove(salaAtual);
                    refreshList();
                } else {
                    appendToMessageArea(senderName + ": " + msg + "\n", null);
                }
            }
        }
    }

    public  void unbind(String nomeSala){
        roomStub = null;
    }

    public static void main(String[] args) throws RemoteException {
        UserChat user = new UserChat();
        user.usuario= user;
    }
}
