package uk.co.kring.android.dcs;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.*;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import uk.co.kring.android.dcs.room.AppDatabase;
import uk.co.kring.android.dcs.statics.CodeStatic;
import uk.co.kring.android.dcs.statics.DSPStatic;

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

    //===================== PUBLIC INTERFACE
    public AudioService() {
        phoneStateListen();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //recordPermission = intent.getBooleanExtra("record", false);
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

    public void setGain(int gainMaxScale) {
        gain = DSPStatic.log(gainMaxScale, 1F, 4F);//8dB?
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO
        return super.onStartCommand(intent, flags, startId);
    }

    //==================== PACKAGED
    short inBuff[], outBuff[];
    float fInBuff[], fOutBuff[];
    float gain = 1F;

    void readAudio(AudioRecord ar) {
        int i = 0;
        while(i < inBuff.length) {
            //error?
            i += ar.read(inBuff, i, inBuff.length - i);//fill buffer
        }
        for(i = 0; i < inBuff.length; ++i) {
            fInBuff[i] = gain * (float)inBuff[i];
        }
        //TODO: process immediate
    }

    void writeAudio(AudioTrack at) {
        //TODO: if processed
        int i = 0;
        short val;
        for(i = 0; i < fOutBuff.length; ++i) {
            val = (short)fOutBuff[i];//clip?

            outBuff[i] = val;
        }
        while(i < outBuff.length) {
            //error?
            i += at.write(outBuff, i, outBuff.length - i);
        }
    }

    void getAudioIn() {
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
