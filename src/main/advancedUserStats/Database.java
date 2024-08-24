package main.advancedUserStats;

import fileio.input.UserInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
public class Database {
    private static Database instance;
    private final ArrayList<UserStats> listaRezUsers = new ArrayList<>();

    private Database(ArrayList<UserInput> users) {
        for (UserInput user : users) {
            UserStats newUser = new UserStats();
            newUser.setName(user.getUsername());
            newUser.setAge(user.getAge());
            newUser.setCity(user.getCity());
            listaRezUsers.add(newUser);
        }
    }

    public static Database getInstance(ArrayList<UserInput> users, Boolean newDatabase) {
        if (instance == null || newDatabase) {
            instance = new Database(users);
        }
        return instance;
    }

    public ArrayList<UserStats> getListaRezUsers() {
        return listaRezUsers;
    }

    public UserStats findByName(String name) {
        for (UserStats user : listaRezUsers) {
            if (user.getName().equals(name)) return user;
        }
        return null;
    }
}
