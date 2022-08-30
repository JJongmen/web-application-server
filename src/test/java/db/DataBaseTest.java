package db;

import model.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class DataBaseTest {

    private DataBase dataBase;

    @Before
    public void before() {
        dataBase = new DataBase();
    }

    @Test
    public void addUser() throws Exception {
        User user = new User("test", "1234", "jyp", "abc@gmail.com");

        dataBase.addUser(user);

        User findUser = dataBase.findUserById(user.getUserId());
        assertEquals(findUser, user);
    }

    @Test
    public void findAll() throws Exception {
        User user1 = new User("test1", "1234", "jyp", "abc@gmail.com");
        User user2 = new User("test2", "12345", "jypjyp", "abcd@gmail.com");
        dataBase.addUser(user1);
        dataBase.addUser(user2);

        Collection<User> users = dataBase.findAll();

        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

}