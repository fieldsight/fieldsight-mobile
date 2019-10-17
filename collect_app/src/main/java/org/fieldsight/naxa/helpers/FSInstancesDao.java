package org.fieldsight.naxa.helpers;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.loader.content.CursorLoader;

import org.fieldsight.naxa.common.FieldSightUserSession;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.site.db.SiteUploadHistoryLocalSource;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.dto.Instance;
import org.odk.collect.android.provider.InstanceProviderAPI;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.FormDeploymentFrom.PROJECT;
import static org.fieldsight.naxa.common.Constant.FormDeploymentFrom.SITE;

public class FSInstancesDao extends org.odk.collect.android.dao.InstancesDao {

    public CursorLoader getUnsentInstancesCursorLoaderBySite(String siteId, String sortOrder) {
        CursorLoader cursorLoader;
        if (siteId.length() == 0) {
            cursorLoader = getUnsentInstancesCursorLoader(sortOrder);
        } else {
            String selection =
                    InstanceProviderAPI.InstanceColumns.STATUS + " !=? and "
                            + InstanceProviderAPI.InstanceColumns.FS_SITE_ID + " = ?";
            String[] selectionArgs = {
                    InstanceProviderAPI.STATUS_SUBMITTED,
                    siteId};

            cursorLoader = getInstancesCursorLoader(null, selection, selectionArgs, sortOrder);
        }

        return cursorLoader;
    }

    public CursorLoader getFinalizedInstancesCursorLoaderBySite(String siteId, String sortOrder) {
        CursorLoader cursorLoader;
        if (siteId.length() == 0) {
            cursorLoader = getFinalizedInstancesCursorLoader(sortOrder);
        } else {
            String selection =
                    "(" + InstanceProviderAPI.InstanceColumns.STATUS + "=? or "
                            + InstanceProviderAPI.InstanceColumns.STATUS + "=?) and "
                            + InstanceProviderAPI.InstanceColumns.FS_SITE_ID + " = ?";
            String[] selectionArgs = {
                    InstanceProviderAPI.STATUS_COMPLETE,
                    InstanceProviderAPI.STATUS_SUBMISSION_FAILED,
                    siteId};

            cursorLoader = getInstancesCursorLoader(null, selection, selectionArgs, sortOrder);
        }

        return cursorLoader;
    }

    public CursorLoader getSavedInstancesCursorLoaderSite(String siteId, String sortOrder) {
        CursorLoader cursorLoader;
        if (siteId.length() == 0) {
            cursorLoader = getSavedInstancesCursorLoader(sortOrder);
        } else {
            String selection =
                    InstanceProviderAPI.InstanceColumns.DELETED_DATE + " IS NULL and "
                            + InstanceProviderAPI.InstanceColumns.FS_SITE_ID + " = ?";
            String[] selectionArgs = {siteId};
            cursorLoader = getInstancesCursorLoader(null, selection, selectionArgs, sortOrder);
        }

        return cursorLoader;
    }

    public CursorLoader getCompletedUndeletedInstancesCursorLoaderHideOfflineSite(CharSequence charSequence, String sortOrder) {
        CursorLoader cursorLoader;

        String selection = InstanceProviderAPI.InstanceColumns.DELETED_DATE + " IS NULL and ("
                + InstanceProviderAPI.InstanceColumns.STATUS + "=? or "
                + InstanceProviderAPI.InstanceColumns.STATUS + "=? or "
                + InstanceProviderAPI.InstanceColumns.STATUS + "=?) and "
                + "("
                + "length(" + InstanceProviderAPI.InstanceColumns.FS_SITE_ID + ")" + " < 12 "
                + "OR " + InstanceProviderAPI.InstanceColumns.FS_SITE_ID + " NOT LIKE '%fake%'"
                + ")";

        String[] selectionArgs = {
                InstanceProviderAPI.STATUS_COMPLETE,
                InstanceProviderAPI.STATUS_SUBMISSION_FAILED,
                InstanceProviderAPI.STATUS_SUBMITTED
        };

        cursorLoader = getInstancesCursorLoader(null, selection, selectionArgs, sortOrder);

        return cursorLoader;
    }

    public CursorLoader getFinalizedInstancesCursorLoaderHideOfflineSite(CharSequence charSequence, String sortOrder) {
        CursorLoader cursorLoader;


        String selection =
                "(" + InstanceProviderAPI.InstanceColumns.STATUS + "=? or "
                        + InstanceProviderAPI.InstanceColumns.STATUS + "=?) and "
                        + "("
                        + "length(" + InstanceProviderAPI.InstanceColumns.FS_SITE_ID + ")" + " < 12 "
                        + "OR " + InstanceProviderAPI.InstanceColumns.FS_SITE_ID + " NOT LIKE '%fake%'"
                        + ")";

        String[] selectionArgs = {
                InstanceProviderAPI.STATUS_COMPLETE,
                InstanceProviderAPI.STATUS_SUBMISSION_FAILED};

        cursorLoader = getInstancesCursorLoader(null, selection, selectionArgs, sortOrder);

        return cursorLoader;
    }

    public CursorLoader getCompletedUndeletedInstancesCursorLoaderBySite(String siteId, String
            sortOrder) {
        CursorLoader cursorLoader;
        if (siteId.length() == 0) {
            cursorLoader = getAllCompletedUndeletedInstancesCursorLoader(sortOrder);
        } else {
            String selection = InstanceProviderAPI.InstanceColumns.DELETED_DATE + " IS NULL and ("
                    + InstanceProviderAPI.InstanceColumns.STATUS + "=? or "
                    + InstanceProviderAPI.InstanceColumns.STATUS + "=? or "
                    + InstanceProviderAPI.InstanceColumns.STATUS + "=?) and "
                    + InstanceProviderAPI.InstanceColumns.FS_SITE_ID + " = ?";


            String[] selectionArgs = {
                    InstanceProviderAPI.STATUS_COMPLETE,
                    InstanceProviderAPI.STATUS_SUBMISSION_FAILED,
                    InstanceProviderAPI.STATUS_SUBMITTED,
                    siteId};

            cursorLoader = getInstancesCursorLoader(null, selection, selectionArgs, sortOrder);
        }
        return cursorLoader;
    }

    public int updateSiteId(String newSiteId, String oldSiteId) {


        ContentValues contentValues = new ContentValues();
        contentValues.put(InstanceProviderAPI.InstanceColumns.FS_SITE_ID, newSiteId);
        String where = InstanceProviderAPI.InstanceColumns.FS_SITE_ID + "=?";

        String[] whereArgs = {
                oldSiteId
        };

        return updateInstance(contentValues, where, whereArgs);
    }

    public List<Instance> getInstancesFromCursor(Cursor cursor) {
        List<Instance> instances = new ArrayList<>();
        if (cursor != null) {
            try {
                cursor.moveToPosition(-1);
                while (cursor.moveToNext()) {
                    int displayNameColumnIndex = cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.DISPLAY_NAME);
                    int submissionUriColumnIndex = cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.SUBMISSION_URI);
                    int canEditWhenCompleteIndex = cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.CAN_EDIT_WHEN_COMPLETE);
                    int instanceFilePathIndex = cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH);
                    int jrFormIdColumnIndex = cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.JR_FORM_ID);
                    int jrVersionColumnIndex = cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.JR_VERSION);
                    int statusColumnIndex = cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.STATUS);
                    int lastStatusChangeDateColumnIndex = cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.LAST_STATUS_CHANGE_DATE);
                    int deletedDateColumnIndex = cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.DELETED_DATE);
                    int fsSiteColumnIndex = cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.FS_SITE_ID);
                    int fsInstanceIdColumnIndex = cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns.FS_SUBMISSION_INSTANCE_ID);

                    int databaseIdIndex = cursor.getColumnIndex(InstanceProviderAPI.InstanceColumns._ID);


                    Instance instance = new Instance.Builder()
                            .displayName(cursor.getString(displayNameColumnIndex))
                            .submissionUri(fixUploadUrl(cursor.getString(submissionUriColumnIndex)))
                            .canEditWhenComplete(cursor.getString(canEditWhenCompleteIndex))
                            .instanceFilePath(cursor.getString(instanceFilePathIndex))
                            .jrFormId(cursor.getString(jrFormIdColumnIndex))
                            .jrVersion(cursor.getString(jrVersionColumnIndex))
                            .status(cursor.getString(statusColumnIndex))
                            .lastStatusChangeDate(cursor.getLong(lastStatusChangeDateColumnIndex))
                            .deletedDate(cursor.getLong(deletedDateColumnIndex))
                            .fieldSightInstanceId(cursor.getString(fsInstanceIdColumnIndex))
                            .fieldSightSiteId(cursor.getString(fsSiteColumnIndex))
                            .databaseId(cursor.getLong(databaseIdIndex))
                            .build();

                    instances.add(instance);
                }
            } finally {
                cursor.close();
            }
        }
        return instances;
    }

    private String fixUploadUrl(String url) {
        try {
            if (checkContainsFakeSiteID(url)) {
                String mockedSiteId = getSiteIdFromUrl(url);
                String fsFormId = getFsFormIdFromUrl(url);
                String deployedFrom = getFormDeployedFrom(url);

                url = generateSubmissionUrl(deployedFrom, mockedSiteId.split("-")[0], fsFormId);

                String siteId = SiteUploadHistoryLocalSource.getInstance().getById(mockedSiteId).getNewSiteId();
                url = generateSubmissionUrl(deployedFrom, siteId, fsFormId);
            }
        } catch (NullPointerException e) {
            Timber.e(e);
            Timber.e("Failed to fix url");
        }
        return url;
    }

    public static boolean checkContainsFakeSiteID(String url) {
        String[] split = url.split("/");
        String siteId = split[split.length - 1];
        return siteId.contains("fake");

    }

    private String getSiteIdFromUrl(String url) {
        String[] split = url.split("/");
        return split[split.length - 1];
    }


    public static String generateSubmissionUrl(String formDeployedFrom, String siteId, String fsFormId) {

        String submissionUrl = FieldSightUserSession.getServerUrl(Collect.getInstance()) + APIEndpoint.FORM_SUBMISSION_PAGE;

        switch (formDeployedFrom) {
            case PROJECT:
                submissionUrl += "Timber.e(e);/" + fsFormId + "/" + siteId;
                break;
            case SITE:
                submissionUrl += fsFormId + "/" + siteId;
                break;
            default:
                throw new RuntimeException("Unknown form deployed");
        }

        return submissionUrl;

    }

    public Observable<Integer> cascadedSiteIds(String oldId, String newId) {
        return Observable.just(getBySiteId(oldId))
                .flatMapIterable((Function<List<Instance>, Iterable<Instance>>) instances -> instances)
                .map(new Function<Instance, Integer>() {
                    @Override
                    public Integer apply(Instance instance) {
                        String url = instance.getSubmissionUri();
                        String deployedFrom = getFormDeployedFrom(url);
                        String fsFormId = getFsFormIdFromUrl(url);

                        String oldUrl = generateSubmissionUrl(deployedFrom, oldId, fsFormId);
                        String newUrl = generateSubmissionUrl(deployedFrom, newId, fsFormId);


                        ContentValues contentValues = new ContentValues();
                        contentValues.put(InstanceProviderAPI.InstanceColumns.SUBMISSION_URI, newUrl);
                        contentValues.put(InstanceProviderAPI.InstanceColumns.FS_SITE_ID, newId);

                        String selection = InstanceProviderAPI.InstanceColumns.FS_SITE_ID + "=?" +
                                " AND " +
                                InstanceProviderAPI.InstanceColumns.SUBMISSION_URI + "=?";
                        String[] selectionArgs = new String[]{oldId, oldUrl};

                        return updateInstance(contentValues, selection, selectionArgs);
                    }
                });


    }

    private String getFormDeployedFrom(String url) {
        String[] split = url.split("/");
        if ( PROJECT.equals(split[split.length - 3])) {
            return  PROJECT;
        } else {
            return  SITE;
        }
    }




    public String getFsFormIdFromUrl(String url) {
        String[] split = url.split("/");
        return split[split.length - 2];
    }

    public List<Instance> getBySiteId(String siteId) {

        Cursor cursor;
        String selection = InstanceProviderAPI.InstanceColumns.FS_SITE_ID + "=?";

        String[] selectionArgs = new String[]{siteId};

        cursor = getInstancesCursor(selection, selectionArgs);
        return getInstancesFromCursor(cursor);
    }


}
