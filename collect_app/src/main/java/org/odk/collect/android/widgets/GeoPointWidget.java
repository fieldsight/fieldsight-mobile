/*
 * Copyright (C) 2009 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.widgets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fieldsight.collect.android.R;
import org.javarosa.core.model.data.GeoPointData;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.activities.GeoPointActivity;
import org.odk.collect.android.activities.GeoPointMapActivity;
import org.odk.collect.android.geo.MapProvider;
import org.odk.collect.android.listeners.PermissionListener;
import org.odk.collect.android.preferences.GeneralKeys;
import org.odk.collect.android.utilities.PlayServicesUtil;
import org.odk.collect.android.utilities.WidgetAppearanceUtils;
import org.odk.collect.android.widgets.interfaces.BinaryWidget;

import java.text.DecimalFormat;

import static org.odk.collect.android.utilities.ApplicationConstants.RequestCodes;
import static org.odk.collect.android.utilities.WidgetAppearanceUtils.MAPS;
import static org.odk.collect.android.utilities.WidgetAppearanceUtils.PLACEMENT_MAP;
import static org.odk.collect.android.utilities.WidgetAppearanceUtils.hasAppearance;

/**
 * GeoPointWidget is the widget that allows the user to get GPS readings.
 *
 * @author Carl Hartung (carlhartung@gmail.com)
 * @author Yaw Anokwa (yanokwa@gmail.com)
 * @author Jon Nordling (jonnordling@gmail.com)
 */
@SuppressLint("ViewConstructor")
public class GeoPointWidget extends QuestionWidget implements BinaryWidget {
    public static final String LOCATION = "gp";
    public static final String ACCURACY_THRESHOLD = "accuracyThreshold";
    public static final String READ_ONLY = "readOnly";
    public static final String DRAGGABLE_ONLY = "draggable";

    public static final double DEFAULT_LOCATION_ACCURACY = 5.0;
    private final boolean readOnly;
    private final Button getLocationButton;
    private final TextView answerDisplay;
    private boolean useMap;
    private final double accuracyThreshold;
    private boolean draggable = true;

    private String stringAnswer;

    public GeoPointWidget(Context context, FormEntryPrompt prompt) {
        super(context, prompt);

        // Determine the accuracy threshold to use.
        String acc = prompt.getQuestion().getAdditionalAttribute(null, ACCURACY_THRESHOLD);
        if (acc != null && acc.length() != 0) {
            accuracyThreshold = Double.parseDouble(acc);
        } else {
            accuracyThreshold = DEFAULT_LOCATION_ACCURACY;
        }

        // Determine whether to use the map and whether the point should be draggable.
        useMap = false;
        if (MapProvider.getConfigurator().isAvailable(context)) {
            if (hasAppearance(prompt, PLACEMENT_MAP)) {
                draggable = true;
                useMap = true;
            } else if (hasAppearance(prompt, MAPS)) {
                draggable = false;
                useMap = true;
            }
        }

        readOnly = prompt.isReadOnly();
        answerDisplay = getCenteredAnswerTextView();

        getLocationButton = getSimpleButton(R.id.get_location);

        // finish complex layout
        LinearLayout answerLayout = new LinearLayout(getContext());
        answerLayout.setOrientation(LinearLayout.VERTICAL);
        answerLayout.addView(getLocationButton);
        answerLayout.addView(answerDisplay);
        addAnswerView(answerLayout);

        // Set vars Label/text for button enable view or collect...
        boolean dataAvailable = false;
        String answer = prompt.getAnswerText();
        if (answer != null && !answer.equals("")) {
            dataAvailable = true;
            setBinaryData(answer);
        }
        updateButtonLabelsAndVisibility(dataAvailable);
    }

    private void updateButtonLabelsAndVisibility(boolean dataAvailable) {
        if (useMap) {
            if (readOnly) {
                getLocationButton.setText(R.string.geopoint_view_read_only);
            } else {
                getLocationButton.setText(
                    dataAvailable ? R.string.view_change_location : R.string.get_point);
            }
        } else {
            if (!readOnly) {
                getLocationButton.setText(
                    dataAvailable ? R.string.change_location : R.string.get_point);
            }
        }
    }

    @Override
    public void clearAnswer() {
        stringAnswer = null;
        answerDisplay.setText(null);
        updateButtonLabelsAndVisibility(false);
        widgetValueChanged();
    }

    @Override
    public IAnswerData getAnswer() {
        if (stringAnswer == null || stringAnswer.isEmpty()) {
            return null;
        } else {
            try {
                // segment lat and lon
                String[] sa = stringAnswer.split(" ");
                double[] gp = new double[4];
                gp[0] = Double.valueOf(sa[0]);
                gp[1] = Double.valueOf(sa[1]);
                gp[2] = Double.valueOf(sa[2]);
                gp[3] = Double.valueOf(sa[3]);

                return new GeoPointData(gp);

            } catch (Exception numberFormatException) {
                return null;
            }
        }
    }

    private String truncateDouble(String s) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(Double.valueOf(s));
    }

    private String formatGps(double coordinates, String type) {
        String location = Double.toString(coordinates);
        String degreeSign = "°";
        String degree = location.substring(0, location.indexOf('.'))
                + degreeSign;
        location = "0." + location.substring(location.indexOf('.') + 1);
        double temp = Double.valueOf(location) * 60;
        location = Double.toString(temp);
        String mins = location.substring(0, location.indexOf('.')) + "'";

        location = "0." + location.substring(location.indexOf('.') + 1);
        temp = Double.valueOf(location) * 60;
        location = Double.toString(temp);
        String secs = location.substring(0, location.indexOf('.')) + '"';
        if (type.equalsIgnoreCase("lon")) {
            if (degree.startsWith("-")) {
                degree = String.format(getContext()
                        .getString(R.string.west), degree.replace("-", ""), mins, secs);
            } else {
                degree = String.format(getContext()
                        .getString(R.string.east), degree.replace("-", ""), mins, secs);
            }
        } else {
            if (degree.startsWith("-")) {
                degree = String.format(getContext()
                        .getString(R.string.south), degree.replace("-", ""), mins, secs);
            } else {
                degree = String.format(getContext()
                        .getString(R.string.north), degree.replace("-", ""), mins, secs);
            }
        }
        return degree;
    }

    @Override
    public void setBinaryData(Object answer) {
        stringAnswer = (String) answer;

        if (stringAnswer != null && !stringAnswer.isEmpty()) {
            String[] parts = stringAnswer.split(" ");
            answerDisplay.setText(getContext().getString(
                R.string.gps_result,
                formatGps(Double.parseDouble(parts[0]), "lat"),
                formatGps(Double.parseDouble(parts[1]), "lon"),
                truncateDouble(parts[2]),
                truncateDouble(parts[3])
            ));
        } else {
            answerDisplay.setText("");
        }

        updateButtonLabelsAndVisibility(true);
        widgetValueChanged();
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        getLocationButton.setOnLongClickListener(l);
        answerDisplay.setOnLongClickListener(l);
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();
        getLocationButton.cancelLongPress();
        answerDisplay.cancelLongPress();
    }

    @Override
    public void onButtonClick(int buttonId) {
        getPermissionUtils().requestLocationPermissions((Activity) getContext(), new PermissionListener() {
            @Override
            public void granted() {
                startGeoPoint();
            }

            @Override
            public void denied() {
            }
        });
    }

    private void startGeoPoint() {
        Context context = getContext();
        Intent intent = new Intent(
            context, useMap ? GeoPointMapActivity.class : GeoPointActivity.class);

        if (stringAnswer != null && !stringAnswer.isEmpty()) {
            String[] sa = stringAnswer.split(" ");
            double[] gp = new double[4];
            gp[0] = Double.valueOf(sa[0]);
            gp[1] = Double.valueOf(sa[1]);
            gp[2] = Double.valueOf(sa[2]);
            gp[3] = Double.valueOf(sa[3]);
            intent.putExtra(LOCATION, gp);
        }
        intent.putExtra(READ_ONLY, readOnly);
        intent.putExtra(DRAGGABLE_ONLY, draggable);
        intent.putExtra(ACCURACY_THRESHOLD, accuracyThreshold);

        waitForData();
        ((Activity) context).startActivityForResult(intent, RequestCodes.LOCATION_CAPTURE);
    }
}
