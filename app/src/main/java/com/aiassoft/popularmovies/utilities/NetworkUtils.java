/**
 * Copyright (C) 2018 by George Vrynios
 * This project was made under the supervision of Udacity
 * in the Android Developer Nanodegree Program
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aiassoft.popularmovies.utilities;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.aiassoft.popularmovies.Constant;
import com.aiassoft.popularmovies.MyApp;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {
    private static final String LOG_TAG = MyApp.APP_TAG + NetworkUtils.class.getSimpleName();

    /* Holds the selected Sort By type for the Movies List */
    public static theMovieDbSortBy moviesListSortBy = theMovieDbSortBy.MOST_POPULAR;

    /**
     * The Sort By Enums,
     * with the method getUrl to get the right web url based on the selected Sort By type
     */
    public enum theMovieDbSortBy {
        MOST_POPULAR,
        HIGHEST_RATED;

        /**
         * Get the right web url based on the selected Sort By type
         *
         * @return The web url based on the selected Sort By type
         */
        String getUrl() {
            switch (this) {
                case MOST_POPULAR:
                    return Constant.THEMOVIEDB_BASE_URL + Constant.THEMOVIEDB_POPULAR_MOVIES;
                case HIGHEST_RATED:
                    return Constant.THEMOVIEDB_BASE_URL + Constant.THEMOVIEDB_TOP_RATED_MOVIES;
                default:
                    throw new AssertionError("Unknown Sort By " + this);
            }
        }
    }

    /**
     * Builds the Movie Poster URL
     *
     * @param posterPath The Poster Path, as given in themoviedb.org
     * @return           The URL to download the Movie Poster
     */
    public static String buildPosterUrl(String posterPath) {
        String url = Constant.THEMOVIEDB_POSTER_IMAGE_BASE_URL
                + Constant.THEMOVIEDB_POSTER_IMAGE_THUMBNAIL_SIZE
                + posterPath;
        Log.d(LOG_TAG, "buildPosterUrl= " + url);
        return url;
    }

    /**
     * Builds the URL to download the Movie's data
     *
     * @param movieId The Movie Id, as given in themoviedb.org
     * @return        The URL to get the Movie's data
     */
    public static URL buildMovieUrl(int movieId) {
        Uri builtUri = Uri.parse(Constant.THEMOVIEDB_BASE_URL
                + Constant.THEMOVIEDB_MOVIE
                + "/" + Integer.toString(movieId)).buildUpon()
                .appendQueryParameter(Constant.THEMOVIEDB_PARAM_API_KEY,
                        Constant.THEMOVIEDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, "Build Movie URI" + url);

        return url;
    }

    /**
     * Builds the URL to download the Movies List
     *
     * @return The URL to get the Movies List from the themoviedb.org
     */
    public static URL buildMoviesListUrl() {
        Uri builtUri = Uri.parse(moviesListSortBy.getUrl()).buildUpon()
                .appendQueryParameter(Constant.THEMOVIEDB_PARAM_API_KEY,
                        Constant.THEMOVIEDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, "Build Movies List URI" + url);

        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     *
     * @return TRUE, if we are connected to the internet, otherwise returns FALSE
     */
    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) MyApp.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}