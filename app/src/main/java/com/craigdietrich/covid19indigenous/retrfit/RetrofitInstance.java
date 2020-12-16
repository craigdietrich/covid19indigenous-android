package com.craigdietrich.covid19indigenous.retrfit;

import com.craigdietrich.covid19indigenous.common.Constant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit retrofit;

    /**
     * Create an instance of Retrofit object
     */


    public static Retrofit getRetrofitInstance() {

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setLenient()
                .setPrettyPrinting()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
