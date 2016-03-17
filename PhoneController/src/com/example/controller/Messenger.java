package com.example.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class Messenger {
	
	// 主动拨号请求
	public static final String DIAL_CALL_REQUEST = "##CTL_DIAL_CALL_REQUEST_#";
	public static final String DIAL_CALL_REPLY_CALLING = "##CTL_DIAL_CALL_REPLY_CALLING##";		
	public static final String DIAL_CALL_REPLY_CALLING_ACK = "##CTL_DIAL_CALL_REPLY_CALLING_ACK##";		
	public static final String DIAL_CALL_REPLY_SUCCESS = "##CTL_DIAL_CALL_REPLY_SUCCESS##";
	public static final String DIAL_CALL_REPLY_FAILED = "##CTL_DIAL_CALL_REPLY_FAILED##";
	public static final String DIAL_CALL_REQUEST_ACK = "##CTL_DIAL_CALL_REQUEST_ACK##";

	// 来电
	public static final String DIAL_CALLED_REQUEST = "##CTL_DIAL_CALLED_REQUEST_#";// ##CTL_DIAL_CALLED_REQUEST_#NUM##
	public static final String DIAL_CALLED_REPLY_PENDING = "##CTL_DIAL_CALLED_REPLY_PENDING##";// ##CTL_DIAL_CALLED_REPLY_PENDING##
	public static final String DIAL_CALLED_REPLY_ACCEPT = "##CTL_DIAL_CALLED_REPLY_ACCEPT##";// "##CTL_DIAL_CALLED_REPLY_ACCEPT##"
	public static final String DIAL_CALLED_REPLY_DECLINE = "##CTL_DIAL_CALLED_REPLY_DECLINE##";// ##CTL_DIAL_CALLED_REPLY_DECLINE##
	public static final String DIAL_CALLED_REQUEST_ACK = "##CTL_DIAL_CALLED_REQUEST_ACK##";

	// 挂机
	public static final String DIAL_BYE_REQUEST = "##CTL_DIAL_BYE_REQUEST##";
	public static final String DIAL_BYE_REPLY = "##CTL_DIAL_BYE_REPLY##";
	public static final String DIAL_BYE_ACK = "##CTL_DIAL_BYE_ACK##";
	
	//取消会话
	public static final String DIAL_CALL_REQUEST_CANCEL = "##CTL_DIAL_CALL_REQUEST_CANCEL##";
	public static final String DIAL_CALL_REQUEST_CANCEL_ACK = "##CTL_DIAL_CALL_REQUEST_CANCEL_ACK##";
	
	//超时
	public static final String DIAL_CALLED_REQUEST_TIMEOUT = "##CTL_DIAL_CALLED_REQUEST_TIMEOUT##";
	public static final String DIAL_CALLED_REQUEST_TIMEOUT_ACK = "##CTL_DIAL_CALLED_REQUEST_TIMEOUT_ACK##";
	
	//机顶盒下线消息
	public static final String SHUTDOWN_MSG = "##CTL_SHUTDOWN_MSG##";
	
	//帧速率设置消息
	public static final String FRAME_RATE_MSG="##FRAME_RATE_#";
	public static final String  FRAME_RATE_MSG_ACK="##FRAME_RATE_ACK##";
	
	//分辨率设置
	public static final String RESOLUTION_MSG="##RESOLUTION_#";
	public static final String RESOLUTION_MSG_ACK="##RESOLUTION_ACK##";
	

	
    public static String TAG="Messenger";
	private String remoteIp;
	private int remotePort;
	private Socket mClientSocket;
	private OutputStream mOutputStream;

	static Messenger instance;
	private Context ctx;
	private Handler mHandler;
	private ServerSocket mServerSocket;
	private String result = "";
	private boolean shouldRunning;

	private  List<Socket> socketQueue = new ArrayList<Socket>();
	Thread serverThread;
	void initServerSocket() {
		serverThread=new Thread() {
			public void run() {
				try {
					mServerSocket = new ServerSocket();
					mServerSocket.setReuseAddress(true);
					InetSocketAddress address = new InetSocketAddress(8000);
					mServerSocket.bind(address);
					if (mServerSocket != null)
						while (shouldRunning) {
							final Socket acceptedSocket = mServerSocket.accept();
							if (!socketQueue.contains(acceptedSocket))
								socketQueue.add(acceptedSocket);
							startListen(acceptedSocket);
						}
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
			
		};
		serverThread.start();
	}

	protected void startListen(final Socket socket) {
		new Thread(new Runnable() {
			public void run() {
				BufferedReader in;
				try {
					while (!socket.isClosed()) {
						in = new BufferedReader(new InputStreamReader(socket
								.getInputStream(), "UTF-8"));
						String str = in.readLine();
						if (str == null || str.equals("")) {
							break;
						}
						Log.i(TAG, "server" + socket + "str =" + str);
						if (str.contains(DIAL_CALLED_REQUEST)) {
							mHandler.obtainMessage(MainActivity.CALLED_REQUEST,
									str).sendToTarget();
						}else if (str.contains(DIAL_CALLED_REQUEST_ACK)) {
							mHandler.obtainMessage(
									MainActivity.CALLED_REQUEST_ACK, str)
									.sendToTarget();
							Log.i(TAG, "incoming ack : "+str);
						}else if (str.contains(DIAL_CALL_REPLY_CALLING)) {
							mHandler.obtainMessage(MainActivity.REPLY_CALLING,
									str).sendToTarget();
						} else if (str.contains(DIAL_CALL_REPLY_SUCCESS)) {
							mHandler.obtainMessage(MainActivity.REPLY_SUCCESS,
									str).sendToTarget();
						} else if (str.contains(DIAL_CALL_REPLY_FAILED)) {
							mHandler.obtainMessage(MainActivity.REPLY_FAILED,
									str).sendToTarget();
						} else if (str.contains(DIAL_BYE_REPLY)) {
							mHandler.obtainMessage(MainActivity.BYE_REPLY,
									str).sendToTarget();
						} else if (str.contains(DIAL_BYE_REQUEST)) {
							mHandler.obtainMessage(MainActivity.BYE_REQUEST,
									str).sendToTarget();
						} else if (str.contains(DIAL_BYE_ACK)) {
							mHandler.obtainMessage(MainActivity.BYE_ACK, str)
									.sendToTarget();
						} else if (str.contains(DIAL_CALL_REQUEST_CANCEL_ACK)) {
							mHandler.obtainMessage(MainActivity.CANCEL_ACK, str)
							.sendToTarget();
				     } else if (str.contains(DIAL_CALLED_REQUEST_TIMEOUT)) {
					    mHandler.obtainMessage(MainActivity.REQUEST_TIMEOUT, str)
				     	.sendToTarget();
		        	}  else if (str.contains(SHUTDOWN_MSG)) {
			            mHandler.obtainMessage(MainActivity.SHUTDOWN, str)
		            	.sendToTarget();
		        	}  else if (str.contains(FRAME_RATE_MSG_ACK)) {
		        		mHandler.obtainMessage(MainActivity.FRAME_RATE_MSG_ACK, str)
		        		.sendToTarget();
		        	}  else if (str.contains(RESOLUTION_MSG_ACK)) {
		        		mHandler.obtainMessage(MainActivity.RESOLUTION_MSG_ACK, str)
		        		.sendToTarget();
		        	}  
				 
						final String str2=str;
						mHandler.post(new Runnable(){
							@Override
							public void run() {
								Toast.makeText(ctx, "receive: "+str2, Toast.LENGTH_SHORT).show();
							}
						});
						// myHandler.obtainMessage(0, str).sendToTarget();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	private void initSocket() {
		new Thread() {
			public void run() {
				try {
					mClientSocket = new Socket(remoteIp, remotePort);
					mOutputStream = mClientSocket.getOutputStream();
				} catch (UnknownHostException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static Messenger getInstance(String remoteIp, int remotePort,
			Context context, Handler handler) {
		if (instance == null)
			{instance = new Messenger(remoteIp, remotePort, context, handler);
			  Log.i(TAG,"Messenger is null");
			  return instance;
			}		
		else{
			instance.closeConnection();
			instance = null;
			instance = new Messenger(remoteIp, remotePort, context, handler);
			  Log.i(TAG,"Messenger is  not null");
		}
		return instance;
	}

	public void sendMessage(final String message) {

		new Thread(new Runnable() {
			@Override
			public void run() {				
					try {
						mClientSocket = new Socket(remoteIp,remotePort);
						mOutputStream = mClientSocket.getOutputStream();
						PrintWriter out = new PrintWriter(mOutputStream);
						out.println(message);
						// out.println(JsonUtil.obj2Str(msg));
						out.flush();
						//sendMessage(message);
						Log.i(TAG, "conncetion is down, retry send message: "+message);
						mHandler.post(new Runnable(){
							@Override
							public void run() {
								Toast.makeText(ctx, "send: "+message, Toast.LENGTH_SHORT).show();
							}
						});
						mClientSocket.close();
						//Log.i("TAG","mClientSocket"+mClientSocket);
					} catch (IOException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}
			}
		}).start();
	}

	private Messenger(String remoteIp, int remotePort, Context ctx,
			Handler handler) {
		super();
		this.remoteIp = remoteIp;
		this.remotePort = remotePort;
		this.ctx = ctx;
		mHandler = handler;
		shouldRunning = true;
		//this.initSocket();
		this.initServerSocket();
		
	}
	
	public void closeConnection() {
		shouldRunning = false;
		try {
			if (mClientSocket != null) {
				//synchronized (mClientSocket) {
					mClientSocket.close();
				//}
				mClientSocket = null;
			}
			serverThread.interrupt();
			if (mServerSocket != null) {
				mServerSocket.close();
				mServerSocket = null;
			}
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		instance = null;
		Log.i(TAG, "messenger has been closed!");
	}
}
