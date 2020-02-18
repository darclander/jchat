import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GUI implements PropertyChangeListener {

    private JFrame frame;
    private JTextArea chatContainer;
    private JLabel profileName;
    private int width = 960;
    private int height = 540;
    private JTextField messageBox;
    private Login loginDialog;
    private JButton sendButton;

    public GUI() {
        this.messageBox = new JTextField("Write a message");
        loginDialog = new Login(this);
        loginDialog.getDialog().setVisible(true);
        makeFrame();
        makeBody();
        renderWindow();

    }

    public JFrame getFrame() {return this.frame;}

    private void makeFrame() {
        frame = new JFrame("jChat");
        frame.setMinimumSize(new Dimension(width,height));
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    private void makeBody(){
        //Left panel
        JPanel contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(280,0));
        contentPanel.setMaximumSize(new Dimension(280,Integer.MAX_VALUE));
        contentPanel.setLayout(new BoxLayout(contentPanel,BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(32, 34, 37));

        //Right Panel
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBackground(new Color(54, 57, 63));

        //Left-top panel
        JPanel profileOverview = new JPanel();
        profileOverview.setLayout(new BorderLayout());
        profileOverview.setPreferredSize(new Dimension(280,180));
        profileOverview.setMaximumSize(new Dimension(280,180));
        profileOverview.setBackground(new Color(32, 34, 37));

        //Left-bottom panel (Tabbed pane)
        JTabbedPane contactTabs = new JTabbedPane();
        contactTabs.addTab("Friend list", new JPanel());
        contactTabs.addTab("Groups", new JPanel());
        contactTabs.addTab("Settings", new JPanel());
        contactTabs.setBorder(null);

        //Profile Picture
        Image profile = Toolkit.getDefaultToolkit().createImage("defaultProfile.png");
        Image newImage = profile.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        JLabel profilePicture = new JLabel(new ImageIcon(newImage));
        profilePicture.setBorder(new EmptyBorder(20,0,20,0));

        //Profile name
        profileName = new JLabel();
        profileName.setSize(280,30);
        profileName.setBorder(new EmptyBorder(0,0,20,0));
        profileName.setHorizontalAlignment(JLabel.CENTER);
        profileName.setForeground(Color.WHITE);

        //Header
        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setBackground(new Color(32, 34, 37));
        header.setPreferredSize(new Dimension(0,35));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(22, 24, 26)));

        //Recipient
        JLabel recipientLabel = new JLabel("@Group");
        recipientLabel.setForeground(Color.WHITE);
        recipientLabel.setVerticalAlignment(JLabel.CENTER);
        recipientLabel.setBorder(new EmptyBorder(0,10,0,0));

        //Chat container
        chatContainer = new JTextArea();
        chatContainer.setLayout(new BoxLayout(chatContainer, BoxLayout.Y_AXIS));
        chatContainer.setForeground(Color.WHITE);
        chatContainer.setAutoscrolls(true);
        chatContainer.setBackground(new Color(39, 42, 46));
        chatContainer.setEditable(false);
        JScrollPane chatScrollFrame = new JScrollPane(chatContainer);
        chatScrollFrame.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollFrame.setBorder(null);
        chatScrollFrame.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(5,5,15,5, new Color(54, 57, 63)), BorderFactory.createMatteBorder(10,10,10,10, new Color(39,42,46))));
        DefaultCaret caret = (DefaultCaret)chatContainer.getCaret();
        //possible to change in settings to NEVER_UPDATE?
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        //Message Panel
        JPanel messagePanel = new JPanel();
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

        //Adding content to mainframe
        messagePanel.add(messageBox);
        messagePanel.add(sendButton);
        header.add(recipientLabel, BorderLayout.WEST);
        chatPanel.add(header, BorderLayout.NORTH);
        chatPanel.add(chatScrollFrame, BorderLayout.CENTER);
        chatPanel.add(messagePanel, BorderLayout.SOUTH);
        profileOverview.add(profilePicture, BorderLayout.NORTH);
        profileOverview.add(profileName, BorderLayout.CENTER);
        contentPanel.add(profileOverview);
        contentPanel.add(contactTabs);
        frame.getContentPane().add(contentPanel);
        frame.getContentPane().add(chatPanel);
    }


    private void renderWindow(){
        frame.pack();
    }

    public JButton getSendButton() {
        return this.sendButton;
    }

    public JTextField getMessageBox() {
        return  this.messageBox;
    }

    public Login getLoginDialog() {
        return this.loginDialog;
    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if ("newMsg".equals(e.getPropertyName())) {
            chatContainer.append(e.getNewValue().toString());
        }
        if ("usernameChosen".equals(e.getPropertyName())) {
            profileName.setText(e.getNewValue().toString());
        }
    }
}

class Login {

    private JDialog dialog;
    private JTextField txtfldUsername = new JTextField(24);
    private JButton buttonLogin = new JButton("Login");

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

    public void setVisible(boolean visible) {
        this.dialog.setVisible(visible);
    }

    public JDialog getDialog() {return this.dialog;}
    public JTextField getLoginTextField() {return this.txtfldUsername;}
    public String getNameInput() {return this.txtfldUsername.getText();}
    public JButton getSendButton() {return this.buttonLogin;}

}
