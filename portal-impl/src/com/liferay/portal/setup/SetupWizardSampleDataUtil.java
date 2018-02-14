/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.setup;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.lar.PortletDataHandlerKeys;
import com.liferay.portal.kernel.lar.UserIdStrategy;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.*;
import com.liferay.portal.model.*;
import com.liferay.portal.service.*;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.expando.DuplicateColumnNameException;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoColumnConstants;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.model.ExpandoTableConstants;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil;
import org.apache.commons.lang.time.StopWatch;
import org.apache.xpath.operations.Bool;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Shinn Lok
 */
public class SetupWizardSampleDataUtil {

    public static void addSampleData(long companyId, User user) throws Exception {
        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        if (_log.isInfoEnabled()) {
            _log.info("Adding sample data");
        }

        Company company = CompanyLocalServiceUtil.getCompanyById(companyId);

        Account account = company.getAccount();

        account.setName(PropsValues.COMPANY_DEFAULT_NAME);
        account.setLegalName(PropsValues.COMPANY_DEFAULT_NAME + ", Inc");

        AccountLocalServiceUtil.updateAccount(account);

        User defaultUser = company.getDefaultUser();

        addOrgExpandoColumnDemo(companyId);

        Organization organization =
                OrganizationLocalServiceUtil.addOrganization(
                        defaultUser.getUserId(),
                        OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
                        PropsValues.COMPANY_DEFAULT_NAME + " Demo", true);
        createDemoExpandoOrg(organization);

        GroupLocalServiceUtil.updateFriendlyURL(
                organization.getGroupId(), "/"
                        + StringUtil.replace(PropsValues.COMPANY_DEFAULT_NAME.toLowerCase(), StringPool.BLANK, StringPool.MINUS)
                        + "-demo");

        if (user == null) {
            user = UserLocalServiceUtil.addDefaultAdminUser(
                    companyId, "admin", "admin@liferay.com",
                    LocaleUtil.getDefault(), "Вася", StringPool.BLANK, "Пупкин");
        }

        UserLocalServiceUtil.addGroupUser(organization.getGroupId(), user.getUserId());

        user.setPasswordReset(false);

        UserLocalServiceUtil.updateUser(user);

        OrganizationLocalServiceUtil.addUserOrganization(
                user.getUserId(), organization);

        addOrganizations(user, organization);
        Role demoRole = createRole(user.getUserId());
        addUsers(companyId, organization, demoRole);
        importSampleData(companyId, false);

        if (_log.isInfoEnabled()) {
            _log.info("Finished adding data in " + stopWatch.getTime() + " ms");
        }
    }

    public static Map<String, String[]> initParamMap(boolean addSampleData) {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        if (addSampleData) {
            parameterMap.put(
                    PortletDataHandlerKeys.CATEGORIES,
                    new String[] {Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.PERMISSIONS,
                    new String[] {Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.PORTLET_CONFIGURATION,
                    new String[] {Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.PORTLET_DATA,
                    new String[] {Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT,
                    new String[] {Boolean.TRUE.toString()});

        } else {
            parameterMap.put(
                    PortletDataHandlerKeys.LOGO,
                    new String[]{Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.CATEGORIES,
                    new String[]{Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.COMMENTS,
                    new String[]{Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.RATINGS,
                    new String[]{Boolean.FALSE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.PERMISSIONS,
                    new String[]{Boolean.TRUE.toString()});

            parameterMap.put(
                    PortletDataHandlerKeys.PORTLET_CONFIGURATION,
                    new String[]{Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.PORTLET_CONFIGURATION_ALL,
                    new String[]{Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.PORTLET_DATA,
                    new String[]{Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.PORTLET_DATA_ALL,
                    new String[]{Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.PORTLET_DATA_CONTROL_DEFAULT,
                    new String[]{Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.PORTLET_SETUP,
                    new String[]{Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.PORTLET_SETUP_ALL,
                    new String[]{Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS,
                    new String[]{Boolean.FALSE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.PORTLET_ARCHIVED_SETUPS_ALL,
                    new String[]{Boolean.FALSE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.PORTLET_USER_PREFERENCES,
                    new String[]{Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.PORTLET_USER_PREFERENCES_ALL,
                    new String[]{Boolean.TRUE.toString()});

            parameterMap.put(
                    PortletDataHandlerKeys.PUBLIC_LAYOUT_PERMISSIONS,
                    new String[]{Boolean.FALSE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.LAYOUT_SET_SETTINGS,
                    new String[]{Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE,
                    new String[]{PortletDataHandlerKeys.LAYOUTS_IMPORT_MODE_MERGE_BY_LAYOUT_NAME});

            parameterMap.put(
                    PortletDataHandlerKeys.DATA_STRATEGY,
                    new String[]{PortletDataHandlerKeys.DATA_STRATEGY_MIRROR_OVERWRITE});
            parameterMap.put(
                    PortletDataHandlerKeys.THEME_REFERENCE,
                    new String[]{Boolean.TRUE.toString()});
            parameterMap.put(
                    PortletDataHandlerKeys.USER_ID_STRATEGY,
                    new String[]{UserIdStrategy.CURRENT_USER_ID});
        }

        return parameterMap;
    }

    public static void importSampleData(long companyId, boolean defaultData) throws SystemException, PortalException {


        StopWatch stopWatch = new StopWatch();

        stopWatch.start();

        if (_log.isInfoEnabled()) {
            _log.info("Start import lar files for default: " + defaultData);
        }
        Company company = CompanyLocalServiceUtil.getCompanyById(companyId);

        User user = UserLocalServiceUtil.fetchUserByEmailAddress(
                company.getCompanyId(), "admin@liferay.com");

        Map<String, String[]> paramMap = initParamMap(Boolean.FALSE);

        List<Group> companyGroups = GroupLocalServiceUtil.getCompanyGroups(companyId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
        for (Group group : companyGroups) {
            if (group.isSite() && !group.isCompany()) {
                String filePrefix = StringPool.BLANK;
                if (!group.getFriendlyURL().equals("/guest")) {
                    filePrefix = StringUtil.replace(group.getFriendlyURL(), "/", StringPool.BLANK) + StringPool.UNDERLINE;
                }
                String privateFileName = "private.lar";
                if (defaultData) {
                    privateFileName = "default_" + privateFileName;
                }
                String privateFile = filePrefix + privateFileName;
                if (FileUtil.exists(PropsValues.LIFERAY_HOME + StringPool.SLASH + "sample-data" + StringPool.SLASH + privateFile)) {
                    _log.info("file " + privateFile + " exist");
                    try {
                        File file = new File(PropsValues.LIFERAY_HOME + StringPool.SLASH + "sample-data" + StringPool.SLASH + privateFile);
                        if (Validator.isNotNull(file)) {
                            LayoutLocalServiceUtil.importLayouts(user.getUserId(), group.getGroupId(), true, paramMap, file);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    _log.warn("file " + privateFile + " not exist");
                }
                String publicFileName = "public.lar";
                if (defaultData) {
                    publicFileName = "default_" + publicFileName;
                }
                String publicFile = filePrefix + publicFileName;
                if (!publicFile.equals("public.lar")) {
                    if (FileUtil.exists(PropsValues.LIFERAY_HOME + StringPool.SLASH + "sample-data" + StringPool.SLASH + publicFile)) {
                        _log.info("file " + publicFile + " exist");
                        try {
                            File file = new File(PropsValues.LIFERAY_HOME + StringPool.SLASH + "sample-data" + StringPool.SLASH + publicFile);
                            if (Validator.isNotNull(file)) {
                                LayoutLocalServiceUtil.importLayouts(user.getUserId(), group.getGroupId(), false, paramMap, file);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        _log.warn("file " + publicFile + " not exist");
                    }
                }
            }
        }
        if (_log.isInfoEnabled()) {
            _log.info("Finished import lar in " + stopWatch.getTime() + " ms");
        }
    }

    protected static void addUsers(long companyId, Organization parentOrganization, Role demoRole) {
        if (FileUtil.exists(PropsValues.LIFERAY_HOME + StringPool.SLASH + "sample-data" + StringPool.SLASH + "users.json")) {
            Group guestGroup = null;
            try {
                guestGroup = GroupLocalServiceUtil.fetchGroup(companyId, GroupConstants.GUEST);
            } catch (SystemException e) {
                e.printStackTrace();
            }
            File usersJsonFile = new File(PropsValues.LIFERAY_HOME + StringPool.SLASH + "sample-data" + StringPool.SLASH + "users.json");
            try {
                String usersStr = FileUtil.read(usersJsonFile);
                JSONArray usersArray = JSONFactoryUtil.createJSONArray(usersStr);
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject curUser = usersArray.getJSONObject(i);
                    long[] groupIds;
                    long[] organizationIds;

                    List<Group> groups = new ArrayList<Group>();
                    List<Organization> organizations = new ArrayList<Organization>();
                    organizations.add(parentOrganization);
                    if (curUser.has("orgs")) {
                        JSONArray orgs = curUser.getJSONArray("orgs");
                        for (int j = 0; j < orgs.length(); j++) {
                            Group group = null;
                            try {
                                group = GroupLocalServiceUtil.getFriendlyURLGroup(companyId, StringPool.SLASH + orgs.getString(j));
                            } catch (Exception e) {}
                            if (Validator.isNotNull(group)) {
                                groups.add(group);
                            }
                        }
                    }
                    groupIds = new long[groups.size()];
                    for (int g = 0; g < groups.size(); g++) {
                        groupIds[g] = groups.get(g).getGroupId();
                        Organization organization = null;
                        try {
                            organization = OrganizationLocalServiceUtil.getOrganization(groups.get(g).getOrganizationId());
                        } catch (Exception e) {}
                        if (Validator.isNotNull(organization)) {
                            organizations.add(organization);
                        }
                    }
                    organizationIds = new long[organizations.size()];
                    for (int o = 0; o < organizations.size(); o++) {
                        organizationIds[o] = organizations.get(o).getOrganizationId();
                    }

                    User user = UserLocalServiceUtil.addUser(
                            0, companyId, false, "test", "test", false,
                            curUser.getString("screenName"), curUser.getString("emailAddress"), 0, null, LocaleUtil.getDefault(),
                            curUser.getString("firstName"), curUser.getString("middleName"), curUser.getString("lastName"), 0, 0, true, Calendar.JANUARY, 1,
                            1970, curUser.getString("jobTitle"), groupIds, organizationIds, null, null, false,
                            new ServiceContext());
                    updateUserLogo(user, false);
                    if (Validator.isNotNull(guestGroup) && Validator.isNotNull(demoRole)) {
                        UserGroupRoleLocalServiceUtil.addUserGroupRoles(user.getUserId(), guestGroup.getGroupId(), new long[]{demoRole.getRoleId()});
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            _log.warn("file users.json not exist");
        }
    }

    public static void updateUserLogo(User user, boolean isAdmin) {
        File portrait = null;
        String fileName = PropsValues.LIFERAY_HOME + StringPool.SLASH + "sample-data" + StringPool.SLASH;
        if (isAdmin) {
            fileName = fileName + "admin";
        } else {
            fileName = fileName + user.getScreenName();
        }
        if (FileUtil.exists(fileName + ".jpg")) {
            portrait = new File(fileName + ".jpg");
        } else if (FileUtil.exists(fileName + ".jpeg")) {
            portrait = new File(fileName + ".jpeg");
        } else if (FileUtil.exists(fileName + ".png")) {
            portrait = new File(fileName + ".png");
        }
        if (Validator.isNotNull(portrait)) {
            try {
                UserLocalServiceUtil.updatePortrait(user.getUserId(), FileUtil.getBytes(portrait));
            } catch (PortalException | SystemException | IOException e) {
                _log.error(e.getMessage());
            }
        }
    }

    protected static void addOrganizations(
            User defaultUser, Organization parentOrganization)
             {

        for (Object[] organizationArray : _ORGANIZATION_ARRAYS) {
            String name = (String) organizationArray[0];
            long regionId = (Long)organizationArray[1];
            long countryId = (Long)organizationArray[2];
            String type = (String)organizationArray[3];

            Organization organization = null;
            try {
                organization = OrganizationLocalServiceUtil.getOrganization(defaultUser.getCompanyId(), name);
            } catch (Exception e) {}

            try {
                if (Validator.isNull(organization)) {
                    organization =
                            OrganizationLocalServiceUtil.addOrganization(
                                    defaultUser.getUserId(),
                                    parentOrganization.getOrganizationId(), name, type,
                                    regionId, countryId,
                                    ListTypeConstants.ORGANIZATION_STATUS_DEFAULT,
                                    StringPool.BLANK, true, null);

                    GroupLocalServiceUtil.updateFriendlyURL(
                            organization.getGroupId(),
                            FriendlyURLNormalizerUtil.normalize(
                                    StringPool.SLASH + organizationArray[4]));
                }
                createDemoExpandoOrg(organization);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void createDemoExpandoOrg(Organization organization) {
        try {
            organization.getExpandoBridge().setAttribute(EXPANDO_DEMO_COLUMN_NAME, Boolean.TRUE, false);
        } catch (Exception e) {
            _log.error(e);
        }
    }

    private static void addOrgExpandoColumnDemo(long companyId) {
        try {
            ExpandoTable expandoTable = ExpandoTableLocalServiceUtil.fetchDefaultTable(companyId, Organization.class.getName());
            if (Validator.isNull(expandoTable)) {
                expandoTable = ExpandoTableLocalServiceUtil.addDefaultTable(companyId, Organization.class.getName());
            }
            if (Validator.isNotNull(expandoTable)) {
                ExpandoColumnLocalServiceUtil.addColumn(expandoTable.getTableId(), EXPANDO_DEMO_COLUMN_NAME, ExpandoColumnConstants.BOOLEAN, Boolean.FALSE);
            } else {
                _log.error("default expando table for organization is null");
            }
        } catch (Exception e) {
            _log.error(e);
        }
    }

    private static Role createRole(long userId) {
        Role role = null;
        try {
            role = RoleLocalServiceUtil.addRole(userId, null,0, "Demo", null, null,
                    RoleConstants.TYPE_SITE, StringPool.BLANK, new ServiceContext());
        } catch (PortalException | SystemException e) {
            _log.error(e);
        }
        return role;
    }

    private static Log _log = LogFactoryUtil.getLog(
            SetupWizardSampleDataUtil.class);

    private static Object[][] _ORGANIZATION_ARRAYS = {
            {
                    PropsValues.COMPANY_DEFAULT_NAME + " Demo Московский офис", 13001L, 13L, OrganizationConstants.TYPE_LOCATION, "moskva"
            },
            {
                    PropsValues.COMPANY_DEFAULT_NAME + " Demo Офис в Санкт-Петербурге", 13002L, 13L,
                    OrganizationConstants.TYPE_REGULAR_ORGANIZATION, "sankt-peterburg"
            },
            {
                    PropsValues.COMPANY_DEFAULT_NAME + " Demo Новосибирский офис", 13049L, 13L,
                    OrganizationConstants.TYPE_REGULAR_ORGANIZATION, "novosibirsk"
            },
    };

    private static String EXPANDO_DEMO_COLUMN_NAME = "demo";
}