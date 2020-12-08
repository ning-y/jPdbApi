import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import io.ningyuan.jPdbApi.Pdb;
import java.io.IOException;

class PdbTests {
    @Test
    void constructorWorks() {
        Pdb pdb = new Pdb("1B9G");
    }

    @Test
    void loadWorks() throws IOException {
        Pdb pdb = new Pdb("1B9G");
        pdb.load();
        assertEquals(pdb.getTitle(), "INSULIN-LIKE-GROWTH-FACTOR-1");
    }
}
