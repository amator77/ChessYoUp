package com.chessyoup.transport;

import com.chessyoup.transport.Contact.PLATFORM;

public class Util {
	
	public static final String DESKTOP_WINDOWS = "dw";
	public static final String DESKTOP_LINUX = "dl";
	public static final String DESKTOP_IOS = "di";
	public static final String MOBILE_ANDROID = "ma";
	public static final String MOBILE_IPHONE = "mi";
	
	public static PLATFORM getPlatform(String resource){
		
		if( resource != null ){
			String parts[] = resource.split("-");
			
			try{
				String platform = parts[0];
				
				if( platform.equals(DESKTOP_WINDOWS) ){
					return PLATFORM.DESKTOP_WINDOWS;
				}
				else if( platform.equals(DESKTOP_LINUX) ){
					return PLATFORM.DESKTOP_LINUX;
				}
				else if( platform.equals(DESKTOP_IOS) ){
					return PLATFORM.DESKTOP_IOS;
				}
				else if( platform.equals(MOBILE_ANDROID) ){
					return PLATFORM.MOBILE_ANDROID;
				}
				else if( platform.equals(MOBILE_IPHONE) ){
					return PLATFORM.MOBILE_IPHONE;
				}
				else{
					return PLATFORM.UNKNOWN;
				}
			}
			catch(Exception e){
				e.printStackTrace();
				return PLATFORM.UNKNOWN;
			}			
		}
		else{
			return PLATFORM.UNKNOWN;
		}
	}
	
	public static String getClientType(String resource){
		if( resource != null ){
			String parts[] = resource.split("-");
			
			try{
				if( parts.length > 1){
					return  parts[1];
				}
				else{
					return null;
				}								
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}			
		}
		else{
			return null;
		}
	}
	
	public static String getClientVersion(String resource){		
		if( resource != null ){
			String parts[] = resource.split("-");
			
			try{
				if( parts.length > 2){
					return  parts[2];
				}
				else{
					return null;
				}								
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}			
		}
		else{
			return null;
		}
	}
	
	public static String getResourceFromId(String id){
		String parts[] = id.split("/");
		
		if( parts.length > 1 ){
			return parts[1];
		}
		else{
			return null;
		}
	}
}
