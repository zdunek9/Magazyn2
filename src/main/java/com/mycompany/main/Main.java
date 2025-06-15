/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.main;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import javax.swing.*;
import java.util.*;
import com.formdev.flatlaf.FlatDarculaLaf;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String configPath = args.length > 0 ? args[0] : "src/config.json";
        odczytajKonfiguracje(configPath);
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(UserManagerGUI::new);

        Scanner scanner = new Scanner(System.in);
        CSVStorage storage = new CSVStorage("users.csv");
        List<User> users = storage.readUsers();

        while (true) {
            System.out.println("\nCRUD MENU:");
            System.out.println("1. Wyświetl użytkowników");
            System.out.println("2. Dodaj użytkownika");
            System.out.println("3. Edytuj użytkownika");
            System.out.println("4. Usuń użytkownika");
            System.out.println("5. Wyjdź");

            System.out.print("Wybierz opcję: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // usuwa newline

            switch (choice) {
                case 1 -> {
                    if (users.isEmpty()) {
                        System.out.println("Brak użytkowników.");
                    } else {
                        System.out.println("Lista użytkowników:");
                        for (User user : users) {
                            System.out.println(user.getId() + " | " + user.getName() + " | " + user.getEmail());
                        }
                    }
                }
                case 2 -> {
                    System.out.print("Imię i nazwisko: ");
                    String name = scanner.nextLine();
                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    int id = users.stream().mapToInt(User::getId).max().orElse(0) + 1;
                    users.add(new User(id, name, email));
                    storage.writeUsers(users);
                    System.out.println("Użytkownik dodany.");
                }
                case 3 -> {
                    System.out.print("Podaj ID użytkownika do edycji: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    User user = findUserById(users, id);
                    if (user != null) {
                        System.out.print("Nowe imię i nazwisko (" + user.getName() + "): ");
                        String name = scanner.nextLine();
                        System.out.print("Nowy email (" + user.getEmail() + "): ");
                        String email = scanner.nextLine();
                        user.setName(name.isEmpty() ? user.getName() : name);
                        user.setEmail(email.isEmpty() ? user.getEmail() : email);
                        storage.writeUsers(users);
                        System.out.println("Użytkownik zaktualizowany.");
                    } else {
                        System.out.println("Nie znaleziono użytkownika.");
                    }
                }
                case 4 -> {
                    System.out.print("Podaj ID użytkownika do usunięcia: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    boolean removed = users.removeIf(u -> u.getId() == id);
                    if (removed) {
                        storage.writeUsers(users);
                        System.out.println("Użytkownik usunięty.");
                    } else {
                        System.out.println("Nie znaleziono użytkownika.");
                    }
                }
                case 5 -> {
                    System.out.println("Zamykam program.");
                    return;
                }
                default ->
                    System.out.println("Nieprawidłowy wybór.");
            }
        }
    }

    private static void odczytajKonfiguracje(String fileName) {
        if (fileName.endsWith(".json")) {
            wczytajZJson(fileName);
        } else if (fileName.endsWith(".xml")) {
            wczytajZXml(fileName);
        } else {
            System.err.println("Nieobsługiwany format pliku konfiguracyjnego: " + fileName);
        }
    }

    private static void wczytajZJson(String fileName) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject configJson = (JSONObject) parser.parse(new FileReader(fileName));

            String language = (String) configJson.get("language");
            String theme = (String) configJson.get("theme");
            String name = (String) configJson.get("name");
            String user = (String) configJson.get("user");
            String adress = (String) configJson.get("adress");

            wypiszKonfiguracje(language, theme, name, user, adress);
        } catch (IOException | ParseException e) {
            System.err.println("Błąd podczas odczytu JSON: " + e.getMessage());
        }
    }

    private static void wczytajZXml(String fileName) {
        try {
            JAXBContext context = JAXBContext.newInstance(Konfiguracja.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Konfiguracja config = (Konfiguracja) unmarshaller.unmarshal(new File(fileName));

            wypiszKonfiguracje(config.language, config.theme, config.name, config.user, config.adress);
        } catch (Exception e) {
            System.err.println("Błąd podczas odczytu XML: " + e.getMessage());
        }
    }

    private static void wypiszKonfiguracje(String language, String theme, String name, String user, String adress) {
        System.out.println("=== Konfiguracja Aplikacji ===");
        System.out.println("Język interfejsu: " + language);
        System.out.println("Motyw: " + theme);
        System.out.println("Nazwa: " + name);
        System.out.println("User: " + user);
        System.out.println("Adres: " + adress);
    }

    @XmlRootElement(name = "config")
    public static class Konfiguracja {

        @XmlElement
        public String language;
        @XmlElement
        public String theme;
        @XmlElement
        public String name;
        @XmlElement
        public String user;
        @XmlElement
        public String adress;
    }

    private static User findUserById(List<User> users, int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
}
