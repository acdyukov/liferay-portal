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

package com.liferay.gradle.plugins.node.tasks;

import com.liferay.gradle.util.FileUtil;
import com.liferay.gradle.util.GradleUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.gradle.api.tasks.Input;

/**
 * @author Andrea Di Giorgi
 */
public class ExecuteNodeScriptTask extends ExecuteNodeTask {

	@Override
	public void executeNode() {
		setArgs(getCompleteArgs());

		super.executeNode();
	}

	@Input
	public File getScriptFile() {
		return GradleUtil.toFile(getProject(), _scriptFile);
	}

	public void setScriptFile(Object scriptFile) {
		_scriptFile = scriptFile;
	}

	protected List<String> getCompleteArgs() {
		List<String> completeArgs = new ArrayList<>();

		completeArgs.add(FileUtil.getAbsolutePath(getScriptFile()));

		completeArgs.addAll(getArgs());

		return completeArgs;
	}

	private Object _scriptFile;

}