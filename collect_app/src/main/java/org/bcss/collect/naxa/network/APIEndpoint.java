package org.bcss.collect.naxa.network;

public class APIEndpoint {

    public final static int NEW_RECORD_CREATED = 201;
//  public static final String BASE_URL = "http://app.fieldsight.org";
   public static final String BASE_URL = "http://fieldsight.naxa.com.np";

    public static final String PASSWORD_RESET = BASE_URL + "/accounts/password/reset/";
    public static final String USER_LOGIN = BASE_URL + "/users/api/get-auth-token/";

    public static final String FORM_SUBMISSION_URL = BASE_URL;
    public static final String FORM_SUBMISSION_PAGE = "/forms/submission/";
    public static final String GET_SITE_TYPES = BASE_URL + "/fieldsight/api/site-types/";
    public static final String GET_PROJECT_STAGES = BASE_URL + "/forms/api/stage/1/{project_id}";
    public static final String GET_PROJECT_STAGES_NEW = BASE_URL + "/forms/api/stage/{is_project}/{id}";

    public static final String GET_SITE_STAGES = BASE_URL + "/forms/api/stage/0/{site_id}";
    public static final String GET_STAGE_SUB_STAGE = BASE_URL + "/forms/api/stage/{is_project}/{id}";
    public static final String GET_GENERAL_EM = BASE_URL + "/forms/api/general/0/{site_id}";
    public static final String GET_SCHEDULE_EM = BASE_URL + "/forms/api/schedules/0/{site_id}";

    public static final String GET_LOCATION_URL = BASE_URL + "/fieldsight/api/project-sites/";
    public static final String GET_FORM_SCHEDULE = BASE_URL + "/forms/api/schedules/{is_project}/{id}";
    public static final String GET_GENERAL_FORM = BASE_URL + "/forms/api/general/{is_project}/{id}";

    public static final String GET_FS_FORM_DETAIL = BASE_URL + "/forms/api/form-detail/{fs_form_id}";
    public static final String ASSIGNED_FORM_LIST_PROJECT = BASE_URL + "/forms/assignedFormList/project/";
    public static final String ASSIGNED_FORM_LIST_SITE = BASE_URL + "/forms/assignedFormList/siteLevel/";

    public static final String ADD_FCM = BASE_URL + "/fieldsight/fcm/add/";
    public static final String REMOVE_FCM = BASE_URL + "/fieldsight/fcm/logout/";
    public static final String ADD_SITE_URL = BASE_URL + "/fieldsight/api/survey-sites/";
    public static final String SITE_UPDATE_URL = BASE_URL + "/fieldsight/api/update-site/";
    public static final String PROJECT_UPDATE_URL = BASE_URL + "/fieldsight/api/async_save_project/";

    public static final String GET_CLUSTER_LIST = BASE_URL + "/fieldsight/project/region-list/{project_id}/";
    public static final String GET_ALL_SUBMISSION = BASE_URL + "/forms/last-submissions/";


}