package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;
import services.UserService;

public class LoginController {

    // LOGIN FIELDS
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    // TABS + PANES
    @FXML private StackPane authStack;
    @FXML private VBox loginPane;
    @FXML private VBox signupPane;
    @FXML private Button loginTabButton;
    @FXML private Button signupTabButton;

    // REGISTER FIELDS
    @FXML private TextField regFullNameField;
    @FXML private TextField regEmailField;
    @FXML private PasswordField regPasswordField;
    @FXML private ComboBox<String> regRoleCombo;
    @FXML private Label registerErrorLabel;

    private final UserService userService = UserService.getInstance();

    @FXML
    private void initialize() {
        // roles in combo
        if (regRoleCombo != null) {
            regRoleCombo.getItems().setAll("STUDENT"); //can only sign up as student
            regRoleCombo.setValue("STUDENT");
        }

        if (loginPane != null && signupPane != null) {
            showLoginPane();
        }
    }

    // ----- LOGIN -----

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        User user = userService.login(email, password);

        if (user != null) {
            errorLabel.setText("");
            loadDashboard(user);
        } else {
            errorLabel.setText("Invalid email/password");
        }
    }

    private void loadDashboard(User user) {
        try {
            String fxml = "/fxml/admin.fxml";

            String role = user.getRole();
            if (role != null) {
                switch (role.toUpperCase()) {
                    case "STUDENT":
                        fxml = "/fxml/student.fxml";
                        break;
                    case "AGENT":
                        fxml = "/fxml/agent.fxml";
                        break;
                    case "ADMIN":
                    default:
                        fxml = "/fxml/admin.fxml";
                        break;
                }
            }

            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error loading dashboard: " + e.getMessage());
        }
    }

    // ----- REGISTER -----

    @FXML
    private void handleRegister() {
        String name = regFullNameField.getText().trim();
        String email = regEmailField.getText().trim();
        String password = regPasswordField.getText().trim();
        String roleText = regRoleCombo.getValue();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || roleText == null) {
            registerErrorLabel.setText("Please fill in all fields.");
            return;
        }

        // very simple duplicate check
        User existing = userService.login(email, password);
        if (existing != null) {
            registerErrorLabel.setText("An account with this email already exists.");
            return;
        }

        User newUser = new User(email, password, roleText.toUpperCase());
        newUser.setUsername(name);
        userService.addUser(newUser);

        registerErrorLabel.setText("");
        showLoginPane();
        emailField.setText(email);
        passwordField.clear();
    }

    // ----- TAB SWITCHING -----

    @FXML
    private void showLoginPane() {
        loginPane.setVisible(true);
        loginPane.setManaged(true);
        signupPane.setVisible(false);
        signupPane.setManaged(false);

        loginTabButton.setStyle(
                "-fx-background-radius: 20 0 0 20; -fx-background-color: #111827; " +
                        "-fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 210;");
        signupTabButton.setStyle(
                "-fx-background-radius: 0 20 20 0; -fx-background-color: #e5e7eb; " +
                        "-fx-text-fill: #4b5563; -fx-font-weight: bold; -fx-pref-width: 210;");
    }

    @FXML
    private void showSignupPane() {
        loginPane.setVisible(false);
        loginPane.setManaged(false);
        signupPane.setVisible(true);
        signupPane.setManaged(true);

        loginTabButton.setStyle(
                "-fx-background-radius: 20 0 0 20; -fx-background-color: #e5e7eb; " +
                        "-fx-text-fill: #4b5563; -fx-font-weight: bold; -fx-pref-width: 210;");
        signupTabButton.setStyle(
                "-fx-background-radius: 0 20 20 0; -fx-background-color: #111827; " +
                        "-fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 210;");
    }
}
