package com.nf.NewTest.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.nf.NewTest.impl.URTestInstallationNodeService;
import com.nf.NewTest.impl.URTestProgramNodeService;
import com.ur.urcap.api.contribution.DaemonService;
import com.ur.urcap.api.contribution.InstallationNodeService;
import com.ur.urcap.api.contribution.ProgramNodeService;
import com.nf.NewTest.impl.MyDaemonDaemonService;

/**
 * Hello world activator for the OSGi bundle URCAPS contribution
 *
 */
public class Activator implements BundleActivator {
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		MyDaemonDaemonService daemonService = new MyDaemonDaemonService();
		//daemonService.getDaemon().start();
		URTestInstallationNodeService helloWorldInstallationNodeService = new URTestInstallationNodeService(daemonService);

		bundleContext.registerService(InstallationNodeService.class, helloWorldInstallationNodeService, null);
		bundleContext.registerService(ProgramNodeService.class, new URTestProgramNodeService(), null);
		bundleContext.registerService(DaemonService.class, daemonService, null);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("Activator says Goodbye World!");
	}
}

