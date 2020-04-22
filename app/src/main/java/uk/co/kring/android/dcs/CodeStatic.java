package uk.co.kring.android.dcs;

import android.util.Base64;

public class CodeStatic {

    static CodeStatic p;

    public static CodeStatic getInstance() {
        if(p != null) return p;
        return new CodeStatic();
    }

    int codes[] = new int[1024];
    int primaries[];

    static final char baud10 = 1343;//134.3Hz

    //character translations
    static final String letters =
        "@ABCDEFGHIJKLM" +
        "NOPQRSTUVWXYZ*#" +
        "\n'()+,-./\\" +
        "01234567789" +
        ":;<=>?" +
        " !\"£$%^&";

    static final char octals[] = {
        023, 025, 026, 031, 032, 043, 047, 051, 054, 065, 071, 072, 073, 074,
        0114, 0115, 0116, 0125, 0131, 0132, 0134, 0143, 0152, 0155, 0156, 0162, 0165, 0172, 0174,
        0306, 0311, 0315, 0331, 0343, 0346, 0351, 0364, 0365, 0371,
        0411, 0412, 0413, 0423, 0431, 0432, 0445, 0464, 0465, 0466,
        0503, 0506, 0516, 0532, 0546, 0565,
        0703, 0712, 0723, 0731, 0732, 0734, 0743, 0754,//letters end here
            0606 //added for base 64 coding as block does not need synchronous idle
    };

    //static final int controls[] = {
        //r+, r-, g+, g-, b+, b-, up, dn, le, ri, dl,
        //sy, rp, ra, re, ri, rd, ok, un, cq
    //};

    static final char RED_PLUS = 512;
    static final char RED_MINUS = 513;
    static final char GREEN_PLUS = 514;
    static final char GREEN_MINUS = 515;
    static final char BLUE_PLUS = 516;
    static final char BLUE_MINUS = 517;

    static final char UP = 518;
    static final char DOWN = 519;
    static final char LEFT = 520;
    static final char RIGHT = 521;

    static final char SYNC_IDLE = 522;
    static final char REPEAT = 523;
    static final char REPEAT_ACK = 524;
    static final char REPEAT_ACK_ERR = 525;
    static final char RATE_INC = 526;
    static final char RATE_DEC = 527;
    static final char RATE_ACCEPT = 528;
    static final char UN_SYNC = 529;
    static final char CALL_SIGN = 530;

    static final char coctals[] = {
        0205, 0223, 0226, 0243, 0244, 0245, 0251, 0261, 0263, 0265, 0271,
        0606, 0612, 0624, 0627, 0631, 0632, 0654, 0662, 0664
    };

    static final String base64Index =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    public CodeStatic() {
        genCodes();
    }

    public static byte[] base64Encode(byte[] input) {
        input = Base64.encode(input, Base64.NO_PADDING | Base64.NO_WRAP);
        for(int i = 0; i < input.length; ++i) {
            input[i] = (byte)base64Index.indexOf(input[i]);
        }
        return input;
    }

    public static byte[] base64Decode(byte[] input) {//from indexes in octals
        for(int i = 0; i < input.length; ++i) {
            input[i] = (byte)base64Index.charAt(input[i]);
        }
        return Base64.decode(input, Base64.NO_PADDING | Base64.NO_WRAP);
    }

    static final int BITS_23 = 0x7FFFFF;
    static final int BAD_MASK = 0x80000000;

    //CODE GENERATION
    void genCodes() {
        for(int i = 0; i < 512; ++i) {
            codes[i] = i + (4 << 9);//base code
            boolean C1 = (i & 1) != 0;
            boolean C2 = (i & 2) != 0;
            boolean C3 = (i & 4) != 0;
            boolean C4 = (i & 8) != 0;
            boolean C5 = (i & 16) != 0;
            boolean C6 = (i & 32) != 0;
            boolean C7 = (i & 64) != 0;
            boolean C8 = (i & 128) != 0;
            boolean C9 = (i & 256) != 0;
            boolean P[] = new boolean[11];
            P[0] = C1 ^ C2 ^ C3 ^ C4 ^ C5 ^ C8;
            P[1] = !(C2 ^ C3 ^ C4 ^ C5 ^ C6 ^ C9);
            P[2] = C1 ^ C2 ^ C6 ^ C7 ^ C8;
            P[3] = !(C2 ^ C3 ^ C7 ^ C8 ^ C9);
            P[4] = !(C1 ^ C2 ^ C5 ^ C9);
            P[5] = !(C1 ^ C4 ^ C5 ^ C6 ^ C8);
            P[6] = C1 ^ C3 ^ C4 ^ C6 ^ C7 ^ C8 ^ C9;
            P[7] = C2 ^ C4 ^ C5 ^ C7 ^ C8 ^ C9;
            P[8] = C3 ^ C5 ^ C6 ^ C8 ^ C9;
            P[9] = !(C4 ^ C6 ^ C7 ^ C9);
            P[10] = !(C1 ^ C2 ^ C3 ^ C4 ^ C7);
            int c = 0;
            for(int j = 0; j < 11; ++j) {
                if(P[j]) c+= (1 << j);
            }
            c <<= 12;//SHIFT
            codes[i] += c;
            codes[1023 - i] = (~codes[i]) & BITS_23;//23 BIT INVERSE
        }
        int c = 0;//BASE GROUP
        for(int i = 0; i < 1024; ++i) {
            if(codes[i] >> 23 != 0) {
                continue;//already assigned group
            }
            int s = codes[i];//current code
            codes[i] += ((++c) << 23);//assign new group
            for(int j = 0; j < 22; ++j) {
                s = (s << 1) + (s >> 22);//rotate
                if(codes[s & 1023] >> 23 != 0) {
                    continue;//already assigned group
                }
                //check same code
                if((codes[s & 1023] & BITS_23) != s) {
                    continue;//is not same code
                }
                codes[i] += (c << 23);//assign same group
            }
        }
        for(int i = 0; i < 1024; ++i) {
            codes[i] -= BITS_23 + 1;//map code down one
        }
        primaries = new int[c];
        c = 0;
        for(int i = 0; i < 1024; ++i) {
            if(codes[i] >> 23 == c) {
                primaries[c] = i;//look up
                boolean f = false;
                if(i >= 512) {
                    for(int j = 0; j < 512; ++j) {
                        if(codes[j] >> 23 == c) {
                            f |= true;//inverse
                            break;
                        }
                    }
                } else {
                    for(int j = 0; j < 512; ++j) {
                        if(codes[j + 512] >> 23 == c) {
                            f |= true;//inverse
                            break;
                        }
                    }
                }
                if(!f) codes[i] += BAD_MASK;//code no inverse
                c++;
            }
        }
    }
}
