package net.adrianh.jchat.client;

public class Controller {
    private ChatClient model;
    private GUI view;

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

    private void send() {
        model.sendText(model.getUser(),view.getMessageBox().getText());
        view.getMessageBox().setText("");
    }

    private void login() {
        model.setUser(view.getLoginDialog().getNameInput());
        view.getLoginDialog().setVisible(false);
        view.setVisible(true);
        model.connectAndListen();
    }

    private void joinChat() {
        String chat = view.getGroupSearchField().getText();
        model.sendJoinRequest(chat);
    }
}