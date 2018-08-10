package org.bcss.collect.naxa;

import com.google.common.io.Files;

import org.bcss.collect.android.application.Collect;
import org.opendatakit.httpclientandroidlib.HttpResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class ResponseUtils {
    public static void saveHttpResponseToFile(HttpResponse response, String filename) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuilder total = new StringBuilder();

        String line = null;

        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        r.close();


        File file = new File(Collect.ODK_ROOT, filename + ".html");
        Files.write(total, file, Charset.defaultCharset());
    }
}
