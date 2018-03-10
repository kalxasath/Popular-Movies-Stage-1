/*
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

package com.aiassoft.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aiassoft.popularmovies.model.Movie;
import com.aiassoft.popularmovies.utilities.JsonUtils;
import com.aiassoft.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;

/**
 * Created by gvryn on 04/03/18.
 * Displays the details of a movie
 */
public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = MyApp.APP_TAG + DetailActivity.class.getSimpleName();

    /* Identifies the incoming parameter of the movie id */
    public static final String EXTRA_MOVIE_ID = "movie_id";

    /* If there is not a movie id, this id will as the default one */
    private static final int DEFAULT_MOVIE_ID = -1;

    private static Context mContext = null;
    private static Movie mMovie = null;
    private static int mMovieId = DEFAULT_MOVIE_ID;

    /* The views in the xml file */
    private ScrollView mMovieDetails;
    private ImageView mMoviePoster;
    private TextView mOriginalTitle;
    private TextView mOverview;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private TextView mRuntime;

    private LinearLayout mErrorMessageBlock;
    private TextView mErrorMessageText;

    private ProgressBar mLoadingIndicator;


    /**
     * Creates the detail activity
     * @param savedInstanceState The saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_detail);

        /* should be called from another activity, if not show error toast and return */
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        /* Intent parameter should be a valid movie id, if not show error toast and return */
        mMovieId = intent.getIntExtra(EXTRA_MOVIE_ID, DEFAULT_MOVIE_ID);
        if (mMovieId == DEFAULT_MOVIE_ID) {
            // EXTRA_MOVIE_ID not found in intent's parameter
            closeOnError();
        }

        /*
         * The ProgressBar that will indicate to the user that we are loading data.
         * It will be hidden when no data is loading.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* The Error Message Block,
         * is used to display errors and will be hidden if there are no error
         */
        mErrorMessageBlock = (LinearLayout) findViewById(R.id.ll_error_message);

        /* The view holding the error message */
        mErrorMessageText = (TextView) findViewById(R.id.tv_error_message_text);

        /*
         * The movie's details views
         */
        mMovieDetails = (ScrollView) findViewById(R.id.sv_movie_details);
        mMoviePoster = (ImageView) findViewById(R.id.iv_movie_poster);
        mOriginalTitle = (TextView) findViewById(R.id.tv_original_title);
        mOverview = (TextView) findViewById(R.id.tv_overview);
        mReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mVoteAverage = (TextView) findViewById(R.id.tv_vote_average);
        mRuntime = (TextView) findViewById(R.id.tv_runtime);

        /* We will check if we are connected to the internet */
        if (! NetworkUtils.isOnline()) {
            /* We are not connected, show the Error Block
             * with the propriety error message
             */
            showErrorMessage(R.string.error_check_your_network_connectivity);
        } else {
            /* Otherwise fetch movies' data from the internet */
            fetchMoviesDetails();
        }

    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void fetchMoviesDetails() {
        new TheMovieDbQueryTask().execute(NetworkUtils.buildMovieUrl(mMovieId));
    }

    @Override
    protected void onStop() {
        invalidateActivity();
        super.onStop();
    }

    private void invalidateActivity() {
        mContext = null;
        mMovie = null;
        mMovieId = DEFAULT_MOVIE_ID;
    }

    /**
     * This method will take a movie object as input
     * and use that object's data to populate the UI.
     */
    private void populateUI() {
        Picasso.with(mContext)
                .load(NetworkUtils.buildPosterUrl(mMovie.getPosterPath()))
                .into(mMoviePoster);

        mOriginalTitle.setText(mMovie.getOriginalTitle());
        mOverview.setText(mMovie.getOverview());
        mReleaseDate.setText(mMovie.getReleaseDate());
        mVoteAverage.setText(mMovie.getVoteAverage() + MyApp.mMaxRating);
        mRuntime.setText(mMovie.getRunTime() + MyApp.mMinutes);
    }

    public class TheMovieDbQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL movieUrl = urls[0];
            String movieData = null;

            try {
                movieData = NetworkUtils.getResponseFromHttpUrl(movieUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return movieData;
        } // doInBackground

        @Override
        protected void onPostExecute(String s) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (s != null && !s.equals("")) {
                Log.d(LOG_TAG, s);

                /* onStop() invalidates mContext & mMovie */
                if (mContext != null) {
                    mMovie = JsonUtils.parseMoviesJson(s);

                    if (mMovie != null) {
                        populateUI();
                    }
                }
            } else {
                showErrorMessage(R.string.unexpected_fetch_error);
            }
        } // onPostExecute

    } // class TheMovieDbQueryTask


    /**
     * This method will make the View for the movie's details visible and
     * hides the error message block.
     */
    private void showMoviesDetails() {
        /* First, make sure the error block is invisible */
        mErrorMessageBlock.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie details are visible */
        mMovieDetails.setVisibility(View.VISIBLE);
    } // showMoviesDetails

    /**
     * This method will make the error message block visible,
     * populate the error message with the corresponding error message,
     * and hides the movie details.
     * @param errorId The error message string id
     */
    private void showErrorMessage(int errorId) {
        /* First, hide the currently visible movie details */
        mMovieDetails.setVisibility(View.INVISIBLE);
        /* Then, show the error block */
        mErrorMessageBlock.setVisibility(View.VISIBLE);
        /* Show the corresponding error message */
        mErrorMessageText.setText(getString(errorId));
    } // showErrorMessage

    /**
     * Called when a tap occurs in the refresh button
     * @param view The view which reacted to the click
     */
    public void onRefreshButtonClick(View view) {
        /* Again check if we are connected to the internet */
        if (NetworkUtils.isOnline()) {
            /* If the network connectivity is restored
             * show the Movie Details to hide the error block, and
             * fetch movies' data from the internet
             */
            showMoviesDetails();
            fetchMoviesDetails();
        }
    } // onRefreshButtonClick

}
