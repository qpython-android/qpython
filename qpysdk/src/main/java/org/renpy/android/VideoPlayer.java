package org.renpy.android;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.io.FileInputStream;
import java.io.IOException;

public class VideoPlayer implements SurfaceHolder.Callback, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

	MediaPlayer player = null;
	SurfaceView view = null;

	boolean playing = true;


	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.i("VP", "Error is " + what + " " + extra);
		return false;
	}

	public void onCompletion(MediaPlayer mp) {
		Log.i("VP", "Completion.");

		if (playing) {
			stop();
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.i("VP", "Surface created.");

		try {
			player = new MediaPlayer();

			player.setOnErrorListener(this);
			player.setOnCompletionListener(this);

			player.setDisplay(view.getHolder());

			Log.i("VP", "Set display.");

			FileInputStream f = new FileInputStream(realFn);

	        if (length >= 0) {
	            player.setDataSource(f.getFD(), base, length);
	        } else {
	            player.setDataSource(f.getFD());
	        }

			f.close();

	        Log.i("VP", "Set input stream.");

			player.prepare();

			Log.i("VP", "Prepared, duration = " + player.getDuration());

			player.start();

			Log.i("VP", "Started playing");


		} catch (Exception e) {

			Log.e("VP", "exception in surface creation", e);
			stop();
			return;
		}

		synchronized (VideoPlayer.this) {
			VideoPlayer.this.notifyAll();
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	/**
	 * This runs on the UI thread to create the surface.
	 */
	private void startPlaying() {
		Log.i("VP", "startPlaying called");


		view = new SurfaceView(PythonSDLActivity.mActivity);
		view.setZOrderOnTop(true);

		view.getHolder().addCallback(this);

		PythonSDLActivity.mActivity.mFrameLayout.addView(view);

		Log.i("VP", "startPlaying done");
	}

	/**
	 * This runs on the UI thread to remove the view.
	 */
	private void stopPlaying() {
		player.release();
		PythonSDLActivity.mActivity.mFrameLayout.removeView(view);
	}

	public void pause() {
		if (playing) {
			player.pause();
			Log.i("VP", "paused");
		}
	}

	public void unpause() {
		if (playing) {
			player.start();
			Log.i("VP", "unpaused");
		}
	}


	public void stop() {

		playing = false;

		PythonSDLActivity.mActivity.runOnUiThread(new Runnable() {
			public void run() {
				stopPlaying();
			}
		});
	}

	public boolean isPlaying() {
		return this.playing;
	}

	String realFn;
	long base;
	long length;

	/**
	 * Creates a new videoplayer and starts it playing.
	 */
	public VideoPlayer(String realFn, long base, long length) {

		this.realFn = realFn;
		this.base = base;
		this.length = length;

		synchronized (this) {
			PythonSDLActivity.mActivity.runOnUiThread(new Runnable() {
				public void run() {
					startPlaying();
				}
			});

			try {
				wait();
			} catch (InterruptedException e) {

			}
		}

	}

}
