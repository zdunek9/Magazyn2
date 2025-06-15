package com.mycompany.main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserManagerGUI extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private CSVStorage storage;
    private List<User> users;

    public UserManagerGUI() {

        storage = new CSVStorage("users.csv");
        users = storage.readUsers();

        setTitle("Manager Użytkowników");
        ImageIcon icon = new ImageIcon(getClass().getResource("/MailSmall.png"));
        setIconImage(icon.getImage());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 500)); // minimalny rozmiar

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email"}, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // nic nie da się edytować z poziomu tabeli
            }
        };
        table = new JTable(tableModel);
        refreshTable(); // Stworzy sie tabela
        Font font = new Font("SansSerif", Font.PLAIN, 18);
        table.setFont(font);
        table.setRowHeight(20);
        table.getTableHeader().setFont(font);
        table.setShowGrid(true);
        table.setGridColor(Color.WHITE);

        JButton addButton = new JButton("Dodaj");
        JButton editButton = new JButton("Edytuj");
        JButton deleteButton = new JButton("Usuń");
        JButton saveButton = new JButton("Zapisz do CSV");
        JButton loadButton = new JButton("Wczytaj z CSV");
        addButton.setFont(font);
        editButton.setFont(font);
        deleteButton.setFont(font);
        saveButton.setFont(font);
        loadButton.setFont(font);


        JPanel panel = new JPanel();
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(saveButton);
        panel.add(loadButton);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH); // Pozycjonowanie buttonów

        addButton.addActionListener(e -> addUser());
        editButton.addActionListener(e -> editUser());
        deleteButton.addActionListener(e -> deleteUser());
        saveButton.addActionListener(e -> {
            storage.writeUsers(users);
            JOptionPane.showMessageDialog(this, "Zapisano do CSV.");
        });
        loadButton.addActionListener(e -> {
            users = storage.readUsers();
            refreshTable();
        });

        setVisible(true);
    }

    private void refreshTable() {
        users.sort(Comparator.comparingInt(User::getId)); // ← sortowanie po ID rosnąco
        tableModel.setRowCount(0); // czyścimy tabelę
        for (User u : users) {
            tableModel.addRow(new Object[]{u.getId(), u.getName(), u.getEmail()});
        }
    }


    private void addUser() {
        while (true) {
            JTextField nameField = new JTextField();
            JTextField emailField = new JTextField();
            Object[] fields = {"Imię i nazwisko:", nameField, "Email:", emailField};

            int result = JOptionPane.showConfirmDialog(this, fields, "Dodaj użytkownika", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                break;
            }

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Nieprawidłowy adres email. Spróbuj ponownie.");
                continue;
            }

            int id = getFirstFreeId();
            users.add(new User(id, name, email));
            refreshTable();
            break;
        }
    }



    private void editUser() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Wybierz użytkownika do edycji.");
            return;
        }

        User user = users.get(row);

        while (true) {
            JTextField nameField = new JTextField(user.getName());
            JTextField emailField = new JTextField(user.getEmail());
            Object[] fields = {"Nowe imię i nazwisko:", nameField, "Nowy email:", emailField};

            int result = JOptionPane.showConfirmDialog(this, fields, "Edytuj użytkownika", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                break;
            }

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "Nieprawidłowy adres email. Spróbuj ponownie.");
                continue;
            }

            user.setName(name);
            user.setEmail(email);
            refreshTable();
            break;
        }
    }



    private void deleteUser() { //Panel do usuwania
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Wybierz użytkownika do usunięcia.");
            return;
        }
        users.remove(row);
        refreshTable();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{1,3}$");
    }
    private int getFirstFreeId() {
        Set<Integer> usedIds = new HashSet<>();
        for (User user : users) {
            usedIds.add(user.getId());
        }

        int id = 1;
        while (usedIds.contains(id)) {
            id++;
        }
        return id;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserManagerGUI::new);
    }
}
