package com.chessyoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.chessyoup.connector.Device;
import com.chessyoup.connector.RemoteService;
import com.chessyoup.utils.HttpClient;

public class ChessYoUpRemoteService implements RemoteService {
	
	private String url;
	
	public ChessYoUpRemoteService(String url){
		this.url = url;
	}
	
	@Override
	public boolean register(Device device) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("device_id", device.getDeviceIdentifier() );		
		params.put("phone_number", device.getDevicePhoneNumber() != null ? device.getDevicePhoneNumber() : "");
		params.put("gcm_registration_id", device.getRegistrationId());
		int status = HttpClient.getInstance().post(url+"/register.php", params);
		Log.d("ChessYoUpRemoteService", "Remote serevr response status :"+status );
		return status >= 200 && status < 300; 
	}

	@Override
	public boolean unRegister(Device device) {
		return false;
	}

	@Override
	public Device lookup(String deviceIdentifier, String registrationId) {		
		return null;
	}
}
