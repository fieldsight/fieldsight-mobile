package org.odk.collect.android.location.activities;

import org.fieldsight.collect.android.R;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.odk.collect.android.activities.GeoPointMapActivity;
import org.odk.collect.android.geo.MapPoint;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import static android.app.Activity.RESULT_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.odk.collect.android.activities.FormEntryActivity.LOCATION_RESULT;
import static org.odk.collect.android.location.LocationTestUtils.createLocation;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class GeoPointMapActivityTest extends BaseGeoActivityTest {
    @Rule public MockitoRule rule = MockitoJUnit.rule();
    private ActivityController<GeoPointMapActivity> controller;

    @Before public void setUp() throws Exception {
        super.setUp();
        controller = Robolectric.buildActivity(GeoPointMapActivity.class, intent);
    }

    @Test public void shouldReturnPointFromSecondLocationFix() {
        GeoPointMapActivity activity = controller.create().start().resume().visible().get();

        // The very first fix is ignored.
        fakeLocationClient.receiveFix(createLocation("GPS", 1, 2, 3, 4f));
        assertEquals(activity.getString(R.string.please_wait_long), activity.getLocationStatus());

        // The second fix changes the status message.
        fakeLocationClient.receiveFix(createLocation("GPS", 5, 6, 7, 8f));
        assertEquals(activity.formatLocationStatus("gps", 8f), activity.getLocationStatus());

        // When the user clicks the "Save" button, the fix location should be returned.
        activity.findViewById(R.id.accept_location).performClick();
        assertTrue(activity.isFinishing());
        assertEquals(RESULT_OK, shadowOf(activity).getResultCode());
        String result = shadowOf(activity).getResultIntent().getStringExtra(LOCATION_RESULT);
        assertEquals(activity.formatResult(new MapPoint(5, 6, 7, 8)), result);
    }
}
