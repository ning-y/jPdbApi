package io.ningyuan.jPdbApi;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TextSearch {
    public String value;
    public int pageSize = 25;
    public int pageCursor = 0;
    private JsonParameters payload;
    private final String ENDPOINT = "https://search.rcsb.org/rcsbsearch/v1/query";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public TextSearch(String value) {
        this.value = value;
        this.payload = new JsonParameters();
        this.payload.query = new JsonQuery(value);
        this.payload.request_options = new JsonRequestOptions(
            this.pageCursor, this.pageSize);
        this.payload.return_type = "entry";
    }

    public TextSearch(String value, int pageSize) {
        this(value);
        this.pageSize = pageSize;
        this.payload.request_options = new JsonRequestOptions(
            this.pageCursor, this.pageSize);
    }

    public String[] getPage() throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody jsonPayload = RequestBody.create(
            new Gson().toJson(this.payload), this.JSON);
        Request request = new Request.Builder()
            .url(this.ENDPOINT)
            .post(jsonPayload)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException("Unexpected code " + response);
            JsonResults results = new Gson().fromJson(
                response.body().string(), JsonResults.class);
            return Arrays.stream(results.result_set)
                .map(r -> r.identifier)
                // https://stackoverflow.com/a/40902361/6910451
                .toArray(String[]::new);
        }
    }

    public void nextPage() {
        this.pageCursor = this.pageCursor + this.pageSize;
        this.payload.request_options = new JsonRequestOptions(
            this.pageCursor, this.pageSize);

    }
}

/* The following are static classes to help Gson produce JSONs to the structure of:
    {
      "query": {
        "type": "terminal",
        "service": "text",
        "parameters": {
          "value": "thymidine kinase"
        }
      },
      "request_options": {
        "pager": {
          "start": 0,
          "rows": 100
        }
      },
      "return_type": "entry"
    }
*/

class JsonParameters {
    public JsonQuery query;
    public JsonRequestOptions request_options;
    public String return_type;
}

class JsonQuery {
    public String type;
    public String service;
    public JsonQueryParameters parameters;

    public JsonQuery(String value) {
        this.type = "terminal";
        this.service = "text";
        this.parameters = new JsonQueryParameters();
        this.parameters.value = value;
    }
}

class JsonQueryParameters {
    public String value;
}

class JsonRequestOptions {
    public JsonPager pager;

    public JsonRequestOptions(int start, int rows) {
        this.pager = new JsonPager();
        this.pager.start = start;
        this.pager.rows = rows;
    }
}

class JsonPager {
    public int start;
    public int rows;
}

/* The following are static classes to help Gson parse JSONs with the structure:
{
  "query_id" : "53acba80-35e8-454e-a87b-a72b9501e269",
  "result_type" : "entry",
  "total_count" : 107,
  "explain_meta_data" : {
    "total_timing" : 4,
    "terminal_node_timings" : {
      "5895" : 2
    }
  },
  "result_set" : [ {
    "identifier" : "1CSA",
    "score" : 1.0,
    "services" : [ {
      "service_type" : "text",
      "nodes" : [ {
        "node_id" : 5895,
        "original_score" : 13.633682250976562,
        "norm_score" : 1.0
      } ]
    } ]
  }, {
    "identifier" : "2X7K",
    "score" : 0.9016676210746524,
    "services" : [ {
      "service_type" : "text",
      "nodes" : [ {
        "node_id" : 5895,
        "original_score" : 13.350375175476074,
        "norm_score" : 0.9016676210746524
      } ]
    } ]
  }]
}
*/

class JsonResults {
    JsonResult[] result_set;
}

class JsonResult {
    String identifier;
}
