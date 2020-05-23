package uk.co.kring.android.dcs.statics;

public class DSPStatic {
    /* 2P
           Ghigh * s^2 + Gband * s + Glow
    H(s) = ------------------------------
                 s^2 + k * s + 1
    f   = tan(PI * fc / fs)
    t   = 1 / (1 + k * f)
    u   = 1 / (1 + t * f * f)
    tf  = t * f
    bl  -> second integrator buffer
    bh  -> first integrator buffer

    low     = (bl + tf * (bb + f * in)) * u
    band    = (bb + f * (in - low)) * t
    high    = in - low - k * band
    bb      = band + f * high
    bl      = low  + f * band
     */

    /* 1P
    H(s) = 1 / (s + fb)
    f2   = 1 / (1 + f * fb) ... fb is feedback gain
    out  = (f * in + buf) * f2
    buf  =  f * (in - fb * out) + out
     */
}
