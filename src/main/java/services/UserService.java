package services;

import models.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private static UserService instance;

    private final List<User> users;
    private User currentUser;   // track logged-in user

    private UserService() {
        users = new ArrayList<>();
        loadUsersFromFile();
    }

    // ---------- SINGLETON ACCESS ----------

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    // ---------- LOGIN / SESSION ----------

    public User login(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)
                    && user.getPassword().equals(password)) {
                currentUser = user;
                return user;
            }
        }
        return null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    // ---------- USER LIST HELPERS ----------

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public void addUser(User user) {
        users.add(user);
        saveUsersToFile();
    }

    // ---------- FILE LOAD / SAVE ----------

    // users.txt format (one per line):
    // fullName,email,password,ROLE
    private void loadUsersFromFile() {
        users.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String fullName = parts[0].trim();
                    String email    = parts[1].trim();
                    String password = parts[2].trim();
                    String role     = parts[3].trim();

                    User user = new User(email, password, role);
                    user.setUsername(fullName);
                    users.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUsersToFile() {
        try (FileWriter writer = new FileWriter("users.txt")) {
            for (User user : users) {
                String fullName = user.getUsername() != null ? user.getUsername() : "";
                writer.write(fullName + ","
                        + user.getEmail() + ","
                        + user.getPassword() + ","
                        + user.getRole() + System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
