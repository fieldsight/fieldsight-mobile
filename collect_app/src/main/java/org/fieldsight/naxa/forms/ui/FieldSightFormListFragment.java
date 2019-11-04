package org.fieldsight.naxa.forms.ui;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import org.fieldsight.naxa.common.BaseFormListFragment;
import org.fieldsight.naxa.forms.data.local.FieldsightFormDetailsv3;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.site.SiteType;
import org.fieldsight.naxa.site.SiteTypeLocalSource;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.EXTRA_ID;
import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;
import static org.fieldsight.naxa.common.Constant.EXTRA_PROJECT;

public class FieldSightFormListFragment extends BaseFormListFragment {

    private Site loadedSite;
    private String formType;
    private Project project;


    public static FieldSightFormListFragment newInstance(String loadFormType, Site site, Project project) {
        FieldSightFormListFragment fragment = new FieldSightFormListFragment();
        Bundle bundle = new Bundle();

        //hacking way to load survey FORMS - Nishon
        if (site == null) {
            site = new Site();
            site.setSite("0");
            site.setName(project.getName());
            site.setProject(project.getId());
        }

        bundle.putParcelable(EXTRA_OBJECT, site);
        bundle.putString(EXTRA_ID, loadFormType);
        bundle.putParcelable(EXTRA_PROJECT, project);
        fragment.setArguments(bundle);
        return fragment;
    }

    private FieldSightFormListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        loadedSite = getArguments().getParcelable(EXTRA_OBJECT);
        formType = getArguments().getString(EXTRA_ID);
        project = getArguments().getParcelable(EXTRA_PROJECT);
        setToolbarText(loadedSite.getName(), loadedSite.getName());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getViewModel().loadForm(formType, loadedSite.getProject(), loadedSite.getId(), loadedSite.getTypeId(), loadedSite.getRegionId(), project)
                .observe(this, fieldSightForms -> {
                    // filter form by setting
                    // for the form that belongs to all the site not having regions and types, it will have 0 value

                    int newSiteTypeId = TextUtils.isEmpty(loadedSite.getTypeId()) ? 0 : Integer.parseInt(loadedSite.getTypeId());
                    int newSiteRegionId = TextUtils.isEmpty(loadedSite.getRegionId()) ? 0 : Integer.parseInt(loadedSite.getRegionId());

                    List<FieldsightFormDetailsv3> filteredList = new ArrayList<>();

                    Timber.i("FieldsightFormListFragment, project id = %s", project.getId());
                    boolean isProjectRegionsEmpty = project.getRegionList() == null || project.getRegionList().size() == 1;
                    List<SiteType> siteTypeList = SiteTypeLocalSource.getInstance().getByid(project.getId());
                    boolean isProjectTypesEmpty = siteTypeList == null || siteTypeList.size() == 0;



                    Timber.i("loadForm:: isProjectRegionempty = " + isProjectRegionsEmpty + " isProjectTypeEmpty = " + isProjectTypesEmpty);
                    if (isProjectRegionsEmpty && isProjectTypesEmpty) {
                        filteredList.addAll(fieldSightForms);
                    } else {
                        for (FieldsightFormDetailsv3 fieldsightFormDetailsv3 : fieldSightForms) {
                            Timber.i("loadForm :: formsettings = %s", fieldsightFormDetailsv3.getSettings());
                            if (TextUtils.isEmpty(fieldsightFormDetailsv3.getSettings()) || TextUtils.equals(fieldsightFormDetailsv3.getSettings(), "null")) {
                                filteredList.add(fieldsightFormDetailsv3);
                                continue;
                            }
                            try {
                                JSONObject settingJSON = new JSONObject(fieldsightFormDetailsv3.getSettings());
                                boolean typeFound = isProjectTypesEmpty;
                                if(!typeFound) {
                                    JSONArray typesArray = settingJSON.optJSONArray("types");
                                    for (int i = 0; i < typesArray.length(); i++) {
                                        if (typesArray.optInt(i) == newSiteTypeId) {
                                            typeFound = true;
                                            break;
                                        }
                                    }
                                }
                                boolean regionFound = isProjectRegionsEmpty;
                                if(!regionFound) {
                                    JSONArray regionsArray = settingJSON.optJSONArray("regions");
                                    for (int i = 0; i < regionsArray.length(); i++) {
                                        if (regionsArray.optInt(i) == newSiteRegionId) {
                                            regionFound = true;
                                            break;
                                        }
                                    }
                                }
                                if (typeFound && regionFound) {
                                    filteredList.add(fieldsightFormDetailsv3);
                                }

                            } catch (Exception e) {
                                Timber.e(e);
                            }
                        }
                    }
                    Timber.i(filteredList.toString());
                    showEmptyLayout(filteredList.isEmpty());
                    updateList(filteredList, loadedSite.getId());
                });
    }


}
