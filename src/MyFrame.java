import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MyFrame implements ActionListener {
    private final JList<String> list;
    private JButton selectButton, createNewRoomButton;
    private ArrayList<String> options;
    private JFrame frame;
    private JTextPane selectedOptionTextPane;

    public MyFrame(ArrayList<String> options) {
        this.options = options;

        // Create the list
        list = new JList<String>(options.toArray(new String[0]));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(list);

        // Create the Select button
        selectButton = new JButton("Select");
        selectButton.addActionListener(this);

        // Create the Create New Room button
        createNewRoomButton = new JButton("Create New Room");
        createNewRoomButton.addActionListener(this);

        // Add the components to the panel
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1));
        buttonPanel.add(selectButton);
        buttonPanel.add(createNewRoomButton);

        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.add(listScrollPane, BorderLayout.CENTER);
        optionsPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(optionsPanel, BorderLayout.WEST);

        // Create the text window that displays the selected option
        selectedOptionTextPane = new JTextPane();
        selectedOptionTextPane.setEditable(false);
        selectedOptionTextPane.setPreferredSize(new Dimension(300,200));
        JScrollPane textScrollPane = new JScrollPane(selectedOptionTextPane);

        mainPanel.add(textScrollPane, BorderLayout.CENTER);

        // Set up the frame
        frame = new JFrame();
        frame.setTitle("Options");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectButton) {
            // Get the selected item from the list
            String selectedOption = list.getSelectedValue();
            if (selectedOption == null) {
                JOptionPane.showMessageDialog(frame, "Please select an option.");
            } else {
                // Do something with the selected option
                selectedOptionTextPane.setText(selectedOption);
                StyledDocument doc = selectedOptionTextPane.getStyledDocument();
                Style style = selectedOptionTextPane.addStyle("Color Style", null);
                StyleConstants.setForeground(style, Color.BLUE);
                try {
                    doc.insertString(doc.getLength(), " and styled text", style);
                } catch (javax.swing.text.BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        } else if (e.getSource() == createNewRoomButton) {
            // Do something when Create New Room button is clicked
            JOptionPane.showMessageDialog(frame, "Create New Room button clicked.");
        }
    }

    public static void main(String[] args) {
        ArrayList<String> options = new ArrayList<String>();
        options.add("Option 1");
        options.add("Option 2");
        options.add("Option 3");
        new MyFrame(options);
    }
}