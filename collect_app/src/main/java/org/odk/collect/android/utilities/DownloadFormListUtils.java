/*
 * Copyright 2018 Nafundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.odk.collect.android.utilities;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import org.fieldsight.collect.android.R;
import org.javarosa.xform.parse.XFormParser;
import org.kxml2.kdom.Element;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.http.CollectServerClient;
import org.odk.collect.android.logic.FormDetails;
import org.odk.collect.android.logic.ManifestFile;
import org.odk.collect.android.logic.MediaFile;
import org.odk.collect.android.preferences.GeneralKeys;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class DownloadFormListUtils {

    // used to store error message if one occurs
    public static final String DL_ERROR_MSG = "dlerrormessage";
    public static final String DL_AUTH_REQUIRED = "dlauthrequired";

    private static final String NAMESPACE_OPENROSA_ORG_XFORMS_XFORMS_LIST =
            "http://openrosa.org/xforms/xformsList";

    private final WebCredentialsUtils webCredentialsUtils;
    private final CollectServerClient collectServerClient;
    private final Application application;
    private final FormsDao formsDao;

    public DownloadFormListUtils(
            Application application,
            CollectServerClient collectServerClient,
            WebCredentialsUtils webCredentialsUtils,
            FormsDao formsDao) {
        this.application = application;
        this.collectServerClient = collectServerClient;
        this.webCredentialsUtils = webCredentialsUtils;
        this.formsDao = formsDao;
    }

    public HashMap<String, FormDetails> downloadFormList(boolean alwaysCheckMediaFiles) {
        return downloadFormList(null, null, null, alwaysCheckMediaFiles);
    }

    public HashMap<String, FormDetails> downloadFormList(@Nullable String url, @Nullable String username,
                                                         @Nullable String password, boolean alwaysCheckMediaFiles) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(
                application);

        String downloadListUrl = url != null ? url :
                settings.getString(GeneralKeys.KEY_SERVER_URL,
                        application.getString(R.string.default_server_url));

        while (downloadListUrl.endsWith("/")) {
            downloadListUrl = downloadListUrl.substring(0, downloadListUrl.length() - 1);
        }

        // NOTE: /formlist must not be translated! It is the well-known path on the server.
        String formListUrl = application.getString(
                R.string.default_odk_formlist);

        // When a url is supplied, we will use the default formList url
        String downloadPath = (url != null) ?
                formListUrl : settings.getString(GeneralKeys.KEY_FORMLIST_URL, formListUrl);

        downloadListUrl += downloadPath;

        // We populate this with available forms from the specified server.
        // <formname, details>
        HashMap<String, FormDetails> formList = new HashMap<String, FormDetails>();

        if (url != null) {
            String host = Uri.parse(url).getHost();

            if (host != null) {
                if (username != null && password != null) {
                    webCredentialsUtils.saveCredentials(url, username, password);
                } else {
                    webCredentialsUtils.clearCredentials(url);
                }
            }
        }

        DocumentFetchResult result = collectServerClient.getXmlDocument(downloadListUrl);

        clearTemporaryCredentials(url);

        // If we can't get the document, return the error, cancel the task
        if (result.errorMessage != null) {
            if (result.responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                formList.put(DL_AUTH_REQUIRED, new FormDetails(result.errorMessage));
            } else {
                formList.put(DL_ERROR_MSG, new FormDetails(result.errorMessage));
            }
            return formList;
        }

        if (result.isOpenRosaResponse) {
            // Attempt OpenRosa 1.0 parsing
            Element xformsElement = result.doc.getRootElement();
            if (!xformsElement.getName().equals("xforms")) {
                String error = "root element is not <xforms> : " + xformsElement.getName();
                Timber.e("Parsing OpenRosa reply -- %s", error);
                formList.put(
                        DL_ERROR_MSG,
                        new FormDetails(application.getString(
                                R.string.parse_openrosa_formlist_failed, error)));
                return formList;
            }
            String namespace = xformsElement.getNamespace();
            if (!isXformsListNamespacedElement(xformsElement)) {
                String error = "root element namespace is incorrect:" + namespace;
                Timber.e("Parsing OpenRosa reply -- %s", error);
                formList.put(
                        DL_ERROR_MSG,
                        new FormDetails(application.getString(
                                R.string.parse_openrosa_formlist_failed, error)));
                return formList;
            }
            int elements = xformsElement.getChildCount();
            for (int i = 0; i < elements; ++i) {
                if (xformsElement.getType(i) != Element.ELEMENT) {
                    // e.g., whitespace (text)
                    continue;
                }
                Element xformElement = xformsElement.getElement(i);
                if (!isXformsListNamespacedElement(xformElement)) {
                    // someone else's extension?
                    continue;
                }
                String name = xformElement.getName();
                if (!name.equalsIgnoreCase("xform")) {
                    // someone else's extension?
                    continue;
                }

                // this is something we know how to interpret
                String formId = null;
                String formName = null;
                String version = null;
                String majorMinorVersion = null;
                String description = null;
                String downloadUrl = null;
                String manifestUrl = null;
                String hash = null;
                // don't process descriptionUrl
                int fieldCount = xformElement.getChildCount();
                for (int j = 0; j < fieldCount; ++j) {
                    if (xformElement.getType(j) != Element.ELEMENT) {
                        // whitespace
                        continue;
                    }
                    Element child = xformElement.getElement(j);
                    if (!isXformsListNamespacedElement(child)) {
                        // someone else's extension?
                        continue;
                    }
                    String tag = child.getName();
                    switch (tag) {
                        case "formID":
                            formId = XFormParser.getXMLText(child, true);
                            if (formId != null && formId.length() == 0) {
                                formId = null;
                            }
                            break;
                        case "name":
                            formName = XFormParser.getXMLText(child, true);
                            if (formName != null && formName.length() == 0) {
                                formName = null;
                            }
                            break;
                        case "version":
                            version = XFormParser.getXMLText(child, true);
                            if (version != null && version.length() == 0) {
                                version = null;
                            }
                            break;
                        case "majorMinorVersion":
                            majorMinorVersion = XFormParser.getXMLText(child, true);
                            if (majorMinorVersion != null && majorMinorVersion.length() == 0) {
                                majorMinorVersion = null;
                            }
                            break;
                        case "descriptionText":
                            description = XFormParser.getXMLText(child, true);
                            if (description != null && description.length() == 0) {
                                description = null;
                            }
                            break;
                        case "downloadUrl":
                            downloadUrl = XFormParser.getXMLText(child, true);
                            if (downloadUrl != null && downloadUrl.length() == 0) {
                                downloadUrl = null;
                            }
                            break;
                        case "manifestUrl":
                            manifestUrl = XFormParser.getXMLText(child, true);
                            if (manifestUrl != null && manifestUrl.length() == 0) {
                                manifestUrl = null;
                            }
                            break;
                        case "hash":
                            hash = XFormParser.getXMLText(child, true);
                            if (hash != null && hash.length() == 0) {
                                hash = null;
                            }
                            break;
                    }
                }
                if (formId == null || downloadUrl == null || formName == null) {
                    String error =
                            "Forms list entry " + Integer.toString(i)
                                    + " has missing or empty tags: formID, name, or downloadUrl";
                    Timber.e("Parsing OpenRosa reply -- %s", error);
                    formList.clear();
                    formList.put(
                            DL_ERROR_MSG,
                            new FormDetails(application.getString(
                                    R.string.parse_openrosa_formlist_failed, error)));
                    return formList;
                }
                boolean isNewerFormVersionAvailable = false;
                boolean areNewerMediaFilesAvailable = false;
                ManifestFile manifestFile = null;
                if (isThisFormAlreadyDownloaded(formId)) {
                    isNewerFormVersionAvailable = isNewerFormVersionAvailable(FormDownloader.getMd5Hash(hash));
                    if ((!isNewerFormVersionAvailable || alwaysCheckMediaFiles) && manifestUrl != null) {
                        manifestFile = getManifestFile(manifestUrl);
                        if (manifestFile != null) {
                            List<MediaFile> newMediaFiles = manifestFile.getMediaFiles();
                            if (newMediaFiles != null && !newMediaFiles.isEmpty()) {
                                areNewerMediaFilesAvailable = areNewerMediaFilesAvailable(formId, version, newMediaFiles);
                            }
                        }
                    }
                }
                formList.put(formId, new FormDetails(formName, downloadUrl, manifestUrl, formId,
                        (version != null) ? version : majorMinorVersion, hash,
                        manifestFile != null ? manifestFile.getHash() : null,
                        isNewerFormVersionAvailable, areNewerMediaFilesAvailable));
            }
        } else {
            // Aggregate 0.9.x mode...
            // populate HashMap with form names and urls
            Element formsElement = result.doc.getRootElement();
            int formsCount = formsElement.getChildCount();
            String formId = null;
            for (int i = 0; i < formsCount; ++i) {
                if (formsElement.getType(i) != Element.ELEMENT) {
                    // whitespace
                    continue;
                }
                Element child = formsElement.getElement(i);
                String tag = child.getName();
                if (tag.equals("formID")) {
                    formId = XFormParser.getXMLText(child, true);
                    if (formId != null && formId.length() == 0) {
                        formId = null;
                    }
                }
                if (tag.equalsIgnoreCase("form")) {
                    String formName = XFormParser.getXMLText(child, true);
                    if (formName != null && formName.length() == 0) {
                        formName = null;
                    }
                    String downloadUrl = child.getAttributeValue(null, "url");
                    downloadUrl = downloadUrl.trim();
                    if (downloadUrl.length() == 0) {
                        downloadUrl = null;
                    }
                    if (formName == null) {
                        String error =
                                "Forms list entry " + Integer.toString(i)
                                        + " is missing form name or url attribute";
                        Timber.e("Parsing OpenRosa reply -- %s", error);
                        formList.clear();
                        formList.put(
                                DL_ERROR_MSG,
                                new FormDetails(application.getString(
                                        R.string.parse_legacy_formlist_failed, error)));
                        return formList;
                    }
                    formList.put(formName,
                            new FormDetails(formName, downloadUrl, null, formId, null, null, null, false, false));

                    formId = null;
                }
            }
        }
        return formList;
    }

    private void clearTemporaryCredentials(@Nullable String url) {
        if (url != null) {
            String host = Uri.parse(url).getHost();

            if (host != null) {
                webCredentialsUtils.clearCredentials(url);
            }
        }
    }

    private boolean isThisFormAlreadyDownloaded(String formId) {
        Cursor cursor = formsDao.getFormsCursorForFormId(formId);
        return cursor == null || cursor.getCount() > 0;
    }

    private ManifestFile getManifestFile(String manifestUrl) {
        if (manifestUrl == null) {
            return null;
        }

        DocumentFetchResult result = collectServerClient.getXmlDocument(manifestUrl);

        if (result.errorMessage != null) {
            return null;
        }

        String errMessage = application.getString(R.string.access_error, manifestUrl);

        if (!result.isOpenRosaResponse) {
            errMessage += application.getString(R.string.manifest_server_error);
            Timber.e(errMessage);
            return null;
        }

        // Attempt OpenRosa 1.0 parsing
        Element manifestElement = result.doc.getRootElement();
        if (!manifestElement.getName().equals("manifest")) {
            errMessage +=
                    application.getString(R.string.root_element_error,
                            manifestElement.getName());
            Timber.e(errMessage);
            return null;
        }
        String namespace = manifestElement.getNamespace();
        if (!FormDownloader.isXformsManifestNamespacedElement(manifestElement)) {
            errMessage += application.getString(R.string.root_namespace_error, namespace);
            Timber.e(errMessage);
            return null;
        }
        int elements = manifestElement.getChildCount();
        List<MediaFile> files = new ArrayList<>();
        for (int i = 0; i < elements; ++i) {
            if (manifestElement.getType(i) != Element.ELEMENT) {
                // e.g., whitespace (text)
                continue;
            }
            Element mediaFileElement = manifestElement.getElement(i);
            if (!FormDownloader.isXformsManifestNamespacedElement(mediaFileElement)) {
                // someone else's extension?
                continue;
            }
            String name = mediaFileElement.getName();
            if (name.equalsIgnoreCase("mediaFile")) {
                String filename = null;
                String hash = null;
                String downloadUrl = null;
                // don't process descriptionUrl
                int childCount = mediaFileElement.getChildCount();
                for (int j = 0; j < childCount; ++j) {
                    if (mediaFileElement.getType(j) != Element.ELEMENT) {
                        // e.g., whitespace (text)
                        continue;
                    }
                    Element child = mediaFileElement.getElement(j);
                    if (!FormDownloader.isXformsManifestNamespacedElement(child)) {
                        // someone else's extension?
                        continue;
                    }
                    String tag = child.getName();
                    switch (tag) {
                        case "filename":
                            filename = XFormParser.getXMLText(child, true);
                            if (filename != null && filename.length() == 0) {
                                filename = null;
                            }
                            break;
                        case "hash":
                            hash = XFormParser.getXMLText(child, true);
                            if (hash != null && hash.length() == 0) {
                                hash = null;
                            }
                            break;
                        case "downloadUrl":
                            downloadUrl = XFormParser.getXMLText(child, true);
                            if (downloadUrl != null && downloadUrl.length() == 0) {
                                downloadUrl = null;
                            }
                            break;
                    }
                }
                if (filename == null || downloadUrl == null || hash == null) {
                    errMessage +=
                            application.getString(R.string.manifest_tag_error,
                                    Integer.toString(i));
                    Timber.e(errMessage);
                    return null;
                }
                files.add(new MediaFile(filename, hash, downloadUrl));
            }
        }

        return new ManifestFile(result.getHash(), files);
    }

    private boolean isNewerFormVersionAvailable(String md5Hash) {
        if (md5Hash == null) {
            return false;
        }
        try (Cursor cursor = formsDao.getFormsCursorForMd5Hash(md5Hash)) {
            return cursor != null && cursor.getCount() == 0;
        }
    }

    private boolean areNewerMediaFilesAvailable(String formId, String formVersion, List<MediaFile> newMediaFiles) {
        String mediaDirPath = formsDao.getFormMediaPath(formId, formVersion);
        if (mediaDirPath != null) {
            File[] localMediaFiles = new File(mediaDirPath).listFiles();
            if (localMediaFiles != null) {
                for (MediaFile newMediaFile : newMediaFiles) {
                    if (!isMediaFileAlreadyDownloaded(localMediaFiles, newMediaFile)) {
                        return true;
                    }
                }
            } else if (!newMediaFiles.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private static boolean isMediaFileAlreadyDownloaded(File[] localMediaFiles, MediaFile newMediaFile) {
        // TODO Zip files are ignored we should find a way to take them into account too
        if (newMediaFile.getFilename().endsWith(".zip")) {
            return true;
        }

        String mediaFileHash = newMediaFile.getHash();
        mediaFileHash = mediaFileHash.substring(4, mediaFileHash.length());
        for (File localMediaFile : localMediaFiles) {
            if (mediaFileHash.equals(FileUtils.getMd5Hash(localMediaFile))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isXformsListNamespacedElement(Element e) {
        return e.getNamespace().equalsIgnoreCase(NAMESPACE_OPENROSA_ORG_XFORMS_XFORMS_LIST);
    }
}
