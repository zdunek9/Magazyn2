/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.main;



import java.util.*;

public class Main {
    public static void main(String[] args) {
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
                default -> System.out.println("Nieprawidłowy wybór.");
            }
        }
    }

    private static User findUserById(List<User> users, int id) {
        for (User user : users) {
            if (user.getId() == id) return user;
        }
        return null;
    }
}
