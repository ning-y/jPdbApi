package io.ningyuan.jPdbApi;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Pdb {
    private static final String GET_ENTRY_BY_ID_URL = "https://data.rcsb.org/rest/v1/core/entry/%s";
    private static final String DOWNLOAD_BY_ID_URL = "https://files.rcsb.org/download/%s.pdb";

    private String structureId;
    private String title;

    public Pdb(String structureId) {
        this.structureId = structureId;
    }

    /**
     * Access the <a href="https://data.rcsb.org/redoc/index.html">RCSB REST API</a>
     * to load this entry's <code>title</code>. For example, the
     * <code>title</code> for the entry with <code>structureId</code>
     * <code>"1B9G"</code> is <code>"INSULIN-LIKE-GROWTH-FACTOR-1"</code>.
     */
    public void load() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
            .url(String.format(this.GET_ENTRY_BY_ID_URL, this.structureId))
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            JsonEntryResults results = new Gson().fromJson(
                response.body().string(), JsonEntryResults.class);
            this.title = results.struct.title.trim();
        }
    }

    /**
     * Get an InputStream for this entry's PDB file.
     */
    public InputStream getInputStream() throws IOException {
        URL url = new URL(String.format(DOWNLOAD_BY_ID_URL, this.structureId));
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }

    public String getStructureId() {
        return this.structureId;
    }

    /**
     * Get the descriptor title for this entry. Make sure to call
     * <code>load</code> first, or this will return <code>null</code>.
     */
    public String getTitle() {
        return this.title;
    }

    public String toString() {
        return String.format("[%s] %s", this.getStructureId(), this.getTitle());
    }
}


/* The following are static classes to help Gson parse JSON responses from the
   /core/entry/{entry_id} endpoint.
   https://data.rcsb.org/redoc/index.html#operation/getEntryById */

class JsonEntryResults {
    JsonStruct struct;
}

class JsonStruct {
    String title;
}
