package uk.co.kring.android.dcs;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.*;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import uk.co.kring.android.dcs.room.AppDatabase;
import uk.co.kring.android.dcs.statics.CodeStatic;
import uk.co.kring.android.dcs.statics.DSPStatic;
import uk.co.kring.android.dcs.statics.UtilStatic;

public class AudioService extends Service {

    CodeStatic dcs = CodeStatic.getInstance();
    AppDatabase db;
    boolean recordPermission;
    boolean isRecording = false;
    boolean isPlaying = false;
    Thread recordingThread, playingThread;
    int sampleRateIn, sampleRateOut;
    AudioTrack audioOut;
    AudioRecord audioIn;
    boolean setNotify = true;

    //===================== PUBLIC INTERFACE
    public AudioService() {
        phoneStateListen();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if(setNotify) {
            NotificationManagerCompat nm = NotificationManagerCompat.from(this);
            PendingIntent contentIntent = PendingIntent.getActivity(this,
                    0, new Intent(this, DCSListActivity.class),
                    0);
            Notification builder = new NotificationCompat.Builder(this,
                    NotificationChannel.DEFAULT_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_filter)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentIntent(contentIntent)
                    .setContentText(getString(R.string.service_notification))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .build();
            startForeground(1, builder);
            setNotify = false;
        }
        db = AppDatabase.getInstance(getApplicationContext());
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        AudioService getService() {
            return AudioService.this;
        }
    }

    public void setPermission(boolean rp) {
        recordPermission = rp;
    }

    public void stopAudioAll() {
        stopAudioIn();
        stopAudioOut();
    }

    public void setDSPAlg(int i, int[] c) {
        //TODO
    }

    public void setMute(boolean m) {
        //TODO
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopAudioAll();
        super.onDestroy();
    }

    //==================== PACKAGED
    short inBuff[], outBuff[];
    float fBuff[];
    boolean processed = false;
    float gain;

    void readAudio(AudioRecord ar) {
        if(processed) Thread.yield();
        int i = 0;
        while(i < inBuff.length) {
            //error?
            i += ar.read(inBuff, i, inBuff.length - i);//fill buffer
        }
        for(i = 0; i < inBuff.length; ++i) {
            fBuff[i] = gain * (float)inBuff[i];
        }
        //TODO: process immediate
        processed = true;
    }

    void writeAudio(AudioTrack at) {
        if(!processed) Thread.yield();
        int i = 0;
        short val;
        for(i = 0; i < fBuff.length; ++i) {
            val = (short)fBuff[i];//clip?

            outBuff[i] = val;
        }
        processed = false;
        while(i < outBuff.length) {
            //error?
            i += at.write(outBuff, i, outBuff.length - i);
        }
    }

    void getAudioIn() {
        if(audioIn != null) return;
        if(!recordPermission) return;
        String v = UtilStatic.pref(this, "mic_gain",
                "0");
        gain = (float)Math.pow(2F, (float)Integer.valueOf(v));
        AudioRecord ar = new AudioRecord(MediaRecorder.AudioSource.MIC,
                AudioFormat.SAMPLE_RATE_UNSPECIFIED, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                AudioRecord.getMinBufferSize(
                        44100, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT) * 2);
        sampleRateIn = ar.getSampleRate();
        ar.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                while(isRecording)
                    readAudio(ar);
            }
        }, "AudioRecorder");
        recordingThread.start();
        audioIn = ar;
    }

    void stopAudioIn() {
        if (audioIn != null) {
            isRecording = false;
            audioIn.stop();
            audioIn.release();
            recordingThread = null;
        }
    }

    void getAudioOut() {
        if(audioOut != null) return;
        @Deprecated
        AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC,
                AudioFormat.SAMPLE_RATE_UNSPECIFIED, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                AudioTrack.getMinBufferSize(
                        44100, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT) * 2,
                AudioTrack.MODE_STREAM);
        sampleRateOut = at.getSampleRate();
        isPlaying = true;
        playingThread = new Thread(new Runnable() {
            public void run() {
                while(isPlaying)
                    writeAudio(at);
            }
        }, "AudioPlayer");
        playingThread.start();
        at.play();
        audioOut = at;
    }

    void stopAudioOut() {
        if (audioOut != null) {
            isPlaying = false;
            audioOut.stop();
            audioOut.release();
            playingThread = null;
        }
    }

    void phoneStateListen() {
        PhoneStateListener psl = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING ||
                        state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    stopAudioAll();
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        TelephonyManager telephonyManager =
                (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(psl, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
