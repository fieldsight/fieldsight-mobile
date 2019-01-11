package org.odk.collect.android.utilities;

import org.bcss.collect.android.upload.InstanceGoogleSheetsUploader;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InstanceGoogleSheetsUploaderTaskTest {
    @Test
    public void gpsLocationRegexTests() {
        assertFalse(InstanceGoogleSheetsUploader.isLocationValid("{}{"));
        assertFalse(InstanceGoogleSheetsUploader.isLocationValid("28"));
        assertFalse(InstanceGoogleSheetsUploader.isLocationValid("-@123"));
        assertFalse(InstanceGoogleSheetsUploader.isLocationValid(";'[@123"));
        assertFalse(InstanceGoogleSheetsUploader.isLocationValid("*&1w345"));
        assertFalse(InstanceGoogleSheetsUploader.isLocationValid("41 24.2028, 2 10.4418"));
        assertFalse(InstanceGoogleSheetsUploader.isLocationValid("41.40338"));
        assertTrue(InstanceGoogleSheetsUploader.isLocationValid("-9.9 -9.9 -9.9 9.9"));
        assertTrue(InstanceGoogleSheetsUploader.isLocationValid("-0.0 0.8 -9.7 9.9"));
        assertTrue(InstanceGoogleSheetsUploader.isLocationValid("8.0 0.8 8.7 8.9"));
    }
}