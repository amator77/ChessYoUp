package com.chessyoup.transport.xmpp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smack.SASLAuthentication;

import android.content.Context;
import android.content.res.AssetManager;

public class XMPPConfigurationManager {
	
	private static final String GTALK_HOST = "talk.google.com";

	private static final String FACEBOOK_HOST = "chat.facebook.com";

	private static final int GTALK_PORT = 5222;

	private static final int FACEBOOK_PORT = 5222;
	
	private static final String GTALK_SERVICE = "gtalk";
	
	private static final String GTALK_RESOURCE = "chessyoup";
	
	private static ConnectionConfiguration gTalkConfiguration;
	
	private static ConnectionConfiguration facebookConfiguration;
			
	public static void init(Context appContext){
		facebookConfiguration = new ConnectionConfiguration(FACEBOOK_HOST,FACEBOOK_PORT);
		facebookConfiguration.setSASLAuthenticationEnabled(true);			        	        	
        SASLAuthentication.registerSASLMechanism(SASLXFacebookPlatformMechanism.NAME, SASLXFacebookPlatformMechanism.class);
        SASLAuthentication.supportSASLMechanism(SASLXFacebookPlatformMechanism.NAME, 0);
        facebookConfiguration.setSecurityMode(SecurityMode.enabled);
        facebookConfiguration.setTruststoreType("BKS");
        facebookConfiguration.setSendPresence(false);        
        facebookConfiguration.setTruststorePath(getCacertsPath(appContext));
        
        gTalkConfiguration = new ConnectionConfiguration(GTALK_HOST, GTALK_PORT,GTALK_SERVICE, ProxyInfo.forNoProxy());
        gTalkConfiguration.setSecurityMode(SecurityMode.required);
        gTalkConfiguration.setSASLAuthenticationEnabled(true);
        gTalkConfiguration.setDebuggerEnabled(true);
        gTalkConfiguration.setReconnectionAllowed(true);
        gTalkConfiguration.setRosterLoadedAtLogin(true);
        gTalkConfiguration.setSendPresence(true);
        gTalkConfiguration.setServiceName(GTALK_RESOURCE);
	}
	
	private static String getCacertsPath(Context appContext){						
		AssetManager manager = appContext.getAssets();				
		
		try {
			InputStream fis =  manager.open("cacerts.bks");
			File f = File.createTempFile("cacerts", "bks");
			byte[] buffer = new byte[1024];
			FileOutputStream fos = new FileOutputStream(f);
			int read = 0;
			
			while( (read = fis.read(buffer)) != -1 ){
				fos.write(buffer, 0, read);
			}
			
			fis.close();
			fos.close();
			return f.getPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;		
	}
}	
