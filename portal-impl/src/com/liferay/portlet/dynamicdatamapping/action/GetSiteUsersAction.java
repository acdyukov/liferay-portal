package com.liferay.portlet.dynamicdatamapping.action;

import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.*;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetSiteUsersAction extends Action
{
    @Override
    public ActionForward execute(
            ActionMapping actionMapping, ActionForm actionForm,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
            long groupId = ParamUtil.get(request, "groupId", themeDisplay.getScopeGroupId());
            String searchQuery = ParamUtil.get(request, "inputVal", StringPool.BLANK).toLowerCase();
            List<User> findedUsers  = UserLocalServiceUtil.search(themeDisplay.getCompanyId(), searchQuery,
                    WorkflowConstants.STATUS_APPROVED, (LinkedHashMap<String, Object>)null,
                    0, 10, (OrderByComparator)null);
            JSONArray jsonArray = JSONFactoryUtil.createJSONArray();
            for (User user: findedUsers) {
                JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
                String name = user.getFullName().toLowerCase();
                String screenName = user.getScreenName().toLowerCase();
                String emailAddress = user.getEmailAddress().toLowerCase();
                String jobTitle = user.getJobTitle().toLowerCase();
                if ( ((name.contains(searchQuery))
                        || (screenName.contains(searchQuery))
                        || (emailAddress.contains(searchQuery))
                        || (jobTitle.contains(searchQuery)))
                        &&  GroupLocalServiceUtil.hasUserGroup(user.getUserId(), groupId) ) {
                    jsonObject.put("name", user.getFullName());
                    jsonObject.put("screenName", user.getScreenName());
                    jsonObject.put("emailAddress", user.getEmailAddress());
                    jsonObject.put("jobTitle", user.getJobTitle());
                    jsonObject.put("id", user.getUserId());
                    jsonArray.put(jsonObject);
                }
            }
            response.setContentType(ContentTypes.APPLICATION_JSON);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            ServletResponseUtil.write(response, jsonArray.toString());
        } catch (Exception e) {
            JSONObject jsonObject = JSONFactoryUtil.createJSONObject();
            jsonObject.putException(e);
            ServletResponseUtil.write(response, jsonObject.toString());
        }
        return null;
    }
}
