package org.bcss.collect.naxa.demo;

import android.util.Pair;

import com.google.gson.Gson;

import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.login.model.SiteBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.reactivex.Observable;

public class RawAssetLoader {

    private String rawResourceToInputStream(String assetName) throws IOException {
        InputStream jsonStream = Collect.getInstance().getAssets().open(assetName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();

    }

    public Observable<Site> loadTextFromAsset(String assetName) {
        return Observable.create(e -> {
            try {
                String json = rawResourceToInputStream(assetName);
                PointGeoJSON pointGeoJSON = new Gson().fromJson(json, PointGeoJSON.class);
                for (FeaturesItem featuresItem : pointGeoJSON.getFeatures()) {
                    String name = featuresItem.getProperties().getName();
                    String identifier = featuresItem.getProperties().getIdentifier();
                    Double lat = featuresItem.getGeometry().getCoordinates().get(0);
                    Double lon = featuresItem.getGeometry().getCoordinates().get(1);
                    String id = String.valueOf(featuresItem.getId());


                    Site site = new SiteBuilder().setName(name)
                            .setProject("183")
                            .setIdentifier(identifier)
                            .setLatitude(String.valueOf(lat))
                            .setLongitude(String.valueOf(lon))
                            .setId(id)
                            .createSite();

                    e.onNext(site);
                }
                e.onComplete();
            } catch (Exception exception) {
                e.onError(exception);
            }
        });
    }


}
