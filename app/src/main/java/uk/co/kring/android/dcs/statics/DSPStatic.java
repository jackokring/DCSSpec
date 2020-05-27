package uk.co.kring.android.dcs.statics;

import static java.lang.Math.PI;

public class DSPStatic {
    /* 2P
           Ghigh * s^2 + Gband * s + Glow
    H(s) = ------------------------------
                 s^2 + k * s + 1
     */
    public class TwoPole {
        float fs, f, t, u, k, tf, bl, bb, lb, i;

        public TwoPole(float sampleRate) {
            fs = sampleRate;
        }

        public void setFK(float fc, float ks, float inv, float lowBand) {
            f   = (float)Math.tan(PI * fc / fs);
            t   = 1 / (1 + k * f);
            u   = 1 / (1 + t * f * f);
            tf  = t * f;
            k = ks;
            lb = lowBand;
            i = inv;
        }

        public void process(float[] samples) {
            for(int i = 0; i < samples.length; ++i) {
                float low = (bl + tf * (bb + f * samples[i])) * u;
                float band = (bb + f * (samples[i] - low)) * t;
                float high = samples[i] - low - k * band;
                bb = band + f * high;
                bl = low  + f * band;
                low = band * (1F - lb) + low * lb;
                high = band * (1F - lb) + high * lb;
                samples[i] = low * (1F - i) + (samples[i] - high) * i;//lpf default
            }
        }
    }

    /* 1P H(s) = 1 / (s + fb) */
    public class OnePole {
        float fs, f, f2, k, b;

        public OnePole(float sampleRate) {
            fs = sampleRate;
        }

        public void setFK(float fc, float ks) {//ks feedback not k*s denominator
            f   = (float)Math.tan(PI * fc / fs);
            f2   = 1 / (1 + ks * f);
            k = ks;
        }

        public void process(float[] samples) {
            for(int i = 0; i < samples.length; ++i) {
                float out = (f * samples[i] + b) * f2;
                b = f * (samples[i] - k * out) + out;
                samples[i] = out;//lpf default
            }
        }
    }
}
