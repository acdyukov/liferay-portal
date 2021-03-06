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

package com.liferay.portlet.asset.model;

import com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseDDMFormValuesReader implements DDMFormValuesReader {

	@Override
	public List<DDMFormFieldValue> getDDMFormFieldValues(
			String ddmFormFieldType)
		throws PortalException {

		List<DDMFormFieldValue> filteredDDMFormFieldValues = new ArrayList<>();

		DDMFormValues ddmFormValues = getDDMFormValues();

		addDDMFormFieldValuesByType(
			ddmFormValues.getDDMFormFieldValues(), filteredDDMFormFieldValues,
			ddmFormFieldType);

		return filteredDDMFormFieldValues;
	}

	protected void addDDMFormFieldValuesByType(
		List<DDMFormFieldValue> ddmFormFieldValues,
		List<DDMFormFieldValue> filteredDDMFormFieldValues,
		String ddmFormFieldType) {

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			if (ddmFormFieldType.equals(ddmFormFieldValue.getType())) {
				filteredDDMFormFieldValues.add(ddmFormFieldValue);
			}

			addDDMFormFieldValuesByType(
				ddmFormFieldValue.getNestedDDMFormFieldValues(),
				filteredDDMFormFieldValues, ddmFormFieldType);
		}
	}

}