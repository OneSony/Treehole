package com.example.treehole.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class SharedPreferencesCookieJar implements CookieJar {
    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final String COOKIES_PREF = "client_cookies";

    public SharedPreferencesCookieJar(@NonNull Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(COOKIES_PREF, Context.MODE_PRIVATE);
    }

    @Override
    public void saveFromResponse(HttpUrl url, @NonNull List<Cookie> cookies) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        HashSet<String> cookieStrings = new HashSet<>();
        for (Cookie cookie : cookies) {
            cookieStrings.add(cookie.toString());
        }
        editor.putStringSet(url.host(), cookieStrings);
        editor.apply();
    }

    @NonNull
    @Override
    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {

        List<Cookie> cookies = new ArrayList<>();

        for (String cookie: sharedPreferences.getStringSet(url.host(), new HashSet<>())){
            cookies.add(Cookie.parse(url, cookie));
        }
        return cookies;
    }
}
