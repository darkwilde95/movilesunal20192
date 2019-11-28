package com.example.mapapp;

import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;


public class GsonRequest<ApiResponse> extends Request<ApiResponse> {

    private final Gson gson = new Gson();
    private final Class<ApiResponse> clazz;
    private final Response.Listener<ApiResponse> listener;

    public GsonRequest(
        String url,
        Class<ApiResponse> clazz,
        Response.Listener<ApiResponse> listener,
        @Nullable Response.ErrorListener errorListener) {

        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.listener = listener;
    }

    @Override
    protected Response<ApiResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data);
            return Response.success(gson.fromJson(json, clazz), null);
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(ApiResponse response) {
        listener.onResponse(response);
    }
}
