# DCSSpec
DCS squelch is a system using a 134.3 baud 23 bit Golay code.
This project is to adapt this to a code for data transmission.
The basic mode has 63 characters and 20 control codes.
A more advanced block mode uses a form of base 64 coding to send UTF-8.

The data rate becomes 5.839 chars per second. Or slower with repeats.
It does however occupy sub-bass and is recoverable from a low bandwidth
signal. 67.15Hz is the maximum frequency needed, so the bandwidth is low
and maybe inserted into guard bands in a spectrum for example, or
sent over a PA system at a venue instead of visual QR codes.

There is enough code space to extend the coding, and all but 2 of the
177 codes maybe used. The current version 1 control codes include
both channel control and optional space and colour locatives.

Any undefined code translates to the code *UN* to indicate non synchronisation.
The 2 unusable codes are indicated by *NOI* as they are not phase
invariant. They can be used to detect signal polarity and hence phase.

## FM Bandwidth
Assuming a regular 15% deviation, in a channel for under 2.5kHz, or 5 DCS channels per VHF channel, the modulation index is very high. This has a very good signal to noise. Along with repeats of selective codes and overall repeat rstes controlling an intrinsic doubling of codes and halving of character rates, long distance on low power should be possible.

## Under Construction



