package com.chessyoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.chessyoup.connector.Device;
import com.chessyoup.connector.RemoteService;
import com.chessyoup.connector.gcm.GCMDevice;
import com.chessyoup.utils.HttpClient;
import com.chessyoup.utils.HttpClientResponse;

public class ChessYoUpRemoteService implements RemoteService {
	
	private String url;
	
	public ChessYoUpRemoteService(String url){
		this.url = url;
	}
	
	@Override
	public boolean register(Device device) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("device_id", device.getDeviceIdentifier() );		
		params.put("phone_number", device.getDevicePhoneNumber() != null ? device.getDevicePhoneNumber() : "");
		params.put("gcm_registration_id", device.getRegistrationId());
		params.put("google_account", device.getGoogleAccount());		
				
		try {
			HttpClientResponse reponse = HttpClient.getInstance().post(url+"/register", params);
			Log.d("ChessYoUpRemoteService", "Remote server response:"+reponse );
			return true;
		} catch (IOException e) {
			Log.e("ChessYoUpRemoteService", "Remote server response error:"+e.getMessage() );
			e.printStackTrace();
			return false;
		} 
	}

	@Override
	public boolean unRegister(Device device) {
		return true;
	}

	@Override
	public Device findByPhoneNumberlookup(String phoneNumber)
			throws IOException {
		
		StringBuffer findUrl = new StringBuffer();
		findUrl.append(this.url).append("/phone/").append(phoneNumber);		
		
		HttpClientResponse reponse = HttpClient.getInstance().readEntity(findUrl.toString(), "");
		Log.d("ChessYoUpRemoteService", "Remote server response:"+reponse );
		
		return null;
		
	}

	@Override
	public Device findByAccount(String account) throws IOException {
		StringBuffer findUrl = new StringBuffer();
		findUrl.append(this.url).append("/account/").append(account);		
		
		HttpClientResponse reponse = HttpClient.getInstance().readEntity(findUrl.toString(), "");
		Log.d("ChessYoUpRemoteService", "Remote server response:"+reponse );
		
		return null;
	}

	@Override
	public List<Device> search(String keyword) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
