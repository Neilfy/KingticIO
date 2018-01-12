package com.kingtic.kingticIO.impl;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import com.kingtic.kingticIO.impl.MyDaemonDaemonService;
import com.kingtic.kingticIO.impl.XmlRpcMyDaemonInterface;

public class KingticIOInstallationNodeContribution implements InstallationNodeContribution {
	
	//io名，地址，权限（0：启用，1：程序员操作，2：禁用）
	private String[] addrs={"kingtic_in_0,0,0", "kingtic_in_1,10,0"
			, "kingtic_in_2,11,0","kingtic_out_0,100,0"
			, "kingtic_out_1,101,0"};
	

	private static final String ENABLED_KEY = "enabled";
	private static final String XMLRPC_VARIABLE = "my_daemon";
	
	
	private String IP = "";
	
	ArrayList<Object> ioItems = new ArrayList<Object>();
	ArrayList<KingticIO> ioInfo = new ArrayList<KingticIO>();

	private DataModel model;
	
	private final MyDaemonDaemonService daemonService;
	private XmlRpcMyDaemonInterface xmlRpcDaemonInterface;
	private Timer uiTimer;

	public KingticIOInstallationNodeContribution(MyDaemonDaemonService daemonService, DataModel model) {
		this.daemonService = daemonService;
		this.model = model;
		
		//读IO配置信息  
		for(int i=0; i<addrs.length; ++i)
		{
			String[] addr = addrs[i].split(",");
			ioInfo.add(new KingticIO(addr[0], addr[0], addr[1], addr[2]));
		}
		
		//根据用户设定更新ioInfo
		for(int i=0; i<ioInfo.size(); ++i)
		{
			KingticIO io = ioInfo.get(i);
			if(model.isSet(io.defaultName))
			{
				String[] vals = model.get(io.defaultName, io.defaultName+","+io.rule).split(",");
				if(vals.length == 2)
				{
					io.displayName = vals[0];
					io.rule = vals[1];
				}
			}
			ioItems.add(io.displayName);
		}

		xmlRpcDaemonInterface = new XmlRpcMyDaemonInterface("127.0.0.1", 40404);
	}
	
	@Input(id = "txtIP")
	private InputTextField IPText;
	
	@Input(id = "btnConnectTCP")
	private InputButton connectTCPButton;
	
	@Label(id = "lblTcpStatus")
	private LabelComponent tcpStatusLabel;
	
	
	@Input(id = "btnConnectTCP")
	public void onConnectClick(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			if(daemonService.getDaemon().getState() == DaemonContribution.State.RUNNING)
			{
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
			}else
			{
				tcpStatusLabel.setText("daemon或Rpc启动失败！");
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
			
			KingticIO io = ioInfo.get(idx);
			if(!val.isEmpty())
			{
				io.displayName = val;
				model.set(io.defaultName, val+","+io.rule);
			}else
			{
				io.displayName = io.defaultName;
				model.remove(io.defaultName);
			}
			updateName(idx, io.displayName);
		}
	}
	
	@Input(id = "btnClean")
	public void onCleanClick(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_CHANGE) {
			
		}
	}
	

	@Override
	public void openView() {
		connectTCPButton.setText("连接");
		System.out.println("My Daemon\n");
		selIOs.setItems(ioItems);

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
	
	private void updateName(int idx, String name)
	{
		ioItems.set(idx, name);
		selIOs.setItems(ioItems);;
		selIOs.selectItemAtIndex(idx);
		System.out.println(ioItems.size());
	}
	
	private void updateUI() {
		DaemonContribution.State state = getDaemonState();

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
		System.out.println(text);
		//daemonStatusLabel.setText(text);
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

	private DaemonContribution.State getDaemonState(){
		return daemonService.getDaemon().getState();
	}

	
	public ArrayList<Object> getIOItems()
	{
		return ioItems;
	}
	
	public KingticIO getIO(int idx)
	{
		return ioInfo.get(idx);
	}
	
	public String getXMLRPCVariable() {return XMLRPC_VARIABLE;}
	
	public XmlRpcMyDaemonInterface getXmlRpcDaemonInterface() {return xmlRpcDaemonInterface; }

}
