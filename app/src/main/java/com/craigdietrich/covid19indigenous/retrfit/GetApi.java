package com.craigdietrich.covid19indigenous.retrfit;


import com.craigdietrich.covid19indigenous.common.Constant;
import com.craigdietrich.covid19indigenous.model.CultureVo;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetApi {

    @GET(Constant.CULTURE)
    Call<List<CultureVo>> getCultureManifest(
            @Query("t") String t
    );

    @GET(Constant.QUESTIONS)
    Call<ResponseBody> getQuestions(
            @Query("key") String key,
            @Query("t") String t
    );
}
