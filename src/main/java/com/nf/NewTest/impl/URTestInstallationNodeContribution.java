package com.nf.NewTest.impl;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.xmlrpc.XmlRpcException;

import com.ur.urcap.api.contribution.DaemonContribution;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.ui.annotation.Div;
import com.ur.urcap.api.ui.annotation.Input;
import com.ur.urcap.api.ui.annotation.Label;
import com.ur.urcap.api.ui.annotation.Select;
import com.ur.urcap.api.ui.component.DivComponent;
import com.ur.urcap.api.ui.component.InputButton;
import com.ur.urcap.api.ui.component.InputEvent;
import com.ur.urcap.api.ui.component.InputTextField;
import com.ur.urcap.api.ui.component.LabelComponent;
import com.ur.urcap.api.ui.component.SelectDropDownList;
import com.ur.urcap.api.ui.component.SelectList;
import com.nf.NewTest.impl.MyDaemonDaemonService;
import com.nf.NewTest.impl.XmlRpcMyDaemonInterface;

public class URTestInstallationNodeContribution implements InstallationNodeContribution {

	private static final String ENABLED_KEY = "enabled";
	private static final String XMLRPC_VARIABLE = "my_daemon";
	
	private String IP = "";
	
	ArrayList<Object> ioItems = new ArrayList<Object>();

	private DataModel model;
	
	private final MyDaemonDaemonService daemonService;
	private XmlRpcMyDaemonInterface xmlRpcDaemonInterface;
	private Timer uiTimer;

	public URTestInstallationNodeContribution(MyDaemonDaemonService daemonService, DataModel model) {
		this.daemonService = daemonService;
		this.model = model;
		ArrayList<KingticIO> ioInfo = new ArrayList<KingticIO>();
		ioInfo.add(new KingticIO("kingtic_in_0","",0));
		ioInfo.add(new KingticIO("kingtic_in_1","",0));
		
		xmlRpcDaemonInterface = new XmlRpcMyDaemonInterface("127.0.0.1", 40404);
	}

	@Input(id = "btnEnableDaemon")
	private InputButton enableDaemonButton;

	@Input(id = "btnDisableDaemon")
	private InputButton disableDaemonButton;

	@Label(id = "lblDaemonStatus")
	private LabelComponent daemonStatusLabel;
	
	@Input(id = "txtIP")
	private InputTextField IPText;
	
	@Input(id = "btnConnectTCP")
	private InputButton connectTCPButton;
	
	@Label(id = "lblTcpStatus")
	private LabelComponent tcpStatusLabel;
	
	@Input(id = "btnEnableDaemon")
	public void onStartClick(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			setDaemonEnabled(true);
			applyDesiredDaemonStatus();
		}
	}

	@Input(id = "btnDisableDaemon")
	public void onStopClick(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			setDaemonEnabled(false);
			applyDesiredDaemonStatus();
		}
	}
	
	@Input(id = "btnConnectTCP")
	public void onConnectClick(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			try {
				IP = IPText.getText();
				if(!IP.isEmpty())
				{
					Boolean ret = xmlRpcDaemonInterface.ConnectTCP(IP);
					//Boolean ret1 = xmlRpcDaemonInterface.SendCommand("11");
					tcpStatusLabel.setText("连接"+(ret?"成功":"失败"));
				}else
				{
					tcpStatusLabel.setText("IP地址不能为空！");
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	@Select(id="selIOs")
	private SelectDropDownList selIOs;
	@Input(id = "txtioName")
	private InputTextField ioNameText;
	@Input(id = "btnClean")
	private InputButton cleanBotton;
	
	@Input(id = "txtioName")
	public void onMessageChange(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			int idx = selIOs.getSelectedIndex();
			String val = ioNameText.getText();
			if(!val.isEmpty())
			{
				ioItems.set(idx, val);
			}else
			{
				ioItems.set(idx, "kingtic_in_io"+idx);
			}
			selIOs.setItems(ioItems);
		}
	}
	
	@Input(id = "btnClean")
	public void onCleanClick(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			
		}
	}
	

	@Override
	public void openView() {
		enableDaemonButton.setText("Start Daemon");
		disableDaemonButton.setText("Stop daemon");
		connectTCPButton.setText("连接");
		selIOs.setItems(ioItems);
		try {
			awaitDaemonRunning(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//UI updates from non-GUI threads must use EventQueue.invokeLater (or SwingUtilities.invokeLater)
		uiTimer = new Timer(true);
		uiTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						updateUI();
					}
				});
			}
		}, 0, 1000);
	}
	
	private void updateUI() {
		DaemonContribution.State state = getDaemonState();

		if (state == DaemonContribution.State.RUNNING) {
			enableDaemonButton.setEnabled(false);
			disableDaemonButton.setEnabled(true);
		} else {
			enableDaemonButton.setEnabled(true);
			disableDaemonButton.setEnabled(false);
		}

		String text = "";
		switch (state) {
		case RUNNING:
			text = "My Daemon runs";
			break;
		case STOPPED:
			text = "My Daemon stopped";
			break;
		case ERROR:
			text = "My Daemon failed";
			break;
		}
		daemonStatusLabel.setText(text);
	}

	@Override
	public void closeView() { 
		if (uiTimer != null) {
			uiTimer.cancel();
		}
	}


	@Override
	public void generateScript(ScriptWriter writer) {
		writer.globalVariable(XMLRPC_VARIABLE, "rpc_factory(\"xmlrpc\", \"http://127.0.0.1:40404/RPC2\")");
		// Apply the settings to the daemon on program start in the Installation pre-amble
		writer.appendLine(XMLRPC_VARIABLE + ".connect_TCP(\"" + IP + "\")");
	}
	
	private void setDaemonEnabled(Boolean enable) {
		model.set(ENABLED_KEY, enable);
	}
	
	private Boolean isDaemonEnabled() {
		return model.get(ENABLED_KEY, true); //This daemon is enabled by default
	}
	
	private DaemonContribution.State getDaemonState(){
		return daemonService.getDaemon().getState();
	}
	
	private void applyDesiredDaemonStatus() {
		if (isDaemonEnabled()) {
			// Download the daemon settings to the daemon process on initial start for real-time preview purposes
			try {
				awaitDaemonRunning(5000);
			} catch(Exception e){
				System.err.println("Could not set the title in the daemon process.");
			}
		} else {
			daemonService.getDaemon().stop();
		}
	}
	
	private void awaitDaemonRunning(long timeOutMilliSeconds) throws InterruptedException {
		daemonService.getDaemon().start();
		long endTime = System.nanoTime() + timeOutMilliSeconds * 1000L * 1000L;
		while(System.nanoTime() < endTime && (daemonService.getDaemon().getState() != DaemonContribution.State.RUNNING || !xmlRpcDaemonInterface.isReachable())) {
			Thread.sleep(100);
		}
	}
	
	public String getXMLRPCVariable() {return XMLRPC_VARIABLE;}
	
	public XmlRpcMyDaemonInterface getXmlRpcDaemonInterface() {return xmlRpcDaemonInterface; }

}
