package com.craigdietrich.covid19indigenous.retrfit;


import com.craigdietrich.covid19indigenous.common.Constant;
import com.craigdietrich.covid19indigenous.model.AnswerVo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GetApi {

    @GET(Constant.CULTURE)
    Call<ResponseBody> getCultureManifest(@Query("t") String t);

    @GET(Constant.QUESTIONS)
    Call<ResponseBody> getQuestions(@Query("key") String key, @Query("t") String t);

    @POST(Constant.ANSWERS)
    Call<AnswerVo> uploadAnswer(@Body AnswerVo body);
}
