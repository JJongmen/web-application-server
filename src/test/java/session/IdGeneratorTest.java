package session;

import org.junit.Test;

import static org.junit.Assert.*;

public class IdGeneratorTest {

    @Test
    public void createSessionId() throws Exception {
        IdGenerator generator = new IdGenerator();
        String id = generator.generateId(26);
        System.out.println(id);
    }

}