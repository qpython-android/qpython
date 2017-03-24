/*
 * Audio microphone thread.
 */

package org.audiostream;

import java.lang.Thread;
import android.os.Process;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.AudioEncoder;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;
import java.nio.ByteBuffer;

class AudioIn extends Thread {
	private static String TAG = "AudioIn";
	private boolean stopped = false;
	static AudioIn instance = null;

	int rate;
	int channel;
	int encoding;
	int bufsize;
	int source;

	public AudioIn(int source, int bufsize, int rate, int channel, int encoding) {
		this.source = source;
		this.rate = rate;
		this.channel = channel;
		this.encoding = encoding;
		this.bufsize = bufsize;
	}

	static public boolean check_configuration(int bufsize, int rate, int channel, int encoding) {
		int minbufsize = AudioRecord.getMinBufferSize(rate, channel, encoding);
		Log.d(TAG, String.format("check_configuration() min buffer size is %d", minbufsize));
		if ( minbufsize < 0 )
			return false;
		if ( bufsize > 0 && bufsize < minbufsize )
			return false;
		return true;
	}

	@Override
	public void run() {
		AudioRecord recorder = null;
		ByteBuffer buffer = null;
		int N = 0;

		Log.d(TAG, "Starting audio recording thread");

		Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

		try {
			// ... initialize
			int bufsize = AudioRecord.getMinBufferSize(this.rate, this.channel, this.encoding);
			if ( this.bufsize <= bufsize )
				this.bufsize = bufsize;
			Log.d(TAG, String.format("Audio bufsize is %d bytes", bufsize));

			buffer = ByteBuffer.allocateDirect(bufsize);
			recorder = new AudioRecord(this.source,
					this.rate, this.channel, this.encoding, this.bufsize);

			Log.d(TAG, "Recording started");
			recorder.startRecording();

			// ... loop

			while (!stopped) {
				N = recorder.read(buffer, bufsize);
				nativeAudioCallback(buffer, N);
			}

		} catch(Throwable x) {
			Log.w(TAG, "Error reading voice audio", x);
		} finally {
			if ( recorder != null )
				recorder.stop();
			recorder = null;
		}
	}

	public void close() {
		stopped = true;
	}

	static public void start_recording(int source, int bufsize, int rate, int channel, int encoding) {
		instance = new AudioIn(source, bufsize, rate, channel, encoding);
		instance.start();
	}

	static public void stop_recording() {
		if ( instance != null ) {
			instance.close();
			instance = null;
		}
	}

	public native void nativeAudioCallback(ByteBuffer buffer, int bufsize);

}
