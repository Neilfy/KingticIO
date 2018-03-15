package com.kingtic.kingticIO.impl;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

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
	private String[] addrs={"kingtic_in_0,0,0"
			, "kingtic_in_1,1,0"
			, "kingtic_in_2,2,0"
			, "kingtic_in_3,3,0"
			, "kingtic_in_4,4,0"
			, "kingtic_in_5,5,0"
			, "kingtic_in_6,6,0"
			, "kingtic_in_7,7,0"
			, "kingtic_in_8,8,0"
			, "kingtic_in_9,9,0"
			, "kingtic_in_10,10,0"
			, "kingtic_in_11,11,0"
			, "kingtic_in_12,12,0"
			, "kingtic_in_13,13,0"
			, "kingtic_in_14,14,0"
			, "kingtic_in_15,15,0"
			
			,"kingtic_out_0,0,0"
			, "kingtic_out_1,1,0"
			, "kingtic_out_2,2,0"
			, "kingtic_out_3,3,0"
			, "kingtic_out_4,4,0"
			, "kingtic_out_5,5,0"
			, "kingtic_out_6,6,0"
			, "kingtic_out_7,7,0"
			, "kingtic_out_8,8,0"
			, "kingtic_out_9,9,0"
			, "kingtic_out_10,10,0"
			, "kingtic_out_11,11,0"
			, "kingtic_out_12,12,0"
			, "kingtic_out_13,13,0"
			, "kingtic_out_14,14,0"
			, "kingtic_out_15,15,0"
	};
	

	private static final String ENABLED_KEY = "enabled";
	private static final String XMLRPC_VARIABLE = "my_daemon";
	private final static String IMAGE_RED = "com/kingtic/kingticIO/impl/red.png";
	private final static String IMAGE_GRAY = "com/kingtic/kingticIO/impl/gray.png";
	
	
	private String IP = "";
	
	ArrayList<Object> ioItems = new ArrayList<Object>();
	ArrayList<KingticIO> ioInfo = new ArrayList<KingticIO>();
	ArrayList<InputButton> ioBtn = new ArrayList<InputButton>();

	private DataModel model;
	
	private final MyDaemonDaemonService daemonService;
	private XmlRpcMyDaemonInterface xmlRpcDaemonInterface;
	private Timer uiTimer;
	
	private BufferedImage img_red, img_gray;

	public KingticIOInstallationNodeContribution(MyDaemonDaemonService daemonService, DataModel model) {
		this.daemonService = daemonService;
		this.model = model;
		//
		img_red = loadImage(IMAGE_RED);
		img_gray = loadImage(IMAGE_GRAY);
		
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
	
	public void initIObtn(){
		ioBtn.add(ki0);
		ioBtn.add(ki1);
		ioBtn.add(ki2);
		ioBtn.add(ki3);
		ioBtn.add(ki4);
		ioBtn.add(ki5);
		ioBtn.add(ki6);
		ioBtn.add(ki7);
		ioBtn.add(ki8);
		ioBtn.add(ki9);
		ioBtn.add(ki10);
		ioBtn.add(ki11);
		ioBtn.add(ki12);
		ioBtn.add(ki13);
		ioBtn.add(ki14);
		ioBtn.add(ki15);
		
		ioBtn.add(ko0);
		ioBtn.add(ko1);
		ioBtn.add(ko2);
		ioBtn.add(ko3);
		ioBtn.add(ko4);
		ioBtn.add(ko5);
		ioBtn.add(ko6);
		ioBtn.add(ko7);
		ioBtn.add(ko8);
		ioBtn.add(ko9);
		ioBtn.add(ko10);
		ioBtn.add(ko11);
		ioBtn.add(ko12);
		ioBtn.add(ko13);
		ioBtn.add(ko14);
		ioBtn.add(ko15);
		
		for(int i=0; i<ioBtn.size(); ++i)
		{
			ioBtn.get(i).setImage(img_gray);
		}
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
	
	@Input(id = "ki0")
	private InputButton ki0;
	@Input(id = "ki1")
	private InputButton ki1;
	@Input(id = "ki2")
	private InputButton ki2;
	@Input(id = "ki3")
	private InputButton ki3;
	@Input(id = "ki4")
	private InputButton ki4;
	@Input(id = "ki5")
	private InputButton ki5;
	@Input(id = "ki6")
	private InputButton ki6;
	@Input(id = "ki7")
	private InputButton ki7;
	@Input(id = "ki8")
	private InputButton ki8;
	@Input(id = "ki9")
	private InputButton ki9;
	@Input(id = "ki10")
	private InputButton ki10;
	@Input(id = "ki11")
	private InputButton ki11;
	@Input(id = "ki12")
	private InputButton ki12;
	@Input(id = "ki13")
	private InputButton ki13;
	@Input(id = "ki14")
	private InputButton ki14;
	@Input(id = "ki15")
	private InputButton ki15;
	
	@Input(id = "ko0")
	private InputButton ko0;
	@Input(id = "ko1")
	private InputButton ko1;
	@Input(id = "ko2")
	private InputButton ko2;
	@Input(id = "ko3")
	private InputButton ko3;
	@Input(id = "ko4")
	private InputButton ko4;
	@Input(id = "ko5")
	private InputButton ko5;
	@Input(id = "ko6")
	private InputButton ko6;
	@Input(id = "ko7")
	private InputButton ko7;
	@Input(id = "ko8")
	private InputButton ko8;
	@Input(id = "ko9")
	private InputButton ko9;
	@Input(id = "ko10")
	private InputButton ko10;
	@Input(id = "ko11")
	private InputButton ko11;
	@Input(id = "ko12")
	private InputButton ko12;
	@Input(id = "ko13")
	private InputButton ko13;
	@Input(id = "ko14")
	private InputButton ko14;
	@Input(id = "ko15")
	private InputButton ko15;
	
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
	
	///tab
	@Input(id = "tabIoSetting")
	private InputButton tabIoSetting;
	@Input(id = "tabIoMonitor")
	private InputButton tabIoMonitor;
	
	@Div(id="divIoSetting")
	private DivComponent divIOSetting;
	@Div(id="divIoMonitor")
	private DivComponent divIOMonitor;
	
	@Input(id = "tabIoSetting")
	public void ontabSettingClick(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_RELEASED) {
			divIOSetting.setVisible(true);
			divIOMonitor.setVisible(false);
			tabIoSetting.setEnabled(false);
			tabIoMonitor.setEnabled(true);
		}
	}
	
	@Input(id = "tabIoMonitor")
	public void ontabMonitorClick(InputEvent event) {
		if (event.getEventType() == InputEvent.EventType.ON_RELEASED) {
			divIOMonitor.setVisible(true);
			divIOSetting.setVisible(false);
			tabIoSetting.setEnabled(true);
			tabIoMonitor.setEnabled(false);
		}
	}

	@Override
	public void openView() {
		divIOMonitor.setVisible(false);
		tabIoSetting.setText("I/O设置");
		tabIoMonitor.setText("I/O监控");
		tabIoSetting.setEnabled(false);
		
		connectTCPButton.setText("连接");
		cleanBotton.setText("清除");
		selIOs.setItems(ioItems);
		//io btn
		initIObtn();
		
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
		}, 0, 300);
	}
	
	private BufferedImage loadImage(String path) {
		BufferedImage bufferedImage = null;
		BufferedInputStream bufferedInputStream = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream(path));
		try {
			bufferedImage = ImageIO.read(bufferedInputStream);
		} catch (IOException e) {
			System.err.println("Unable to load image: " + path);
		} finally {
			if (bufferedImage != null) {
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					System.err.println("Failed to close image input stream " + e.getMessage());
				}
			}
		}
		return bufferedImage;
	}
	
	private void updateName(int idx, String name)
	{
		ioItems.set(idx, name);
		selIOs.setItems(ioItems);;
		selIOs.selectItemAtIndex(idx);
		System.out.println(ioItems.size());
	}
	
	private void updateUI() {
		try {
//			String list = xmlRpcDaemonInterface.GetIO("0,1");
//			
//			if(!list.isEmpty())
//			{
//				String[] vals = list.split(",");
//				for(int i=0; i<vals.length; ++i)
//				{
//					System.out.println(ioBtn.get(i));
//					if(Integer.parseInt(vals[i])==0)
//					{
//						ioBtn.get(i).setImage(img_gray);
//					}else
//					{
//						ioBtn.get(i).setImage(img_red);
//					}
//				}
//			}
			
			String ret_in = xmlRpcDaemonInterface.GetIO("0,1");
			String ret_out = xmlRpcDaemonInterface.GetIO("1,1");
			if(!ret_in.isEmpty() && !ret_out.isEmpty())
			{
				Boolean[] status_in = new Boolean[16];
				Boolean[] status_out = new Boolean[16];
				Integer val_in = new Integer(ret_in);
				Integer val_out = new Integer(ret_out);
				for(int i=0; i<16; i++)
				{
					status_in[i] = ((val_in>>i) & 0x1) == 0 ? false : true;
					status_out[i] = ((val_out>>i) & 0x1) == 0 ? false : true;
				}
				for(int i=0; i<16; i++)
				{
					if(status_in[i])
					{
						ioBtn.get(i).setImage(img_red);
					}else
					{
						ioBtn.get(i).setImage(img_gray);
					}
					
					if(status_out[i])
					{
						ioBtn.get(i+16).setImage(img_red);
					}else
					{
						ioBtn.get(i+16).setImage(img_gray);
					}
				}
			}
			
		} catch (XmlRpcException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Boolean[] getStatusFromHoldingRegisters() throws XmlRpcException, UnknownResponseException
	{
		Boolean[] status = new Boolean[16];
		String ret = xmlRpcDaemonInterface.GetIO("0,1");
		Short val = new Short(ret);
		for(int i=0; i<16; i++)
		{
			status[i] = ((val>>i) & 0x1) == 0 ? false : true;
		}
		return status;
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
	
	public ArrayList<Object> getIOInputItems()
	{
		ArrayList<Object> ret = new ArrayList<Object>();
		for(int i=0; i<16; ++i)
		{
			ret.add(ioItems.get(i));
		}
		return ret;
	}
	
	public ArrayList<Object> getIOOutputItems()
	{
		ArrayList<Object> ret = new ArrayList<Object>();
		for(int i=16; i<32; ++i)
		{
			ret.add(ioItems.get(i));
		}
		return ret;
	}
	
	public KingticIO getIO(int idx)
	{
		return ioInfo.get(idx);
	}
	
	public KingticIO getIOOutput(int idx)
	{
		return ioInfo.get(16+idx);
	}
	
	public KingticIO getIOInput(int idx)
	{
		return ioInfo.get(idx);
	}
	
	public String getXMLRPCVariable() {return XMLRPC_VARIABLE;}
	
	public XmlRpcMyDaemonInterface getXmlRpcDaemonInterface() {return xmlRpcDaemonInterface; }

}
