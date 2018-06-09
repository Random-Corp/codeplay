package com.random.app.codeplay;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface NetworkService
{
    @POST("/vision/v2.0/recognizeText?mode=Handwritten")
    @Multipart
    @Headers({"Content-Type:multipart/form-data","Ocp-Apim-Subscription-Key:***REMOVED***"})
    Call<Response<ResponseBody>> azurePostImage(@Part MultipartBody.Part imageRequestBody);


    @GET
    @Headers({"Content-Type:application/json","Ocp-Apim-Subscription-Key:***REMOVED***"})
    Call<Response<ResponseBody>> azureGetImageText(@Url String url);
}
