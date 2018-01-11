package com.kingtic.kingticIO.impl;

import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.InstallationNodeService;
import com.ur.urcap.api.domain.URCapAPI;

import java.io.InputStream;

import com.ur.urcap.api.domain.data.DataModel;
import com.kingtic.kingticIO.impl.MyDaemonDaemonService;

public class KingticIOInstallationNodeService implements InstallationNodeService {

	private final MyDaemonDaemonService daemonService;
	public KingticIOInstallationNodeService(MyDaemonDaemonService daemonService) 
	{
		this.daemonService = daemonService;
	}

	@Override
	public InstallationNodeContribution createInstallationNode(URCapAPI api, DataModel model) {
		return new KingticIOInstallationNodeContribution(daemonService, model);
	}

	@Override
	public String getTitle() {
		return "kingtic I/O Setup";
	}

	@Override
	public InputStream getHTML() {
		InputStream is = this.getClass().getResourceAsStream("/com/kingtic/kingticIO/impl/installation.html");
		return is;
	}
}
