package com.nf.NewTest.impl;

import org.apache.xmlrpc.XmlRpcException;

import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.domain.URCapAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.ui.annotation.Input;
import com.ur.urcap.api.ui.annotation.Label;
import com.ur.urcap.api.ui.component.InputButton;
import com.ur.urcap.api.ui.component.InputEvent;
import com.ur.urcap.api.ui.component.InputTextField;
import com.ur.urcap.api.ui.component.LabelComponent;

public class URTestProgramNodeContribution implements ProgramNodeContribution {
	private static final String NAME = "name";

	private final DataModel model;
	private final URCapAPI api;

	public URTestProgramNodeContribution(URCapAPI api, DataModel model) {
		this.api = api;
		this.model = model;
	}

	@Input(id = "txtCommand")
	private InputTextField commandTextField;
	
	@Input(id = "btnSend")
	private InputButton cmdSendButton;

	@Input(id = "btnSend")
	public void onSendClick(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			String cmd = commandTextField.getText();
			try {
				getInstallation().getXmlRpcDaemonInterface().SendCommand(cmd);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	@Override
	public void openView() {
		cmdSendButton.setText("发送");
	}

	@Override
	public void closeView() {
	}

	@Override
	public String getTitle() {
		return "SendCmd";
	}

	@Override
	public boolean isDefined() {
		return true;
	}

	@Override
	public void generateScript(ScriptWriter writer) {
		writer.assign("ret", getInstallation().getXMLRPCVariable() + ".send_Command(\"" + commandTextField.getText() + "\")");
		writer.appendLine("popup(ret, ret, False, False, blocking=True)");
		writer.writeChildren();
	}

	private String generatePopupMessage() {
		return model.isSet(NAME) ? "Hello " + getName() + ", welcome to PolyScope!" : "No name set";
	}


	private String getName() {
		return model.get(NAME, "");
	}

	private void setName(String name) {
		if ("".equals(name)){
			model.remove(NAME);
		}else{
			model.set(NAME, name);
		}
	}

	private URTestInstallationNodeContribution getInstallation() {
		return api.getInstallationNode(URTestInstallationNodeContribution.class);
	}

}
