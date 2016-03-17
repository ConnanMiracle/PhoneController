package com.example.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;

import android.os.Handler;
import android.util.Log;

public class Scanner extends Thread {
	public static  final String SCAN_REPLY_ACK="##CTL_SCAN_DEVICE_REPLY_ACK##";
	public static final String TAG = "DetectBroadcast";
	private static final int MAX_DATA_PACKET_LENGTH = 40;
	private byte[] sendBuffer = new byte[MAX_DATA_PACKET_LENGTH];
	private byte[] recvBuffer = new byte[MAX_DATA_PACKET_LENGTH];
	private String dataToSend;
	private DatagramSocket udpSendSocket;
	private DatagramSocket udpRecvSocket;
	private int localPort_send;
	private int localPort_recv;
	private int remotePort;
	private boolean start;
	private boolean run = false;

	private Handler mHandler;

	public Scanner(int local_send, int local_recv, int remote, Handler handler) {
		localPort_send = local_send;
		localPort_recv = local_recv;
		remotePort = remote;
		mHandler = handler;
		try {
			udpSendSocket = new DatagramSocket(localPort_send);
			udpRecvSocket = new DatagramSocket(localPort_recv);
		} catch (SocketException e) {
			// TODO 自动生成的 catch 块
			Log.e(TAG, e.toString());
		}
	}

	public void run() {
		DatagramPacket request = null;
		DatagramPacket replay = null;

		request = new DatagramPacket(sendBuffer, MAX_DATA_PACKET_LENGTH);
		dataToSend = "##CTL_SCAN_DEVICE_REQUEST_MSG##";
		byte[] data = dataToSend.getBytes();
		request.setData(data);
		request.setLength(data.length);
		request.setPort(remotePort);
		InetAddress broadcastAddr;
		try {
			broadcastAddr = InetAddress.getByName("255.255.255.255");
			request.setAddress(broadcastAddr);

		} catch (UnknownHostException e1) {
			// TODO 自动生成的 catch 块
			e1.printStackTrace();
		}
		if (udpSendSocket == null || udpRecvSocket == null) {
			start = false;
			Log.i(TAG, String.valueOf(start));
		}

		while (start) {
			// request
			try {
				Log.i(TAG, "sending detect message...");
				Log.i(TAG, new String(request.getData()));
				udpSendSocket.send(request);
				sleep(1000);
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
			// replay
			try {
				replay = new DatagramPacket(recvBuffer, MAX_DATA_PACKET_LENGTH);
				// try to get a reply until the timeout expires
				udpRecvSocket.setSoTimeout(3000);
				udpRecvSocket.receive(replay);
				String content = new String(replay.getData(), "UTF-8");
				//Log.i(TAG, "recive from jidinghe:" + content);
				if (content.contains("CTL_SCAN_DEVICE_REPLY_MSG")) {
					Log.i(TAG, "detect replay received from");
					int start = content.indexOf("_#") + 2;
					int end0 = content.indexOf("*", start);
					int end1=content.indexOf("##",end0);
					String id = content.substring(start, end0);
					String status=content.substring(end0+1, end1);
					String result = id + "  "+status +" "+ "IP:"
							+ replay.getAddress().toString().substring(1)
							+ "##";
					Log.i(TAG, "send to main: " + result);
					HashMap<String, String> map=new HashMap<String, String>();
					map.put("ip", replay.getAddress().toString().substring(1));
					map.put("id", id);
					map.put("status",status);
					mHandler.obtainMessage(MainActivity.DETECT_DEVICE_FOUND,map).sendToTarget();
					// break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void startScan() {
		if (run == false)
			super.start();
		run = true;
		start = true;
	}

	public void stopScan() {
		start = false;
		this.interrupt();
		udpSendSocket.close();
		udpRecvSocket.close();
	}
}
