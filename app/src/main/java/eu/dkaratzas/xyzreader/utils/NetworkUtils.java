/*
 * Copyright 2018 Dionysios Karatzas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.dkaratzas.xyzreader.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.dkaratzas.xyzreader.data.models.Article;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * These utilities will be used to check for Internet connectivity and parsing JSON from the server.
 */
public class NetworkUtils {

    private static final String JSON_URL = "https://go.udacity.com/xyz-reader-json";

    /**
     * Check for a network connection
     *
     * @param context The context
     * @return is there network connectivity?
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    /**
     * Fetch the articles from the network
     *
     * @param context The context
     * @return The list of articles to display
     */
    public static List<Article> getArticles(Context context) {
        // Check for a network connection, if there is none, return an empty list
        if (!isNetworkAvailable(context)) {
            return new ArrayList<>();
        }

        List<Article> articles = null;

        try {
            articles = parseJSON(getJson(JSON_URL));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return articles;
    }

    /**
     * Request JSON from server and return them as a String
     *
     * @param url The HTTP URL
     * @return JSON String from the URL
     * @throws IOException
     */
    private static String getJson(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    /**
     * @param jsonString The String returned from getJson(String url)
     * @return A list of Article objects parsed from the JSON
     * @throws JSONException
     */
    private static List<Article> parseJSON(String jsonString) throws JSONException {
        JSONArray results = new JSONArray(jsonString);

        List<Article> articleList = new ArrayList<>();

        for (int i = 0; i < results.length(); i++) {
            JSONObject object = results.getJSONObject(i);

            Article article = new Article(object.getInt("id"),
                    object.getDouble("aspect_ratio"),
                    object.getString("thumb"),
                    object.getString("author"),
                    object.getString("photo"),
                    object.getString("title"),
                    object.getString("body"),
                    object.getString("published_date"));

            articleList.add(article);
        }

        return articleList;
    }
}
