package com.example.controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;





import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.example.utils.DialerUtils;
import com.example.utils.PhoneDialerUtils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	final String TAG = MainActivity.class.getCanonicalName();
	private static final int LOCAL_SEND_PORT = 10001;
	private static final int LOCAL_REV_PORT = 10002;
	private static final int REMOTE_SCANNER_PORT = 10002;
	private static final int REMOTE_MESSENGER_PORT = 10003;
	public static final int DETECT_DEVICE_FOUND = 0;
	public static final int RECV_ACK = 1;
	public static final int CALLED_REQUEST = 2;
	public static final int REPLY_CALLING = 3;
	public static final int REPLY_SUCCESS = 4;
	public static final int REPLY_FAILED = 5;
	public static final int BYE_REPLY = 6;
	public static final int BYE_REQUEST = 7;
	public static final int BYE_ACK = 8;
	public static final int CALLED_REQUEST_ACK = 9;
	public static final int CANCEL_ACK = 10;
	public static final int REQUEST_TIMEOUT = 11;
	public static final int SHUTDOWN=12;
    public static final int FRAME_RATE_MSG_ACK=13;
    public static final int RESOLUTION_MSG_ACK=14;
    public static final int DETECT_DEVICE_RESULT=15;

	Messenger mMessenger;
	Scanner mScanner;
	Handler mHandler;
	String remoteIp;
	MediaPlayer mMediaPlayer = new MediaPlayer();
	Vibrator vibrator;
	String num,idStatus;
	StringBuffer testStr;
	//private static String[] id = {"","","","","","","","","","","","","","","",""};
	private List<HashMap> idList;
	private ListAdapter mAdapter;
	//private static int count;

	private EditText mEtNumber, state,test,editText; 
	private ImageButton dialerShowButton, pick, hang;
	private LinearLayout dialerLayout, dialerExpandLayout;
	// private Button btnSearchButton;
	private ImageView link_status;
	private ImageView setButton;
	//private ProgressBar circle;
	


	private boolean if_link = false;

	public enum myStatus {
		IDLE, CALL, CALLED, CONNECTING, DISCONNECTING, CALLED_DISCONNECTING, CONNECTED, CANCEL;
	}

	myStatus status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		//setProgressBarIndeterminateVisibility(true);
		test=(EditText) findViewById(R.id.test);
		state = (EditText) findViewById(R.id.state);
		setButton= (ImageView) findViewById(R.id.btnSetButton);
		status=myStatus.IDLE;
		Log.i("onCreate",status.toString());
		testStr=new StringBuffer("idle");
	   test.setText(testStr);
		initHandler(); 
		mScanner = new Scanner(LOCAL_SEND_PORT, LOCAL_REV_PORT, REMOTE_SCANNER_PORT,
				mHandler);
		// initSearchButton();
		idList=new ArrayList<HashMap>();
		init();
		scanDevice();
		setButton.setOnClickListener(mOnSetClick);
	}

	private void init() {
		mEtNumber = (EditText) findViewById(R.id.edit_number);
		dialerShowButton = (ImageButton) findViewById(R.id.dialer_show_button);
		dialerLayout = (LinearLayout) findViewById(R.id.dialer_layout);
		dialerExpandLayout = (LinearLayout) findViewById(R.id.dialer_expand_layout);
		pick = (ImageButton) findViewById(R.id.pick);
		hang = (ImageButton) findViewById(R.id.hang);
        editText=(EditText)findViewById(R.id.editText);
       // circle=(ProgressBar)findViewById(R.id.circle);
	
		//circle.setVisibility(View.INVISIBLE);
		pick.setEnabled(false);
		pick.setAlpha(0.5f);
		hang.setEnabled(false);
		hang.setAlpha(0.5f);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_0, "0",
				"+", PhoneDialerUtils.TAG_0, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_1, "1",
				"", PhoneDialerUtils.TAG_1, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_2, "2",
				"ABC", PhoneDialerUtils.TAG_2, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_3, "3",
				"DEF", PhoneDialerUtils.TAG_3, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_4, "4",
				"GHI", PhoneDialerUtils.TAG_4, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_5, "5",
				"JKL", PhoneDialerUtils.TAG_5, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_6, "6",
				"MNO", PhoneDialerUtils.TAG_6, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_7, "7",
				"PQRS", PhoneDialerUtils.TAG_7, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_8, "8",
				"TUV", PhoneDialerUtils.TAG_8, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_9, "9",
				"WXYZ", PhoneDialerUtils.TAG_9, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_star,
				"*", "", PhoneDialerUtils.TAG_STAR, mOnDialerClick);
		PhoneDialerUtils.setDialerTextButton(this, R.id.dialer_button_sharp,
				"#", "", PhoneDialerUtils.TAG_SHARP, mOnDialerClick);

		PhoneDialerUtils.setDialerImageButton(this, R.id.dialer_hide_button,
				R.drawable.keyboard_down, PhoneDialerUtils.TAG_HIDE,
				mOnDialerClick);
		PhoneDialerUtils.setDialerImageButton(this,
				R.id.dialer_call_video_button, R.drawable.visio_call_48,
				PhoneDialerUtils.TAG_VIDEO_CALL, mOnDialerClick);
		PhoneDialerUtils.setDialerImageButton(this, R.id.dialer_delete,
				R.drawable.delete, PhoneDialerUtils.TAG_DELETE, mOnDialerClick);
		PhoneDialerUtils.setDialerImageButton(dialerShowButton,
				PhoneDialerUtils.TAG_SHOW, mOnDialerClick);

		mEtNumber.setFocusable(false);
		mEtNumber.setFocusableInTouchMode(false);
		hang.setOnClickListener(mOnHangClick);
		pick.setOnClickListener(mOnPickClick);
		

	}

	// 为拨号盘按钮添加监听处理
	private final View.OnClickListener mOnDialerClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int tag = Integer.parseInt(v.getTag().toString());
			final String number = mEtNumber.getText().toString();
			if (tag == PhoneDialerUtils.TAG_DELETE) {
				// 删除拨号盘中输入的字符
				final int selStart = mEtNumber.getSelectionStart();
				if (selStart > 0) {
					final StringBuffer sb = new StringBuffer(number);
					sb.delete(selStart - 1, selStart);
					mEtNumber.setText(sb.toString());
					mEtNumber.setSelection(selStart - 1);
				}
			} else if (tag == PhoneDialerUtils.TAG_HIDE) {
				// 控制拨号盘隐藏
				dialerLayout.setVisibility(View.GONE);
				dialerExpandLayout.setVisibility(View.VISIBLE);
			} else if (tag == PhoneDialerUtils.TAG_SHOW) {
				// 控制拨号盘显示
				dialerLayout.setVisibility(View.VISIBLE);
				dialerExpandLayout.setVisibility(View.GONE);
			} else if (tag == PhoneDialerUtils.TAG_VIDEO_CALL) {
				if (status == myStatus.IDLE||status==myStatus.CALL) {
					String message = Messenger.DIAL_CALL_REQUEST + number
							+ "##";
					mMessenger.sendMessage(message);
					hang.setEnabled(true);
					hang.setAlpha(1.0f);
					state.setText("连接中。。。");
					status = myStatus.CALL;
					num = number;
					testStr.append("+call");
					test.setText(testStr);
				}
			} else {
				final String textToAppend = tag == DialerUtils.TAG_STAR ? "*"
						: (tag == DialerUtils.TAG_SHARP ? "#" : Integer
								.toString(tag));
				appendText(textToAppend);
			}
		}
	};

	/**
	 * 为号码输入框追加内容
	 * 
	 * @param textToAppend
	 */
	private void appendText(String textToAppend) {
		final int selStart = mEtNumber.getSelectionStart();
		final StringBuffer sb = new StringBuffer(mEtNumber.getText().toString());
		sb.insert(selStart, textToAppend);
		mEtNumber.setText(sb.toString());
		mEtNumber.setSelection(selStart + 1);
	}
	
	//为设置键编写监听
	private final View.OnClickListener mOnSetClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO 自动生成的方法存根
		    startActivityForResult(new Intent(MainActivity.this,Set.class),0);
		   // setContentView(R.layout.set);
		}	
	};
	protected void onActivityResult(int requsestCode,int resultCode,Intent data){
		final String frameRate=(String)data.getExtras().getString("result");
		final String resolution=(String)data.getExtras().getString("resolution");
		  Toast.makeText(MainActivity.this, frameRate, Toast.LENGTH_LONG).show();
		  Toast.makeText(MainActivity.this, resolution, Toast.LENGTH_LONG).show();
		new Thread(){
		    public void run(){
		    	while(true){
		    		if(mMessenger!=null){
		    			String message1 = Messenger.FRAME_RATE_MSG +frameRate
								+ "##";
						mMessenger.sendMessage(message1);
						String message2 = Messenger.RESOLUTION_MSG +resolution
								+ "##";
						mMessenger.sendMessage(message2);
						break;
		    		}
		    	}
		    }
		}.start();
	}

	private final View.OnClickListener mOnHangClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO 自动生成的方法存根
			switch (status) {
			case CALL:
				String CallMessage = Messenger.DIAL_CALL_REQUEST_CANCEL;
				mMessenger.sendMessage(CallMessage);
				state.setText("取消呼叫 " + num);
				status = myStatus.CANCEL;
				testStr.append("+call");
				test.setText(testStr);
				break;
			case CALLED:
				String CalledMessage = Messenger.DIAL_CALLED_REPLY_DECLINE; // "##CTL_DIAL_CALLED_REPLY_DECLINE##";
				mMessenger.sendMessage(CalledMessage);
				Log.i(TAG, "hangup current state is : " + status);
				Toast.makeText(MainActivity.this, "挂断电话", Toast.LENGTH_LONG)
						.show();
				state.setText("挂断" + num + " 中");
				status = myStatus.CALLED_DISCONNECTING;
				testStr.append("+called_disconnecting");
				test.setText(testStr);
				try {
					mMediaPlayer.stop();
					vibrator.cancel();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case CALLED_DISCONNECTING:
				String CalledDisconnectingMessage = Messenger.DIAL_CALLED_REPLY_DECLINE; // "##CTL_DIAL_CALLED_REPLY_DECLINE##";
				mMessenger.sendMessage(CalledDisconnectingMessage);
				Log.i(TAG, "hangup current state is : " + status);
				Toast.makeText(MainActivity.this, "挂断电话", Toast.LENGTH_LONG)
						.show();
				state.setText("挂断" + num + " 中");
				testStr.append("+called_disconnecting");
				test.setText(testStr);
				break;
			case CONNECTING:
				String ConntingMessage = Messenger.DIAL_CALL_REQUEST_CANCEL;
				mMessenger.sendMessage(ConntingMessage);
				state.setText("取消呼叫 " + num);
				status = myStatus.CANCEL;
				testStr.append("+connecting");
				test.setText(testStr);
				break;
			case CANCEL:
				String CancelMessage = Messenger.DIAL_CALL_REQUEST_CANCEL;
				mMessenger.sendMessage(CancelMessage);
				state.setText("取消呼叫 " + num);
				testStr.append("+connecting");
				test.setText(testStr);
				break;
			case CONNECTED:
				String ConnectedMessage = Messenger.DIAL_BYE_REQUEST; // "##CTL_DIAL_BYE_REQUEST##";
				mMessenger.sendMessage(ConnectedMessage);
				Toast.makeText(MainActivity.this, "挂断电话", Toast.LENGTH_LONG)
						.show();
				state.setText("挂断" + num + " 中");
				status = myStatus.DISCONNECTING;
				testStr.append("+disconnecting");
				test.setText(testStr);
				break;
			case DISCONNECTING:
				String DisconnectedMessage = Messenger.DIAL_BYE_REQUEST; // "##CTL_DIAL_BYE_REQUEST##";
				mMessenger.sendMessage(DisconnectedMessage);
				Toast.makeText(MainActivity.this, "挂断电话", Toast.LENGTH_LONG)
						.show();
				state.setText("挂断" + num + " 中");
				testStr.append("+disconnecting");
				test.setText(testStr);
				break;
				
			default:
				break;
			}
		}
	};

	private final View.OnClickListener mOnPickClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO 自动生成的方法存根
			switch (status) {

			case CALLED:
				String CalledMessage = Messenger.DIAL_CALLED_REPLY_ACCEPT; // "##CTL_DIAL_CALLED_REPLY_ACCEPT##";
				mMessenger.sendMessage(CalledMessage);
				Log.i(TAG, "pick up current state is : " + status);
				Toast.makeText(MainActivity.this, "接听电话", Toast.LENGTH_LONG)
						.show();
				state.setText("接听" + num + " 中");
				status = myStatus.CONNECTING;
				testStr.append("+connecting");
				test.setText(testStr);
				try {
					mMediaPlayer.stop();
					vibrator.cancel();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case CONNECTING:
				String ConnectingMessage = Messenger.DIAL_CALLED_REPLY_ACCEPT; // "##CTL_DIAL_CALLED_REPLY_ACCEPT##";
				mMessenger.sendMessage(ConnectingMessage);
				Log.i(TAG, "pick up current state is : " + status);
				Toast.makeText(MainActivity.this, "接听电话", Toast.LENGTH_LONG)
						.show();
				state.setText("接听" + num + " 中");
				testStr.append("+disconnecting");
				test.setText(testStr);
				break;
			default:
				break;
			}
		}

	};

	private void initHandler() {
		link_status = (ImageView) findViewById(R.id.link_status);
		try {
			Uri alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			mMediaPlayer.setDataSource(MainActivity.this, alert);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
			mMediaPlayer.setLooping(true);
			mMediaPlayer.prepare();
			vibrator = (Vibrator) MainActivity.this
					.getSystemService(Context.VIBRATOR_SERVICE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case DETECT_DEVICE_FOUND:
					//Log.i(TAG,"INFO= "+ msg.obj.toString());
/*					state.setText("发现设备");*/		
					HashMap<String, String> map=null;
					try {
						map = (HashMap<String, String>) msg.obj;
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//int separator=info.indexOf("##");
					//remoteIp =info.substring(0, separator1) ;
					//idStatus=info.substring(0, separator);
					//Log.i(TAG,"第"+count+"个:"+idStatus);
					if(!idList.contains(map))
						idList.add(map);
					//setProgressBarIndeterminateVisibility(true);
				//	circle.setVisibility(View.VISIBLE);
					
					//id[count]=idStatus;
					// count++;
					 
					 
				/*	initMessenger();
					//state.setText("已机顶盒建立连接");
					stopScan();
					// btnSearchButton.setEnabled(false);
					// btnSearchButton.setAlpha(0.5f);
					link_status.setImageResource(R.drawable.network_connect);
					if_link = true;*/
					break;
				case DETECT_DEVICE_RESULT:
				/*	setProgressBarIndeterminateVisibility(false);
					 circle.setVisibility(View.INVISIBLE);*/
					Iterator<HashMap> itor=idList.iterator();
					String []tmp=new String[idList.size()];
					int index=0;
					while(itor.hasNext()){
						HashMap t=itor.next();
						tmp[index++]=(String) t.get("ip")+" "+t.get("id")+" "+t.get("status");
					}
					new AlertDialog.Builder(MainActivity.this).setTitle("选择连接设备")
					.setItems(tmp, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int which)
						{
							final AlertDialog ad = new AlertDialog.Builder(
									MainActivity.this).setMessage(
									"您已经选择了: " + idList.get(which))
									.show();
							android.os.Handler hander = new android.os.Handler();
							// 设置定时器，5秒后调用run方法
							hander.postDelayed(new Runnable()
							{
								@Override
								public void run()
								{
									// 调用AlertDialog类的dismiss方法关闭对话框，也可以调用cancel方法
									ad.dismiss();
								} 
							}, 1 * 1000);
							remoteIp=(String) idList.get(which).get("ip");
							new Thread() {								
								@Override
								public void run() {
									try {
										DatagramSocket udpSendSocket = new DatagramSocket(9999);
										byte[] buffer=new byte[40];
										DatagramPacket pack=new DatagramPacket(buffer,buffer. length);
										byte[] data=Scanner.SCAN_REPLY_ACK.getBytes();
										pack.setData(data);
										pack.setLength(data.length);
										pack.setPort(REMOTE_SCANNER_PORT);
										pack.setAddress(InetAddress.getByName(remoteIp));
										udpSendSocket.send(pack);
										udpSendSocket.close();
										Log.i(TAG, "scan ack has been sent!");
									} catch (SocketException e) {
										// TODO 自动生成的 catch 块
										e.printStackTrace();
									} catch (IOException e) {
										// TODO 自动生成的 catch 块
										e.printStackTrace();
									}catch (Exception e) {
										// TODO: handle exception
										e.printStackTrace();
									}
									
								}
							}.start();
							editText.setText("控制"+(String)idList.get(which).get("id"));							
							initMessenger();
							stopScan();
							String Message0 = Scanner.SCAN_REPLY_ACK;
							mMessenger.sendMessage(Message0);
							link_status.setImageResource(R.drawable.network_connect);
							if_link = true;
							state.setText("发现设备");
						}
					}).show();
					
					break;
				case REPLY_CALLING:
					Log.i(TAG, "REPLY_CALLING");
					Log.i(TAG,status.toString());
					if (status == myStatus.CALL) {		 
						String Message = Messenger.DIAL_CALL_REPLY_CALLING_ACK;
						mMessenger.sendMessage(Message);
						num = mEtNumber.getText().toString();
						state.setText("呼叫" + num + " 中");
						status = myStatus.CONNECTING;
						hang.setEnabled(true);
						hang.setAlpha(1.0f);
						testStr.append("+connecting");
						test.setText(testStr);
					}
					break;
				case REPLY_SUCCESS:
					Log.i(TAG, "REPLY_SUCCESS");
					if (status == myStatus.CONNECTING) {
						String message1 = Messenger.DIAL_CALL_REQUEST_ACK; // "##CTL_DIAL_CALL_REQUEST_ACK##"
						mMessenger.sendMessage(message1);
						state.setText("与"+num+"通话中。。。");
						status = myStatus.CONNECTED;
						hang.setEnabled(true);
						hang.setAlpha(1.0f);
						testStr.append("+connected");
						test.setText(testStr);
					}
					break;
				case REPLY_FAILED:
					Log.i(TAG, "REPLY_FAILED");
					if (status == myStatus.CONNECTING) {
						String message2 = Messenger.DIAL_CALL_REQUEST_ACK; // "##CTL_DIAL_CALL_REQUEST_ACK##"
						mMessenger.sendMessage(message2);
						hang.setEnabled(false);
						hang.setAlpha(0.5f);
						state.setText("对方挂断,通话结束");
						status = myStatus.IDLE;
						testStr.append("+idle");
						test.setText(testStr);
					}
					break;
				case BYE_REPLY:
					Log.i(TAG, "BYE_REPLY");
					if (status == myStatus.DISCONNECTING) {
						String message3 = Messenger.DIAL_BYE_ACK; // "##CTL_DIAL_BYE_ACK##"
						mMessenger.sendMessage(message3);
						hang.setEnabled(false);
						hang.setAlpha(0.5f);
						state.setText("主动挂断,通话结束");
						status = myStatus.IDLE;
						testStr.append("+idle");
						test.setText(testStr);
					}
					break;
				case BYE_REQUEST:
					Log.i(TAG, "BYE_REQUEST");
					if (status == myStatus.CONNECTED) {
						String message4 = Messenger.DIAL_BYE_REPLY; // "##CTL_DIAL_BYE_REPLY##";
						mMessenger.sendMessage(message4);
						hang.setEnabled(false);
						hang.setAlpha(0.5f);
						state.setText("对方挂断。。。");
						status = myStatus.DISCONNECTING;
						testStr.append("+disconnecting");
						test.setText(testStr);
					}
					break;
				case BYE_ACK:
					Log.i(TAG, "BYE_ACK");
					if (status == myStatus.DISCONNECTING) {
						state.setText("对方挂断，通话结束");
						status = myStatus.IDLE;
						hang.setEnabled(false);
						hang.setAlpha(0.5f);
						testStr.append("+idle");
						test.setText(testStr);
					}
					break;
				case CALLED_REQUEST:
					Log.i(TAG, "CALLED_REQUEST");
					if (status == myStatus.IDLE) {
						String message5 = Messenger.DIAL_CALLED_REPLY_PENDING; // "##CTL_DIAL_CALLED_REPLY_PENDING##";
						mMessenger.sendMessage(message5);
						pick.setEnabled(true);
						pick.setAlpha(1.0f);
						hang.setEnabled(true);
						hang.setAlpha(1.0f);
						// 振铃
						try {
							mMediaPlayer.start();
							long[] pattern = { 800, 150, 400, 130 }; // OFF/ON/OFF/ON...
							vibrator.vibrate(pattern, 2);
						} catch (Exception e) {
							e.printStackTrace();
						}
						String result = msg.obj.toString();
						int position_num_start = result.indexOf("_#") + 2;
						int position_num_end = result.indexOf("##",
								position_num_start);
						final String number = result.substring(
								position_num_start, position_num_end);
						state.setText(number + " 来电");
						num = number;
						status = myStatus.CALLED;
						testStr.append("+called");
						test.setText(testStr);
					}
					break;
				case CALLED_REQUEST_ACK:
					Log.i(TAG, "CALLED_REQUEST_ACK");
					Log.i(TAG, "current state is : " + status);
					if (status == myStatus.CONNECTING) {
						hang.setEnabled(true);
						hang.setAlpha(1.0f);
						pick.setEnabled(false);
						pick.setAlpha(0.5f);
						state.setText("与"+num+"通话中。。。");
						status = myStatus.CONNECTED;
						testStr.append("+connected");
						test.setText(testStr);
					} else if (status == myStatus.CALLED_DISCONNECTING) {
						// 从disconnecting转为disconnceted转为idle
						pick.setEnabled(false);
						pick.setAlpha(0.5f);
						hang.setEnabled(false);
						hang.setAlpha(0.5f);
						state.setText("主动挂断，通话结束");
						status = myStatus.IDLE;
						testStr.append("+idle");
						test.setText(testStr);
					}
					break;
				case CANCEL_ACK:
					Log.i(TAG, "CANCEL_ACK");
					if (status == myStatus.CANCEL) {
						state.setText("呼叫取消,通话结束");
						status = myStatus.IDLE;
						hang.setEnabled(false);
						hang.setAlpha(0.5f);
						testStr.append("+idle");
						test.setText(testStr);
					}
					break;
				case REQUEST_TIMEOUT:
					Log.i(TAG, "REQUEST_TIMEOUT");
					state.setText("对方挂断，通话结束");
					try {
						mMediaPlayer.stop();
						vibrator.cancel();
					} catch (Exception e) {
						e.printStackTrace();
					}
					status = myStatus.IDLE;
					testStr.append("+idle");
					test.setText(testStr);
					String message6 = Messenger.DIAL_CALLED_REQUEST_TIMEOUT_ACK; // "##CTL_DIAL_CALLED_REPLY_PENDING##";
					mMessenger.sendMessage(message6);
					hang.setEnabled(false);
					hang.setAlpha(0.5f);
					pick.setEnabled(false);
					pick.setAlpha(0.5f);
					break;
				case SHUTDOWN:
					status = myStatus.IDLE;
					state.setText("机顶盒下线！");
					link_status.setImageResource(R.drawable.network_disconnect);
					try {
						mMediaPlayer.stop();
						vibrator.cancel();
					} catch (Exception e) {
						e.printStackTrace();
					}
					hang.setEnabled(false);
					hang.setAlpha(0.5f);
					pick.setEnabled(false);
					pick.setAlpha(0.5f);
					
					break;
				case FRAME_RATE_MSG_ACK:
					 Toast.makeText(MainActivity.this, "帧速率设置成功", Toast.LENGTH_LONG);
					break;
				case RESOLUTION_MSG_ACK:
					 Toast.makeText(MainActivity.this, "分辨率设置成功", Toast.LENGTH_LONG);
					break;
				default :
				 break;
				}
			}
		};
	}

	

	private void scanDevice() {
		if (mScanner != null) {
			mScanner.startScan();
			Log.i(TAG, "Start Scanning for device...");
			state.setText("搜索设备中。。。");
			new Thread(){
				public void run(){
					/* try {
						sleep(5000);
					} catch (InterruptedException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}*/
					SystemClock.sleep(8000);
				  
					mHandler.obtainMessage(MainActivity.DETECT_DEVICE_RESULT).sendToTarget();	
					Log.i(TAG,"DETECT_DEVICE_RESULT");
				}
			}.start();
		}
	}

	protected void stopScan() {
		if (mScanner != null) {
			mScanner.stopScan();
			Log.i(TAG, "Stop Scanning for device...");
		}
	}	
	private void initMessenger() {
		mMessenger = Messenger.getInstance(remoteIp,REMOTE_MESSENGER_PORT, this,mHandler);
		Log.i(TAG, remoteIp);
		Log.i(TAG, "mMessenger初始化成功");
	}
	public void onBackPressed(){
		super.onBackPressed();
		int pid = android.os.Process.myPid(); //获得自己的pid
	    android.os.Process.killProcess(pid);//通过pid自杀    
	}
	@Override
	protected void onStop() {
		super.onStop();
		}
	@Override
	protected void onDestroy(){
		super.onDestroy();
		if (if_link == false)
			stopScan();
	}
	
	

}
