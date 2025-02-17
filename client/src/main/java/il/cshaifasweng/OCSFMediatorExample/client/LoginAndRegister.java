package il.cshaifasweng.OCSFMediatorExample.client;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.entities.OrderData;
import il.cshaifasweng.OCSFMediatorExample.entities.UserData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LoginAndRegister {

    @FXML
    public Button guest_login;
    @FXML
    public Label label_branch_login;
    @FXML
    public ComboBox branch_list_login;
    public ComboBox branch_list_register;
    public TextField textfield_id_register;
    public Label errorLogin;
    public Label errorRegister;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button button_login;

    @FXML
    private Label label_credit_card_register;

    @FXML
    private Label label_email_register;

    @FXML
    private Label label_password;

    @FXML
    private Label label_password_register;

    @FXML
    private Label label_type_register;

    @FXML
    private Label label_username;

    @FXML
    private Label label_username_register;

    @FXML
    private Button register_login;

    @FXML
    private TextField textfield_credit_card_register;

    @FXML
    private TextField textfield_email_register;

    @FXML
    private TextField textfield_password_login;

    @FXML
    private TextField textfield_password_register;

    @FXML
    private TextField textfield_username_login;

    @FXML
    private TextField textfield_username_register;

    @FXML
    private ComboBox<String> type_dropdown;

    @FXML
    private VBox vbox_main;

    private List<String> branches;

    private List<String> takenUsernames, takenEmails;
    @Subscribe
    public void onLoginRecievedEvent(LoginReceivedEvent event) {
        System.out.println("Received login\n");
        errorLogin.setText("");
        if (event.didSuccessfullyLogin()){
            App.userData = event.getUser();
            App.orderData = new OrderData();
            try{
                System.out.println("Logged in as " + App.userData.getUsername());
                App.setRoot("MainMenu");
            }
            catch(Exception e){
                System.out.println("Error: " + e.getMessage());
            }
        }
        else{
            errorLogin.setText("ERROR: User already logged in or wrong username/password/branch");
            System.out.println("Failed to login\n");
        }

    }
    @Subscribe
    public void onBranchRecievedEvent(BranchesReceivedEvent event) {
        System.out.println("Received branch\n");
        branches = event.getBranches().getBranchList();
        vbox_main.setVisible(true);
        for (String branch : branches) {
            branch_list_login.getItems().add(branch);
            branch_list_register.getItems().add(branch);
        }
    }
    @Subscribe
    public void onUserListDataRecievedEvent(ReceivedUserListEvent event) {
        System.out.println("Received user list, size: " + event.getUsers().users.size());
        takenUsernames = new java.util.ArrayList<>();
        takenEmails = new java.util.ArrayList<>();
        for (UserData user : event.getUsers().users) {
            takenUsernames.add(user.getUsername());
            takenEmails.add(user.getEmail());
        }
    }
    @FXML
    void initialize() {
        EventBus.getDefault().register(this);

        type_dropdown.getItems().addAll(
                "Branch",
                "Network",
                "Subscription"
        );
        errorLogin.setStyle("-fx-text-fill: red;");
        errorRegister.setStyle("-fx-text-fill: red;");
        System.out.println("Sending Branches Request");
        SimpleClient.getClient().requestBranches();
        System.out.println("Sent Branches Request");
        System.out.println("Sending users request");
        SimpleClient.getClient().requestUsers("network");
        System.out.println("Sent users request");
    }

    public void login(ActionEvent actionEvent) {
        //check that all fields are filled out and that branches are selected
        if (textfield_username_login.getText().isEmpty() || textfield_password_login.getText().isEmpty() || branch_list_login.getSelectionModel().isEmpty()) {
            System.out.println("Missing fields\n");
            errorLogin.setText("ERROR: Missing fields");
            return;
        }
        System.out.println("Sending request");
        SimpleClient.getClient().requestLogin(textfield_username_login.getText(), textfield_password_login.getText(), branch_list_login.getSelectionModel().getSelectedItem().toString());
        System.out.println("Sent request");
    }

    public void login_as_guest(ActionEvent actionEvent) {
        App.userData = new UserData();
        try{
            App.setRoot("PrimaryCatalog");
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void register(ActionEvent actionEvent) {
        //check that all fields are filled out and that branches are selected and also that type is selected
        if (textfield_username_register.getText().isEmpty()
                || textfield_password_register.getText().isEmpty() || textfield_email_register.getText().isEmpty()
                || textfield_credit_card_register.getText().isEmpty() || branch_list_register.getSelectionModel().isEmpty()
                || type_dropdown.getSelectionModel().isEmpty() || textfield_id_register.getText().isEmpty()) {
            System.out.println("Missing fields\n");
            errorRegister.setText("ERROR: Missing fields");
            return;
        }
        //check all field validity
        //username conataints only letters and numbers
        if (!textfield_username_register.getText().matches("[a-zA-Z0-9]+")) {
            System.out.println("Username contains illegal characters\n");
            errorRegister.setText("ERROR: Username contains illegal characters");
            return;
        }
        //password contains only letters and numbers and special characters
        if (!textfield_password_register.getText().matches("[a-zA-Z0-9!@#$%^&*()_+-=]+")) {
            System.out.println("Password contains illegal characters\n");
            errorRegister.setText("ERROR: Password contains illegal characters");
            return;
        }
        //email is valid
        if (!textfield_email_register.getText().matches("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
            System.out.println("Email is invalid\n");
            errorRegister.setText("ERROR: Email is invalid");
            return;
        }
        //credit card is valid
        if (!textfield_credit_card_register.getText().matches("^[0-9]{16}$")) {
            System.out.println("Credit card is invalid\n");
            errorRegister.setText("ERROR: Credit card is invalid");
            return;
        }
        //id is valid
        if (!textfield_id_register.getText().matches("^[0-9]{9}$")) {
            System.out.println("ID is invalid\n");
            errorRegister.setText("ERROR: ID is invalid");
            return;
        }
        //check that username is not taken
        for (String un : takenUsernames) {
            if (un.equals(textfield_username_register.getText())) {
                System.out.println("Username is taken\n");
                errorRegister.setText("ERROR: Username is taken");
                return;
            }
        }
        //check that email is not taken
        for (String em : takenEmails) {
            if (em.equals(textfield_email_register.getText())) {
                System.out.println("Email is taken\n");
                errorRegister.setText("ERROR: Email is taken");
                return;
            }
        }
        SimpleClient.getClient().requestRegister(new UserData(textfield_username_register.getText(), textfield_password_register.getText(), textfield_email_register.getText(),
                type_dropdown.getSelectionModel().getSelectedIndex() + 1, textfield_credit_card_register.getText(), textfield_id_register.getText(), -1, branch_list_register.getSelectionModel().getSelectedItem().toString(), -1));
        System.out.println("Sending request\n");
        System.out.println("Sent request\n");
    }
}
