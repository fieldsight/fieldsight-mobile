package org.odk.collect.android.tasks;

import android.net.Uri;

import androidx.test.rule.GrantPermissionRule;

import org.odk.collect.android.dto.Instance;
import org.odk.collect.android.provider.InstanceProviderAPI;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.odk.collect.android.dao.InstancesDao;
import org.odk.collect.android.test.MockedServerTest;

import java.io.File;

import okhttp3.mockwebserver.RecordedRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.odk.collect.android.test.TestUtils.assertMatches;
import static org.odk.collect.android.test.TestUtils.cleanUpTempFiles;
import static org.odk.collect.android.test.TestUtils.createTempFile;
import static org.odk.collect.android.test.TestUtils.resetInstancesContentProvider;

public class InstanceServerUploaderTaskTest extends MockedServerTest {

    @Rule
    public GrantPermissionRule runtimepermissionrule = GrantPermissionRule.grant(android.Manifest.permission.READ_PHONE_STATE);

    private InstancesDao dao;

    @Before
    public void setUp() throws Exception {
        resetInstancesContentProvider();
        dao = new InstancesDao();
    }

    @After
    public void tearDown() {
        cleanUpTempFiles();
        resetInstancesContentProvider();
    }

    @Test
    public void shouldUploadAnInstance() throws Exception {
        // given
        Long id = createStoredInstance();
        willRespondWith(headResponse(), postResponse());

        // when
        InstanceUploaderTask.Outcome o = new InstanceServerUploaderTask().doInBackground(id);

        // then
        assertNull(o.authRequestingServer);
        assertEquals(1, o.messagesByInstanceId.size());
        assertEquals("success", o.messagesByInstanceId.get(id.toString()));

        // and
        HEAD: {
            RecordedRequest r = nextRequest();
            assertEquals("HEAD", r.getMethod());
            assertMatches("/submission\\?deviceID=\\w+%3A\\w+", r.getPath());
            assertMatches("Dalvik/.* org.odk.collect.android/.*", r.getHeader("User-Agent"));
            assertEquals("1.0", r.getHeader("X-OpenRosa-Version"));
            assertTrue(r.getHeader("Accept-Encoding").contains("gzip"));
        }

        // and
        POST: {
            RecordedRequest r = nextRequest();
            assertEquals("POST", r.getMethod());
            assertMatches("/submission\\?deviceID=\\w+%3A\\w+", r.getPath());
            assertMatches("Dalvik/.* org.odk.collect.android/.*", r.getHeader("User-Agent"));
            assertEquals("1.0", r.getHeader("X-OpenRosa-Version"));
            assertTrue(r.getHeader("Accept-Encoding").contains("gzip"));
            assertMatches("multipart/form-data; boundary=.*", r.getHeader("Content-Type"));
            assertTrue(r.getBody().readUtf8().contains("<form-content-here/>"));
        }
    }

    private long createStoredInstance() throws Exception {
        File xml = createTempFile("<form-content-here/>");

        Instance i = new Instance.Builder()
                .displayName("Test Form")
                .instanceFilePath(xml.getAbsolutePath())
                .jrFormId("test_form")
                .status(InstanceProviderAPI.STATUS_COMPLETE)
                .lastStatusChangeDate(123L)
                .build();

        Uri contentUri = dao.saveInstance(dao.getValuesFromInstanceObject(i));
        return Long.parseLong(contentUri.toString().substring(InstanceProviderAPI.InstanceColumns.CONTENT_URI.toString().length() + 1));
    }

    private String hostAndPort() {
        return String.format("%s:%s", server.getHostName(), server.getPort());
    }

    private String headResponse() {
        return join(
            "HTTP/1.1 204 No Content\r",
            "X-OpenRosa-Version: 1.0\r",
            "X-OpenRosa-Accept-Content-Length: 10485760\r",
            "Location: http://" + hostAndPort() + "/submission\r",
            "X-Cloud-Trace-Context: 2813267fc382586b60b1d7d494c53d6e;o=1\r",
            "Date: Wed, 19 Apr 2017 22:11:03 GMT\r",
            "Content-Type: text/html\r",
            "Server: Google Frontend\r",
            "Alt-Svc: quic=\":443\"; ma=2592000; v=\"37,36,35\"\r",
            "Connection: close\r",
            "\r");
    }

    private String postResponse() {
        return join(
            "HTTP/1.1 201 Created\r",
            "Location: http://" + hostAndPort() + "/submission\r",
            "X-OpenRosa-Version: 1.0\r",
            "X-OpenRosa-Accept-Content-Length: 10485760\r",
            "Content-Type: text/xml; charset=UTF-8\r",
            "X-Cloud-Trace-Context: d7e2d1c98f475e7fd912545f6cfac4e2\r",
            "Date: Wed, 19 Apr 2017 22:11:05 GMT\r",
            "Server: Google Frontend\r",
            "Content-Length: 373\r",
            "Alt-Svc: quic=\":443\"; ma=2592000; v=\"37,36,35\"\r",
            "Connection: close\r",
            "\r",
            "<OpenRosaResponse xmlns=\"http://openrosa.org/http/response\"><message>success</message><submissionMetadata xmlns=\"http://www.opendatakit.org/xforms\" id=\"basic\" instanceID=\"uuid:5167d1cf-8dcb-4fa6-b7f7-ee5dd0265ab5\" submissionDate=\"2017-04-19T22:11:04.181Z\" isComplete=\"true\" markedAsCompleteDate=\"2017-04-19T22:11:04.181Z\"/></OpenRosaResponse>\r",
            "\r");
    }
}
