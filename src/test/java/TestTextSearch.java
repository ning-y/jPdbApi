import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import io.ningyuan.jPdbApi.TextSearch;
import java.io.IOException;

class TestForRuntimeErrors {
    @Test
    void constructor1Works() {
        TextSearch textSearch = new TextSearch("cyclosporine");
        assertEquals("cyclosporine", textSearch.value);
        assertEquals(25, textSearch.pageSize);  // 25 is the default
    }

    @Test
    void constructor2Works() {
        TextSearch textSearch = new TextSearch("cyclosporine", 5);
        assertEquals("cyclosporine", textSearch.value);
        assertEquals(5, textSearch.pageSize);
    }

    @Test
    void getPageNoExceptions() {
        TextSearch textSearch = new TextSearch("cyclosporine");
        assertDoesNotThrow(() -> textSearch.getPage());
    }

    @Test
    void getPageNonEmpty() throws IOException {
        TextSearch textSearch = new TextSearch("cyclosporine");
        assertNotEquals(textSearch.getPage().length, 0);
    }

    @Test
    void nextPageWorksOffline() {
        TextSearch textSearch = new TextSearch("cyclosporine");
        assertEquals(textSearch.pageCursor, 0);
        textSearch.nextPage();
        assertEquals(textSearch.pageCursor, textSearch.pageSize);
        textSearch.nextPage();
        assertEquals(textSearch.pageCursor, 2 * textSearch.pageSize);
    }

    @Test
    void nextPageWorksOnline() throws IOException {
        TextSearch textSearchBig = new TextSearch("cyclosporine", 4);
        String[] idxs_big = textSearchBig.getPage();

        TextSearch textSearchSmall = new TextSearch("cyclosporine", 2);
        String[] idxs_small1 = textSearchSmall.getPage();
        textSearchSmall.nextPage();
        String[] idxs_small2 = textSearchSmall.getPage();

        assertEquals(idxs_big[0], idxs_small1[0]);
        assertEquals(idxs_big[1], idxs_small1[1]);
        assertEquals(idxs_big[2], idxs_small2[0]);
        assertEquals(idxs_big[3], idxs_small2[1]);
    }
}
