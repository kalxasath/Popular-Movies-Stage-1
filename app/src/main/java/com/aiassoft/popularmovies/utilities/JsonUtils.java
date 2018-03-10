package com.aiassoft.popularmovies.utilities;

import android.util.Log;

import com.aiassoft.popularmovies.MyApp;
import com.aiassoft.popularmovies.model.Movie;
import com.aiassoft.popularmovies.model.MoviesListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON utilities helper class
 */
public class JsonUtils {
    private static final String LOG_TAG = MyApp.APP_TAG + JsonUtils.class.getSimpleName();

    /**
     * This method will take a json string as input and use that
     * json data to build a Movie object
     * @param json The Movie data in json format as string
     * @return     The Movie Object
     */
    public static Movie parseMoviesJson(String json) {
        Log.d(LOG_TAG, json);
        Movie movie = new Movie();

        try {
            /** Creates a new JSONObject with name/value mappings from the json string */
            JSONObject moviesData = new JSONObject(json);

            movie.setPosterPath(moviesData.optString("poster_path"));
            movie.setOriginalTitle(moviesData.optString("original_title"));
            movie.setOverview(moviesData.optString("overview"));
            /* special handling for invalid release date */
            String releaseDate = moviesData.optString("release_date");
            if (releaseDate.length() >= 4) {
                movie.setReleaseDate(releaseDate.substring(0, 4));
            }
            movie.setVoteAverage(Double.toString(moviesData.optDouble("vote_average")));
            movie.setRunTime(Integer.toString(moviesData.optInt("runtime")));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movie;
    }

    /**
     * This method will take a json string as input and use that
     * json data to build a ArrayList of MoviesListItem objects
     * @param json The Movies List data in json format as string
     * @return     The ArrayList of MoviesListItem objects
     */
    public static List<MoviesListItem> parseMoviesListJson(String json) {
        Log.d(LOG_TAG, json);

        /* ArrayList to hold the movies list items */
        List<MoviesListItem> moviesListItems = new ArrayList<MoviesListItem>();
        MoviesListItem moviesListItem;

        try {
            /** Creates a new JSONObject with name/value mappings from the json string */
            JSONObject moviesData = new JSONObject(json);

            /** Get the movies' data array */
            JSONArray arrResults =  moviesData.getJSONArray("results");

            int maxResults = arrResults.length();
            for (int i=0; i<maxResults; i++) {
                /** Get the movie's data */
                JSONObject movie = arrResults.getJSONObject(i);

                moviesListItem = new MoviesListItem();
                moviesListItem.setId(movie.optInt("id"));
                moviesListItem.setPosterPath(movie.optString("poster_path"));

                moviesListItems.add(moviesListItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return moviesListItems;
    }
}
