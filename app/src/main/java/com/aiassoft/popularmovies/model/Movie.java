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

package com.aiassoft.popularmovies.model;

import com.aiassoft.popularmovies.MyApp;

/**
 * The Movie Object, holds all the necessary information of a movie
 */
public class Movie {
    private static final String LOG_TAG = MyApp.APP_TAG + Movie.class.getSimpleName();

    private int id;
    private String originalTitle;
    private String posterPath;
    private String overview;
    private String voteAverage;
    private String releaseDate;
    private String runTime;

    /**
     * No args constructor for use in serialization
     */
    public Movie() {
    }


    /**
     * Constructor to initialize all the class fields from the parameters
     * @param id            The Movie Id
     * @param originalTitle The Original Title
     * @param posterPath    The Poster Path / movie poster image thumbnail
     * @param overview      The Overview / plot synopsis
     * @param voteAverage   The Vote Average / user rating
     * @param releaseDate   The Year of the Release Date
     */
    public Movie(int id, String originalTitle, String posterPath, String overview,
                 String voteAverage, String releaseDate) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() { return releaseDate; }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRunTime() { return runTime; }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }


}
