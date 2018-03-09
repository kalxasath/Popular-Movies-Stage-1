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
 *
 * Solution for How to retain RecyclerView's position after Orientation change, while using Firebase & ChildEventListener?
 * by Drew Szurko at
 * https://stackoverflow.com/questions/42514011/how-to-retain-recyclerviews-position-after-orientation-change-while-using-fire
 */

package com.aiassoft.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aiassoft.popularmovies.model.MoviesListItem;
import com.aiassoft.popularmovies.utilities.JsonUtils;
import com.aiassoft.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        MoviesListAdapter.MoviesAdapterOnClickHandler,
        LoaderCallbacks<List<MoviesListItem>> {

    private static final String LOG_TAG = MyApp.APP_TAG + MainActivity.class.getSimpleName();

    /*
     * This ID will be used to identify the Loader responsible for loading our movies list.
     */
    private static final int MOVIES_LOADER_ID = 0;

    /*
     * Used to identify the WEB URL that is being used in the loader.loadInBackground
     * to get the movies' data from themoviedb.org
     */
    private static final String LOADER_WEB_URL_EXTRA = "web_url";

    /* The Movies List Adapter */
    private MoviesListAdapter mMoviesListAdapter;

    /* The views in the xml file */
    private RecyclerView mRecyclerView;

    private LinearLayout mErrorMessageBlock;
    private TextView mErrorMessageText;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Getting the views' references from our xml
         */
        /* The recycler view */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

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
         * The gridLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a grid.
         */
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        /* setLayoutManager associates the gridLayoutManager with our RecyclerView */
        mRecyclerView.setLayoutManager(gridLayoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        /*
         * The MoviesListAdapter is responsible for linking our movies' data with the Views that
         * will end up displaying our movie data.
         */
        mMoviesListAdapter = new MoviesListAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mMoviesListAdapter);

        /* We will check if we are connected to the internet */
        if (! NetworkUtils.isOnline()) {
            /* We are not connected, show the Error Block
             * with the propriety error message
             */
            showErrorMessage(R.string.error_check_your_network_connectivity);
        } else {
            /* Otherwise fetch movies' data from the internet */
            fetchMoviesList();
        }
    } // onCreate

    /*
     * Initialize the menu, and response to its selections
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuItem = item.getItemId();

        switch (selectedMenuItem) {
            case R.id.action_sort_by_most_popular :
                /* Display the list by most popular movies */
                invalidateData();
                setTitle(getString(R.string.title_most_popular));

                /* Save the selected Sort By method, will be used to fetch the appropriate
                 * list of movies
                 */
                NetworkUtils.moviesListSortBy = NetworkUtils.theMovieDbSortBy.MOST_POPULAR;
                fetchMoviesList();
                return true;

            case R.id.action_sort_by_highest_rated :
                /* Display the list by highest rated movies */
                invalidateData();
                setTitle(getString(R.string.title_highest_rated));

                /* Save the selected Sort By method, will be used to fetch the appropriate
                 * list of movies
                 */
                NetworkUtils.moviesListSortBy = NetworkUtils.theMovieDbSortBy.HIGHEST_RATED;
                fetchMoviesList();
                return true;
        }

        return super.onOptionsItemSelected(item);
    } // onOptionsItemSelected

    /**
     * Fetch the movies' data from themoviedb.org
     */
    private void fetchMoviesList() {
        /* Create a bundle to pass the web url to the loader */
        Bundle webUrlBundle = new Bundle();
        webUrlBundle.putString(LOADER_WEB_URL_EXTRA,
                NetworkUtils.buildMoviesListUrl().toString());

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity/fragment is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<MoviesListItem>> theMovieDbLoader = loaderManager.getLoader(MOVIES_LOADER_ID);

        if (theMovieDbLoader == null) {
            loaderManager.initLoader(MOVIES_LOADER_ID, webUrlBundle, this);
        } else {
            loaderManager.restartLoader(MOVIES_LOADER_ID, webUrlBundle, this);
        }

    } // fetchMoviesList

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param loaderArgs The WEB URL for fetching the movies' data.
     *
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<List<MoviesListItem>> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<List<MoviesListItem>>(this) {

            /* This MoviesListItem array will hold and help cache our movies list data */
            List<MoviesListItem> mCachedMoviesListData = null;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (loaderArgs == null) {
                    return;
                }

                if (mCachedMoviesListData != null) {
                    deliverResult(mCachedMoviesListData);
                } else {
                    /* If Error Message Block is visible, hide it */
                    if (mErrorMessageBlock.getVisibility() == View.VISIBLE) {
                        showMoviesListView();
                    }
                    mLoadingIndicator.setVisibility(View.VISIBLE);

                    forceLoad();
                }
            } // onStartLoading

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * from themoviedb.org in the background.
             *
             * @return Movies' data from themoviedb.org as a List of MoviesListItem.
             *         null if an error occurs
             */
            @Override
            public List<MoviesListItem> loadInBackground() {

                // Get the WEB URL that is passed on loader initialization */
                String loaderWebUrlString = loaderArgs.getString(LOADER_WEB_URL_EXTRA);
                if (loaderWebUrlString == null || TextUtils.isEmpty(loaderWebUrlString)) {
                    /* If null or empty string is passed, return immediately */
                    return null;
                }

                /* try to fetch the data from the network */
                try {
                    URL theMovieDbUrl = new URL(loaderWebUrlString);

                    /* if succeed returns a List of MoviesListItem objects */
                    return JsonUtils.parseMoviesListJson(
                            NetworkUtils.getResponseFromHttpUrl(theMovieDbUrl));

                } catch (Exception e) {
                    /* If fails returns null to significate the error */
                    e.printStackTrace();
                    return null;
                }
            } // loadInBackground

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(List<MoviesListItem> data) {
                mCachedMoviesListData = data;
                super.deliverResult(data);
            } // deliverResult

        }; // AsyncTaskLoader

    } // Loader

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<List<MoviesListItem>> loader, List<MoviesListItem> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        /* Update the adapters data with the new one */
        mMoviesListAdapter.setMoviesData(data);

        /* Check for error */
        if (null == data) {
            /* If an error has occurred, show the error message */
            showErrorMessage(R.string.unexpected_fetch_error);
        } else {
            /* Else show the movies list */
            showMoviesListView();
        }
    } // onLoadFinished

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<List<MoviesListItem>> loader) {
        /*
         * We aren't using this method in this application, but we are required to Override
         * it to implement the LoaderCallbacks<List<MoviesListItem>> interface
         */
    }

    /**
     * This method is used when we need to reset the data
     */
    private void invalidateData() {
        mMoviesListAdapter.invalidateData();
    }

    /**
     * This method is for responding to clicks from our list.
     *
     * @param movieId the Id from the selected movie
     */
    @Override
    public void onClick(int movieId) {
        /* Prepare to call the detail activity, to show the movie's details */
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE_ID, movieId);
        startActivity(intent);
    }

    /**
     * This method will make the View for the movies list visible and
     * hides the error message block.
     */
    private void showMoviesListView() {
        /* First, make sure the error block is invisible */
        mErrorMessageBlock.setVisibility(View.INVISIBLE);
        /* Then, make sure the movies list is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    } // showMoviesListView

    /**
     * This method will make the error message visible,
     * populate the error message with the corresponding error message block,
     * and hides the movie details.
     * @param errorId The error message string id
     */
    private void showErrorMessage(int errorId) {
        /* First, hide the currently visible movies list */
        mRecyclerView.setVisibility(View.INVISIBLE);
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
             * show the Movies List to hide the error block, and
             * fetch movies' data from the internet
             */
            showMoviesListView();
            fetchMoviesList();
        }
    } // onRefreshButtonClick


}
