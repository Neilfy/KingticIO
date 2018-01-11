package com.kingtic.kingticIO.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.kingtic.kingticIO.impl.KingticIOInstallationNodeService;
import com.kingtic.kingticIO.impl.KingticIOProgramNodeService;
import com.ur.urcap.api.contribution.DaemonService;
import com.ur.urcap.api.contribution.InstallationNodeService;
import com.ur.urcap.api.contribution.ProgramNodeService;
import com.kingtic.kingticIO.impl.MyDaemonDaemonService;

/**
 * Hello world activator for the OSGi bundle URCAPS contribution
 *
 */
public class Activator implements BundleActivator {
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		MyDaemonDaemonService daemonService = new MyDaemonDaemonService();
		//daemonService.getDaemon().start();
		KingticIOInstallationNodeService helloWorldInstallationNodeService = new KingticIOInstallationNodeService(daemonService);

		bundleContext.registerService(InstallationNodeService.class, helloWorldInstallationNodeService, null);
		bundleContext.registerService(ProgramNodeService.class, new KingticIOProgramNodeService(), null);
		bundleContext.registerService(DaemonService.class, daemonService, null);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("Activator says Goodbye World!");
	}
}

