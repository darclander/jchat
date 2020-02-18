public class Controller {

    private ChatClient model;
    private GUI view;

    public Controller(ChatClient model, GUI view) {
        this.model = model;
        this.view = view;

        // Add action listeners for view elements
        view.getSendButton().addActionListener(e -> send());
        view.getMessageBox().addActionListener(e -> send());

        view.getLoginDialog().getLoginTextField().addActionListener(e -> login());
        view.getLoginDialog().getSendButton().addActionListener(e -> login());


    }

    private void send() {
        model.sendText(model.getUser(),view.getMessageBox().getText());
        view.getMessageBox().setText("");
    }

    private void login() {
        model.setUser(view.getLoginDialog().getNameInput());
        view.getLoginDialog().setVisible(false);
        view.setVisible(true);

    }
}
