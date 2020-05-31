package uk.co.kring.android.dcs;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.*;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import uk.co.kring.android.dcs.room.AppDatabase;
import uk.co.kring.android.dcs.statics.CodeStatic;

public class AudioService extends Service {

    NotificationManagerCompat nm;
    CodeStatic dcs = CodeStatic.getInstance();
    AppDatabase db;
    boolean recordPermission;
    boolean isRecording = false;
    boolean isPlaying = false;
    Thread recordingThread, playingThread;
    int sampleRateIn, sampleRateOut;
    AudioTrack audioOut;
    AudioRecord audioIn;

    public AudioService() {
        phoneStateListen();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        recordPermission = intent.getBooleanExtra("record", false);
        db = AppDatabase.getInstance(getApplicationContext());
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void readAudio(AudioRecord ar) {

    }

    public void writeAudio(AudioTrack at) {

    }

    public void getAudioIn() {
        if(audioIn != null) return;
        if(!recordPermission) return;
        AudioRecord ar = new AudioRecord(MediaRecorder.AudioSource.MIC,
                AudioFormat.SAMPLE_RATE_UNSPECIFIED, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                AudioRecord.getMinBufferSize(
                        44100, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT * 2));
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

    public void stopAudioIn() {
        if (audioIn != null) {
            isRecording = false;
            audioIn.stop();
            audioIn.release();
            recordingThread = null;
        }
    }

    public void getAudioOut() {
        if(audioOut != null) return;
        @Deprecated
        AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC,
                AudioFormat.SAMPLE_RATE_UNSPECIFIED, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                AudioTrack.getMinBufferSize(
                        44100, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT * 2),
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
        audioOut = at;;
    }

    public void stopAudioOut() {
        if (audioOut != null) {
            isPlaying = false;
            audioOut.stop();
            audioOut.release();
            playingThread = null;
        }
    }

    public void phoneStateListen() {
        PhoneStateListener psl = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING ||
                        state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    stopAudioIn();
                    stopAudioOut();
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        TelephonyManager telephonyManager =
                (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(psl, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
