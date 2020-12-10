package com.craigdietrich.covid19indigenous.retrfit;


import com.craigdietrich.covid19indigenous.model.CultureVo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetApi {

    @GET("feeds/content/manifest.json?")
    Call<List<CultureVo>> getCultureManifest(
            @Query("t") String t,
            @Header("Cookie") String cookie
    );

    @GET("dashboard/pages/app?")
    Call<Object> getQuestions(
            @Path("key") String key,
            @Path("t") String t
    );
}
