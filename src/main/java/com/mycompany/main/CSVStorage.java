/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.main;

import java.io.*;
import java.util.*;

public class CSVStorage {
    private final String filePath;

    public CSVStorage(String filePath) {
        this.filePath = filePath;
    }

    public List<User> readUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Plik nie istnieje, zostanie utworzony przy zapisie.");
            return users;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // nagłówek
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) continue;
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String email = parts[2].trim();
                users.add(new User(id, name, email));
            }
        } catch (IOException e) {
            System.out.println("Błąd odczytu pliku: " + e.getMessage());
        }
        return users;
    }

    public void writeUsers(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("id,name,email\n");
            for (User user : users) {
                bw.write(user.toString() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Błąd zapisu do pliku: " + e.getMessage());
        }
    }
}
