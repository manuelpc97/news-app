package com.example.android.news_app.Casting;

import android.content.Context;

import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;

import java.util.ArrayList;
import java.util.List;

public class CastOptionsProvider implements OptionsProvider {
    public String CUSTOM_NAMESPACE = "urn:x-cast:custom_namespace";
    @Override
    public CastOptions getCastOptions(Context context) {
        List<String> supportedNamespaces = new ArrayList<>();
        supportedNamespaces.add(CUSTOM_NAMESPACE);
        CastOptions castOptions = new CastOptions.Builder().setReceiverApplicationId(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID).build();
        return castOptions;
    }

    @Override
    public List<SessionProvider> getAdditionalSessionProviders(Context context) {
        return null;
    }
}
