package com.nf.NewTest.impl;

import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.InstallationNodeService;
import com.ur.urcap.api.domain.URCapAPI;

import java.io.InputStream;

import com.ur.urcap.api.domain.data.DataModel;
import com.nf.NewTest.impl.MyDaemonDaemonService;

public class URTestInstallationNodeService implements InstallationNodeService {

	private final MyDaemonDaemonService daemonService;
	public URTestInstallationNodeService(MyDaemonDaemonService daemonService) 
	{
		this.daemonService = daemonService;
	}

	@Override
	public InstallationNodeContribution createInstallationNode(URCapAPI api, DataModel model) {
		return new URTestInstallationNodeContribution(daemonService, model);
	}

	@Override
	public String getTitle() {
		return "URTest";
	}

	@Override
	public InputStream getHTML() {
		InputStream is = this.getClass().getResourceAsStream("/com/nf/URTest/impl/installation.html");
		return is;
	}
}
