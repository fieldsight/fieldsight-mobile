package org.odk.collect.android;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;

import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.common.SharedPreferenceUtils;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.odk.collect.android.dao.InstancesDao;

import static org.bcss.collect.naxa.common.Constant.FormDeploymentFrom.PROJECT;
import static org.junit.Assert.assertEquals;

@SmallTest
public class SharedPreferenceUtilsTest {

    private Context context;
    private GeneralForm generalForm;

    @Before
    public void before() {
        context = InstrumentationRegistry.getTargetContext();
        generalForm = new GeneralForm();
        generalForm.setSiteId("12");
        generalForm.setFormDeployedFrom(Constant.FormDeploymentFrom.PROJECT);
        generalForm.setFsFormId("9876");
    }

    @Test
    public void putAndGetSubmissionUrl() {
        String url = InstancesDao.generateSubmissionUrl(PROJECT, generalForm.getSiteId(), generalForm.getFsFormId());
        SharedPreferenceUtils.saveToPrefs(context, SharedPreferenceUtils.PREF_VALUE_KEY.KEY_URL, url);

        String cachedUrl = SharedPreferenceUtils.getFromPrefs(context, SharedPreferenceUtils.PREF_VALUE_KEY.KEY_URL, "");
        assertEquals(url, cachedUrl);
    }


    @Test
    public void putAndGetSiteId() {
        String siteId = SharedPreferenceUtils.getFromPrefs(context, SharedPreferenceUtils.PREF_VALUE_KEY.KEY_SITE_ID, "");
        String cachedSiteId = SharedPreferenceUtils.getFromPrefs(context, SharedPreferenceUtils.PREF_VALUE_KEY.KEY_SITE_ID, "");
        assertEquals(siteId, "");
    }

    @After
    public void after() {
        SharedPreferenceUtils.deleteAll(context);
    }

}
