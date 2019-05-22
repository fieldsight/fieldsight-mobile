package org.bcss.collect.naxa.common.rx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.HttpUrl;
import timber.log.Timber;

import static org.bcss.collect.naxa.network.APIEndpoint.PARAMS.FORMS_EDU_TYPE;

public class ThrowableWithUrl extends Throwable {

    final String regex = "(?!\\/)\\d+(?=\\/?\\?)";
    private HttpUrl failedUrl;

    void setFailedUrl(HttpUrl failedUrl) {
        this.failedUrl = failedUrl;
    }

    public HttpUrl getFailedUrl() {
        return failedUrl;
    }

    public String getProjectId() {
        if (failedUrl == null) return "-1";

        if (failedUrl.queryParameterNames().contains("project_id")) {
            return failedUrl.queryParameter("project_id");
        }

        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(failedUrl.toString());
        boolean foundProjectId = matcher.matches();
        Timber.i("foundProjectId %s", foundProjectId);
        if (foundProjectId) {
            Timber.i("Matcher group 0 %s", matcher.group(0));
            return matcher.group(0);
        }

        return "-1";

    }

    public String findType() {
        if (failedUrl.queryParameterNames().contains(FORMS_EDU_TYPE)) {
            return failedUrl.queryParameter(FORMS_EDU_TYPE);
        }

        return "sites";
    }
}
