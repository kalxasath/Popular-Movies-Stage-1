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

package com.aiassoft.popularmovies;

/*
 * Defining a class of constants to use them from any where
 *
 */
public class Constant {
    /**
     * themoviedb.org API KEY
     */
    public static final String THEMOVIEDB_API_KEY = BuildConfig.THEMOVIEDB_API_KEY;
    /**
     * themoviedb.org URI definitions
     */
    public static final String THEMOVIEDB_BASE_URL = "http://api.themoviedb.org/3";
    public static final String THEMOVIEDB_MOVIE = "/movie";
    public static final String THEMOVIEDB_POPULAR_MOVIES = "/movie/popular";
    public static final String THEMOVIEDB_TOP_RATED_MOVIES = "/movie/top_rated";
    public static final String THEMOVIEDB_POSTER_IMAGE_BASE_URL = "http://image.tmdb.org/t/p";
    public static final String THEMOVIEDB_POSTER_IMAGE_THUMBNAIL_SIZE = "/w185";
    public static final String THEMOVIEDB_PARAM_API_KEY = "api_key";
}
