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

package com.liferay.portlet.sitesadmin.search;

import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

import javax.portlet.PortletResponse;

/**
 * @author Jorge Ferrer
 */
public class SiteChecker extends RowChecker {

	public SiteChecker(PortletResponse portletResponse, PermissionChecker permissionChecker) {
		super(portletResponse);
		_permissionChecker = permissionChecker;
		//super(portletResponse);
	}

	@Override
	public boolean isDisabled(Object obj) {
		Group group = (Group)obj;

		try {
			if (group.isCompany() ||
				PortalUtil.isSystemGroup(group.getName())) {

				return true;
			}
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		if (!_permissionChecker.hasPermission(group.getGroupId(), Group.class.getName(),
				group.getGroupId(), ActionKeys.DELETE)) {
			return true;
		}

		return super.isDisabled(obj);
	}

	private PermissionChecker _permissionChecker;

	private static Log _log = LogFactoryUtil.getLog(SiteChecker.class);

}