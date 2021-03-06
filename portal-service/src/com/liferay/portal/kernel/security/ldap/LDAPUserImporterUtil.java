/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.portal.kernel.security.ldap;

import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.model.User;

import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapContext;

/**
 * @author Michael C. Han
 */
public class LDAPUserImporterUtil {

	public static User importUser(
			long ldapServerId, long companyId, LdapContext ldapContext,
			Attributes attributes, String password)
		throws Exception {

		return _getInstance().importUser(
			ldapServerId, companyId, ldapContext, attributes, password);
	}

	private static LDAPUserImporter _getInstance() {
		return _instance;
	}

	private static final LDAPUserImporter _instance =
		ProxyFactory.newServiceTrackedInstance(LDAPUserImporter.class);

}