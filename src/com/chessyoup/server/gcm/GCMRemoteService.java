package com.chessyoup.server.gcm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.chessyoup.connector.Device;
import com.chessyoup.connector.GenericDevice;
import com.chessyoup.connector.gcm.GCMDevice;
import com.chessyoup.server.RemoteService;
import com.chessyoup.server.Room;
import com.chessyoup.utils.HttpClient;
import com.chessyoup.utils.HttpClientResponse;

public class GCMRemoteService implements RemoteService {

	private String url;

	public GCMRemoteService(String url) {
		this.url = url;
	}

	@Override
	public boolean register(Device device,String roomId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("room_id", roomId);
		params.put("device_id", device.getDeviceIdentifier());
		params.put(
				"phone_number",
				device.getDevicePhoneNumber() != null ? device
						.getDevicePhoneNumber() : "");
		params.put("gcm_registration_id", device.getRegistrationId());
		params.put("google_account", device.getAccount());

		try {
			HttpClientResponse reponse = HttpClient.getInstance().post(
					url + "/register", params);
			Log.d("ChessYoUpRemoteService", "Remote server response:" + reponse);
			return true;
		} catch (IOException e) {
			Log.e("ChessYoUpRemoteService",
					"Remote server response error:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean unRegister(Device device) {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("device_id", device.getDeviceIdentifier());

		try {
			HttpClientResponse reponse = HttpClient.getInstance().post(
					url + "/unregister", params);
			Log.d("ChessYoUpRemoteService", "Remote server response:" + reponse);
			return true;
		} catch (IOException e) {
			Log.e("ChessYoUpRemoteService",
					"Remote server response error:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Device findByPhoneNumber(String phoneNumber) throws IOException {

		StringBuffer findUrl = new StringBuffer();
		findUrl.append(this.url).append("/phone/").append(phoneNumber);

		HttpClientResponse reponse = HttpClient.getInstance().readEntity(
				findUrl.toString(), "");
		Log.d("ChessYoUpRemoteService", "Remote server response:" + reponse);

		try {
			JSONObject json = new JSONObject(reponse.getBody());

			GenericDevice device = new GenericDevice();
			device.setDeviceIdentifier(json.getString("device_id"));
			device.setRegistrationId(json.getString("gcm_registration_id"));
			device.setDevicePhoneNumber(phoneNumber);

			return device;
		} catch (JSONException e) {
			Log.d("ChessYoUpRemoteService", "Not an json entity from server!");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Device findByAccount(String account) throws IOException {
		StringBuffer findUrl = new StringBuffer();
		findUrl.append(this.url).append("/account/").append(account);

		HttpClientResponse reponse = HttpClient.getInstance().readEntity(
				findUrl.toString(), "");
		Log.d("ChessYoUpRemoteService", "Remote server response:" + reponse);
		try {
			JSONObject json = new JSONObject(reponse.getBody());

			GenericDevice device = new GenericDevice();
			device.setDeviceIdentifier(json.getString("device_id"));
			device.setRegistrationId(json.getString("gcm_registration_id"));
			device.setAccount(account);

			return device;
		} catch (JSONException e) {
			Log.d("ChessYoUpRemoteService", "Not an json entity from server!");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Device> devices(String roomId) throws IOException {
		List<Device> devices = new ArrayList<Device>();
		StringBuffer findUrl = new StringBuffer();
		findUrl.append(this.url).append("/rooms/").append(roomId);

		HttpClientResponse reponse = HttpClient.getInstance().readEntity(
				findUrl.toString(), "");
		Log.d("ChessYoUpRemoteService", "Remote server response:" + reponse);

		try {
			JSONArray jsonArray = new JSONArray(reponse.getBody());

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONArray jsonArray2 = jsonArray.getJSONArray(i);
				GCMDevice device = new GCMDevice();
				device.setDeviceIdentifier(jsonArray2.getString(0));
				device.setRegistrationId(jsonArray2.getString(1));
				device.setGoogleAccount(jsonArray2.getString(2));
				device.setDevicePhoneNumber(jsonArray2.getString(3));
				
				devices.add(device);
			}

			Log.d("ChessYoUpRemoteService", "Devices :" + devices.toString());
			return devices;
		} catch (JSONException e) {
			Log.d("ChessYoUpRemoteService", "Not an json entity from server!");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Room> rooms() throws IOException {
		List<Room> rooms = new ArrayList<Room>();
		StringBuffer findUrl = new StringBuffer();
		findUrl.append(this.url).append("/rooms");

		HttpClientResponse reponse = HttpClient.getInstance().readEntity(
				findUrl.toString(), "");
		Log.d("ChessYoUpRemoteService", "Remote server response:" + reponse);

		try {
			JSONArray jsonArray = new JSONArray(reponse.getBody());

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONArray jsonArray2 = jsonArray.getJSONArray(i);
				GCMRoom room = new GCMRoom();
				room.setId(jsonArray2.getString(0));
				room.setName(jsonArray2.getString(1));
				room.setSenderId(jsonArray2.getString(2));
				room.setApiKey(jsonArray2.getString(3));
				room.setJoinedUsers(jsonArray2.getInt(4));
				rooms.add(room);
			}

			Log.d("ChessYoUpRemoteService", "Online rooms :" + rooms.toString());
			return rooms;
		} catch (JSONException e) {
			Log.d("ChessYoUpRemoteService", "Not an json entity from server!");
			e.printStackTrace();
			return null;
		}
	}
}
