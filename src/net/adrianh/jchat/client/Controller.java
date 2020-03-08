package net.adrianh.jchat.client;

import net.adrianh.jchat.shared.Chat;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A controller that takes care of the communication between the model and the view.
 * Contains all the actionListeners.
 * @author Adrian Håkansson, adrhak@student.chalmers.se
 * @version 2020/03/08
 */
public class Controller {
    private ChatClient model;
    private GUI view;

    /**
     * @param model The model of the application
     * @param view The view containing the visual presentation
     */
    public Controller(ChatClient model, GUI view) {
        this.model = model;
        this.view = view;

        // Add action listeners for view elements
        // Send text
        view.getSendButton().addActionListener(e -> send());
        view.getMessageBox().addActionListener(e -> send());
        // Login
        view.getLoginDialog().getLoginTextField().addActionListener(e -> login());
        view.getLoginDialog().getSendButton().addActionListener(e -> login());
        // Join Group
        view.getGroupJoinButton().addActionListener(e -> joinChat());
        view.getGroupSearchField().addActionListener(e -> joinChat());
    }

    /**
     * Invokes the sendText() method of the model and clears the message box
     */
    private void send() {
        model.sendText(view.getMessageBox().getText());
        view.getMessageBox().setText("");
    }

    /**
     * Invokes the setUser() & connectAndListen() methods of the model and displays the main window
     */
    private void login() {
        model.setUser(view.getLoginDialog().getNameInput());
        view.getLoginDialog().setVisible(false);
        view.setVisible(true);
        model.connectAndListen();
    }

    /**
     * Invokes the sendJoinRequest() of the model and adds a group label to the view
     */
    public void joinChat() {
        String chat = view.getGroupSearchField().getText();
        // Prevent duplicate joins
        for (Chat c: model.getChatsJoined()) {
            if (c.getName().equals(chat)) {
                model.setCurrentChat(c);
                return;
            }
        }
        // Default to default chat
        if (chat.isEmpty()) {
            chat = "default";
        }
        model.sendJoinRequest(chat);

        JLabel label = new JLabel(chat);
        label.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                JLabel l = (JLabel) e.getSource();
                for (Chat c: model.getChatsJoined()) {
                    if (c.getName().equals(l.getText())) {
                        model.setCurrentChat(c);
                    }
                }
            }
        });
        view.addGroupLabel(label);

    }
}