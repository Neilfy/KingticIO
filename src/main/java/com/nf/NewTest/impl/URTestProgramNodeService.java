package com.nf.NewTest.impl;

import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.ProgramNodeService;
import com.ur.urcap.api.domain.URCapAPI;
import com.ur.urcap.api.domain.data.DataModel;

import java.io.InputStream;

public class URTestProgramNodeService implements ProgramNodeService {

	public URTestProgramNodeService() {
	}

	@Override
	public String getId() {
		return "TestNode";
	}

	@Override
	public String getTitle() {
		return "URTest";
	}

	@Override
	public InputStream getHTML() {
		InputStream is = this.getClass().getResourceAsStream("/com/nf/URTest/impl/programnode.html");
		return is;
	}

	@Override
	public boolean isDeprecated() {
		return false;
	}

	@Override
	public boolean isChildrenAllowed() {
		return true;
	}

	@Override
	public ProgramNodeContribution createNode(URCapAPI api, DataModel model) {
		return new URTestProgramNodeContribution(api, model);
	}
}
