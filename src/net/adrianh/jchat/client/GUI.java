package net.adrianh.jchat.client;

import net.adrianh.jchat.shared.Chat;
import net.adrianh.jchat.shared.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains all of the graphical elements of the program
 * @author Johan Fridlund, johfridl@student.chalmers.se
 * @version 2020/03/08
 */
public class GUI implements PropertyChangeListener {

    private static final int width = 960;
    private static final int height = 540;
    private Login loginDialog;

    private JFrame frame;
    private JTextArea chatContainer;
    private JTextField messageBox, groupSearchField;
    private JButton sendButton, groupJoinButton;
    private JPanel messagePanel, header, chatPanel, profileOverview, sidebarPanel, groupList;
    private JLabel recipientLabel, profilePicture, profileName;
    private JScrollPane chatScrollFrame;
    private JTabbedPane contactTabs;

    private Set<JLabel> chatsJoined;

    //constructor of GUI lets user login with an username and creates the UI-window.  
    public GUI() {
        this.messageBox = new JTextField("Write a message");
        loginDialog = new Login(this);
        loginDialog.getDialog().setVisible(true);

        chatsJoined = new HashSet<>();
        makeFrame();
        makeBody();
        renderWindow();
    }

    /**
     * Gets the root frame of the GUI
     * @return the root frame
     */
    public JFrame getFrame() {return this.frame;}

    /**
     * Creates the root frame
     */
    private void makeFrame() {
        frame = new JFrame("jChat");
        frame.setMinimumSize(new Dimension(width,height));
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Creates the sidebar panel
     */
    private void makeSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(280,0));
        sidebarPanel.setMaximumSize(new Dimension(280,Integer.MAX_VALUE));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel,BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(32, 34, 37));
    }

    /**
     * Creates the main chat panel
     */
    private void makeChatPanel() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(new Color(54, 57, 63));
    }

    /**
     * Creates the profile panel
     */
    private void makeProfileView() {
        profileOverview = new JPanel();
        profileOverview.setLayout(new BorderLayout());
        profileOverview.setPreferredSize(new Dimension(280,180));
        profileOverview.setMaximumSize(new Dimension(280,180));
        profileOverview.setBackground(new Color(32, 34, 37));

        //Profile Picture
        Image profile = Toolkit.getDefaultToolkit().createImage(this.getClass().getResource("/defaultProfile.png"));
        Image newImage = profile.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        profilePicture = new JLabel(new ImageIcon(newImage));
        profilePicture.setBorder(new EmptyBorder(20,0,20,0));

        //Profile name
        profileName = new JLabel();
        profileName.setSize(280,30);
        profileName.setBorder(new EmptyBorder(0,0,20,0));
        profileName.setHorizontalAlignment(JLabel.CENTER);
        profileName.setForeground(Color.WHITE);
    }

    /**
     * Creates the different sidebar panel tabs
     */
    private void makeTabs() {
        contactTabs = new JTabbedPane();
        JPanel groupTab = new JPanel();
        JPanel groupSearchPanel = new JPanel();
        groupList = new JPanel();
        groupTab.setLayout(new BorderLayout());
        groupList.setLayout(new BoxLayout(groupList,BoxLayout.Y_AXIS));
        groupSearchField = new JTextField(12);
        groupSearchField.setBackground(new Color(64, 68, 75));
        groupSearchField.setBorder(null);
        groupSearchField.setCaretColor(Color.WHITE);
        groupSearchField.setForeground(Color.WHITE);


        groupList.setBackground(new Color(39, 42, 46));
        groupList.setForeground(Color.WHITE);
        groupSearchPanel.setBackground(new Color(54, 57, 63));

        groupJoinButton = new JButton("Join");
        groupSearchPanel.add(groupSearchField);
        groupSearchPanel.add(groupJoinButton);
        groupTab.add(groupSearchPanel,BorderLayout.NORTH);
        groupTab.add(groupList,BorderLayout.CENTER);


        contactTabs.addTab("Groups", groupTab);
        contactTabs.setBorder(null);
        contactTabs.setMinimumSize(new Dimension(280,0));
    }

    /**
     * Creates the header panel for the chat panel
     */
    private void makeHeader() {
        header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setBackground(new Color(32, 34, 37));
        header.setPreferredSize(new Dimension(0,35));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(22, 24, 26)));

        //Recipient
        recipientLabel = new JLabel("");
        recipientLabel.setForeground(Color.WHITE);
        recipientLabel.setVerticalAlignment(JLabel.CENTER);
        recipientLabel.setBorder(new EmptyBorder(0,10,0,0));
    }

    /**
     * Creates the text area container for the chat messages
     */
    private void makeChatContainer() {
        chatContainer = new JTextArea();
        chatContainer.setLayout(new BoxLayout(chatContainer, BoxLayout.Y_AXIS));
        chatContainer.setForeground(Color.WHITE);
        chatContainer.setAutoscrolls(true);
        chatContainer.setBackground(new Color(39, 42, 46));
        chatContainer.setEditable(false);
        chatScrollFrame = new JScrollPane(chatContainer);
        chatScrollFrame.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollFrame.setBorder(null);
        chatScrollFrame.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(5,5,15,5, new Color(54, 57, 63)), BorderFactory.createMatteBorder(10,10,10,10, new Color(39,42,46))));
        DefaultCaret caret = (DefaultCaret)chatContainer.getCaret();
        //possible to change in settings to NEVER_UPDATE?
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    /**
     * Creates the message box and send button
     */
    private void makeMessageBox() {
        messagePanel = new JPanel();
        messagePanel.setPreferredSize(new Dimension(0,65));
        messagePanel.setBackground(new Color(54, 57, 63));

        //"Message text"-box
        messageBox.setPreferredSize(new Dimension(550,40));
        messageBox.setHorizontalAlignment(JTextField.CENTER);
        messageBox.setBackground(new Color(64, 68, 75));
        messageBox.setForeground(Color.GRAY);
        messageBox.setCaretColor(Color.WHITE);
        messageBox.setBorder(null);

        //  Send button
        sendButton = new JButton("Send");
        sendButton.setForeground(Color.DARK_GRAY);

        messageBox.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                JTextField temp = (JTextField)e.getSource();
                temp.setForeground(Color.WHITE);
                if (temp.getText().equals("Write a message")) {
                    temp.setText("");
                }
            }

            public void focusLost(FocusEvent e) {
                JTextField temp = (JTextField)e.getSource();
                temp.setForeground(Color.GRAY);
                if (temp.getText().isEmpty()) {
                    temp.setText("Write a message");
                }
            }
        });
    }

    /**
     * Assembles all the UI elements
     */
    private void assembleBody() {
        //Adding content to mainframe
        messagePanel.add(messageBox);
        messagePanel.add(sendButton);
        header.add(recipientLabel, BorderLayout.WEST);
        chatPanel.add(header, BorderLayout.NORTH);
        chatPanel.add(chatScrollFrame, BorderLayout.CENTER);
        chatPanel.add(messagePanel, BorderLayout.SOUTH);
        profileOverview.add(profilePicture, BorderLayout.NORTH);
        profileOverview.add(profileName, BorderLayout.CENTER);
        sidebarPanel.add(profileOverview);
        sidebarPanel.add(contactTabs);
        frame.getContentPane().add(sidebarPanel);
        frame.getContentPane().add(chatPanel);
    }

    // collection method for creating the entire body of the GUI, different methods instead of a large method to generalize.

    /**
     * Invokes all the methods creating the UI elements
     */
    private void makeBody() {
        makeSidebar();
        makeChatPanel();
        makeProfileView();
        makeTabs();
        makeHeader();
        makeChatContainer();
        makeMessageBox();
        assembleBody();
    }

    /**
     * Renders the window
     */
    private void renderWindow(){
        frame.pack();
    }

    /**
     * Gets the send button
     * @return the send button
     */
    public JButton getSendButton() {
        return this.sendButton;
    }

    /**
     * Gets the message box
     * @return the message box
     */
    public JTextField getMessageBox() {
        return  this.messageBox;
    }

    /**
     * Gets the text field for joining groups
     * @return the search field
     */
    public JTextField getGroupSearchField() {
        return this.groupSearchField;
    }

    /**
     * Gets the join button
     * @return the join button
     */
    public JButton getGroupJoinButton() {
        return this.groupJoinButton;
    }

    /**
     * Gets the login dialog box
     * @return the login dialog object
     */
    public Login getLoginDialog() {
        return this.loginDialog;
    }

    /**
     * Changes the visibility of the root frame
     * @param visible the new visibility status
     */
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    /**
     * Displays a given chat log in the chat container
     * @param c the new chat to display
     */
    private void viewChat(Chat c) {
        recipientLabel.setText(c.toString());
        chatContainer.setText("");
        for (JLabel l: chatsJoined) {
            if (l.getText().equals(c.getName())) {
                l.setForeground(Color.GRAY);
            } else {
                l.setForeground(Color.WHITE);
            }
        }

        for (Message msg: c.getLog()) {
            chatContainer.append(msg.toString());
        }
    }

    /**
     * Adds a new gruop to the sidebar tab
     * @param label the label to be added
     */
    public void addGroupLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        chatsJoined.add(label);
        groupList.add(label);
        groupList.revalidate();
    }

    /**
     * Observes different changes in the model
     * @param e the triggered event
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if ("newMsg".equals(e.getPropertyName())) {
            chatContainer.append(e.getNewValue().toString());
        }
        if ("usernameChosen".equals(e.getPropertyName())) {
            profileName.setText(e.getNewValue().toString());
        }
        if ("chatChange".equals(e.getPropertyName())) {
            Chat chat = (Chat) e.getNewValue();
            viewChat(chat);
        }
        if ("noConnection".equals(e.getPropertyName())) {
            JOptionPane.showMessageDialog(frame,
                "jChat could not connect to the server specified in the config file " +
                "\n"+e.getNewValue(),
                "Connection Error",JOptionPane.ERROR_MESSAGE);
        }
        if ("wrongConfig".equals(e.getPropertyName())) {
            JOptionPane.showMessageDialog(frame,
                "Make sure the config file is correctly formatted" +
                "\n"+e.getNewValue(),
                "Config Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Displays a login dialog box
     * @author Alexander Tepic, tepic@student.chalmers.se
     * @version 2020/03/08
     */
    static class Login {

        private JDialog dialog;
        private JTextField txtfldUsername = new JTextField(24);
        private JButton buttonLogin = new JButton("Login");

        /**
         * @param parent The main GUI object
         */
        public Login(GUI parent) {
            dialog = new JDialog(parent.getFrame());

            JPanel panel1 = new JPanel(new GridLayout(3,1));
            JLabel lblUsername = new JLabel("Username");
            panel1.add(lblUsername);
            panel1.add(txtfldUsername);
            panel1.add(buttonLogin);
            dialog.setLayout(new BorderLayout());
            dialog.add(panel1, BorderLayout.CENTER);

            dialog.pack();
            dialog.setLocationRelativeTo(null);

            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        }

        /**
         * Changes the visibility of the login box
         * @param visible the new visibility status
         */
        public void setVisible(boolean visible) {
            this.dialog.setVisible(visible);
        }

        /**
         * Gets the dialog box
         * @return the dialog box
         */
        public JDialog getDialog() {return this.dialog;}

        /**
         * Gets the text field of the dialog box
         * @return the text field
         */
        public JTextField getLoginTextField() {return this.txtfldUsername;}

        /**
         * Gets the text of the dialog box's text field
         * @return the text field value
         */
        public String getNameInput() {return this.txtfldUsername.getText();}

        /**
         * Gets the send button
         * @return the send button
         */
        public JButton getSendButton() {return this.buttonLogin;}

    }
}

