package com.xiong.wlanconmmunition.activity;

import com.xiong.wlanconmmunition.R;
import com.xiong.wlanconmmunition.XLog;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioSource;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class VoiceActivity  extends Activity implements OnClickListener{
	private Button voiceOpt;
	
	private static int mFrequence = 8000;
	private static int mChannelConfig = AudioFormat.CHANNEL_IN_STEREO;
	private static int mAudioEncoding  = AudioFormat.ENCODING_PCM_16BIT;
	
	private boolean isRecording = false;

	private AudioRecordTask task;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //隐藏标题栏 隐藏状态栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int flag=WindowManager.LayoutParams.FLAG_FULLSCREEN;
         getWindow() .setFlags(flag, flag);
         
        setContentView(R.layout.voice_layout);
        
        voiceOpt = (Button) findViewById(R.id.voiceOpt);
        voiceOpt.setOnClickListener(this);
        
        
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.voiceOpt:
			if(!isRecording){
				task  = new AudioRecordTask();
				task.execute();
			}
			isRecording = !isRecording;
			break;

		default:
			break;
		}
	}
	
	class AudioRecordTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			int bytesize = AudioRecord.getMinBufferSize(mFrequence, mChannelConfig, mAudioEncoding);
			XLog.logd("bytesize:"+ bytesize);
			AudioRecord record = new AudioRecord(AudioSource.MIC, mFrequence, mChannelConfig, mAudioEncoding, bytesize);
			AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, mFrequence, mChannelConfig, mAudioEncoding, bytesize, AudioTrack.MODE_STREAM);
			short[] buffer = new short[bytesize];
			record.startRecording();
			track.play();
			while(isRecording){
				int readLength = record.read(buffer, 0, buffer.length);
				track.write(buffer, 0, readLength);
			}
			
			record.stop();
			track.stop();
			
			return null;
		}
		
	}

}
