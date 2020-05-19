package uk.co.kring.android.dcs;

import android.util.Base64;

public class CodeStatic {

    static CodeStatic p;

    public static CodeStatic getInstance() {
        if(p != null) return p;
        return new CodeStatic();
    }

    int codes[] = new int[1024];
    byte shifts[] = new byte[1024];//shift future for sync
    int primaries[];

    static final char baud10 = 1343;//134.3Hz

    //character translations
    static final String letters =
        "@ABCDEFGHIJKLM" +
        "NOPQRSTUVWXYZ*#" +
        "\n'()+,-./\\" +
        "01234567789" +
        ":;<=>?" +
        " !\"£$%^&\u020a";

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

    static final char RED_PLUS = 512;//control code offset 512 in 0 based @
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
    static final char SYNC_OCTAL = 0606;//special to index no char in code
    static final char REPEAT = 523;
    static final char REPEAT_ACK = 524;
    static final char REPEAT_ACK_ERR = 525;
    static final char RATE_INC = 526;
    static final char RATE_DEC = 527;
    static final char RATE_ACCEPT = 528;
    static final char UN_SYNC = 529;
    static final char UN_SYNC_OCTAL = 0662;//special to index code group
    static final char CALL_SIGN = 530;

    static final char coctals[] = {
        0205, 0223, 0226, 0243, 0244, 0245, 0251, 0261, 0263, 0265, 0271,
        0606, 0612, 0624, 0627, 0631, 0632, 0654, 0662, 0664, 0306 //and LF on end
    };

    static final String controlStr[] = {
        "R+", "R-", "G+", "G-", "B+", "B-", "UP", "DWN", "LFT", "RGT", "DEL",
        "SYN", "RPT", "RAK", "RAE", "RRI", "RRD", "OK", "UN", "CQ", "LF"
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
    static final int BITS_10 = 1023;
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
            codes[BITS_10 - i] = (~codes[i]) & BITS_23;//23 BIT INVERSE
        }
        int c = 0;//BASE GROUP
        for(int i = 0; i < BITS_10 + 1; ++i) {
            if(codes[i] >> 23 != 0) {
                continue;//already assigned group
            }
            int s = codes[i];//current code
            codes[i] += ((++c) << 23);//assign new group
            shifts[i] = 0;
            for(int j = 0; j < 22; ++j) {
                s = ((s << 1) + (s >> 22)) & BITS_23;//rotate
                int idx = s & BITS_10;
                if(codes[idx] >> 23 != 0) {
                    continue;//already assigned group
                }
                //check same code
                if((codes[idx] & BITS_23) != s) {
                    continue;//is not same code
                }
                codes[idx] += (c << 23);//assign same group
                shifts[idx] = (byte)(j + 1);//number of bits to rotate code right
            }
        }
        for(int i = 0; i < BITS_10 + 1; ++i) {
            codes[i] -= BITS_23 + 1;//map code down one
        }
        primaries = new int[c];
        c = 0;
        for(int i = 0; i < BITS_10 + 1; ++i) {
            if(codes[i] >> 23 == c) {
                primaries[c] = i;//look up
                boolean f = false;
                if(i >= 512) {
                    for(int j = 0; j < 512; ++j) {
                        if(codes[j] >> 23 == c) {
                            f = true;//inverse
                            break;
                        }
                    }
                } else {
                    for(int j = 0; j < 512; ++j) {
                        if(codes[j + 512] >> 23 == c) {
                            f = true;//inverse
                            break;
                        }
                    }
                }
                if(!f) codes[i] += BAD_MASK;//code no inverse
                c++;
            }
        }
    }

    public int RXPrimary(int code) {//code to group
        int i = RXPrimary(code, 1);
        if(i >= 0) return i;
        i = RXPrimary(code, 2);
        if(i >= 0) return i;
        i = RXPrimary(code, 3);
        if(i >= 0) return i;
        return codes[UN_SYNC_OCTAL] >> 23;//un sync code
    }

    public int RXPrimary(int code, int nest) {//code to group
        int rec = codes[code & BITS_10];
        int idx = rec >> 23;
        if((rec & BITS_23) != code) {
            if(nest < 0) {
                idx = -1;//error
            } else {
                for(int i = 0; i < 23; ++i) {
                    code ^= (1 << i);//bit flip error brute
                    int j = RXPrimary(code, nest -1);
                    if(j > idx) {
                        idx = j;
                        break;//>= 1
                    }
                }
            }
        } else {
            if(idx < 0) return codes[UN_SYNC_OCTAL] >> 23;//un sync code
        }
        return idx;//group index
    }

    public String humanStringOfRX(int code) {//map for error correct
        int p = RXPrimary(code);
        return humanString(p);
    }

    public String humanString(int code) {//lookup only
        for(int i = 0; i < coctals.length; ++i) {
            if(codes[coctals[i]] >> 23 == code) return controlStr[i];
        }
        for(int i = 0; i < octals.length; ++i) {
            if(codes[octals[i]] >> 23 == code) return letters.substring(i, i + 1);
        }
        return "[UN]";//as is an error in current spec
    }

    public String alternates(int code) {//from group
        int c = primaries[code];
        c = codes[c] >> 23;//group point
        String s = "";
        for(int i = 0; i < codes.length; ++i) {
            if(codes[i] >> 23 == c) {
                s += signed(i) + " ";
            }
        }
        return s;
    }

    public String signed(int code) {//ones complement octal map
        if(code > 511) return "-" + signed(~code);
        code = (code & 7) + ((code >> 3) & 7) * 10 + ((code >> 6) & 7) * 100;//octal easy
        String s = "000" + String.valueOf(code);
        return s.substring(s.length() - 3);//3 digit
    }

    public char RXChar(int code) {//code to @=0 char notation + 512 for controls
        int p = RXPrimary(code);
        for(int i = 0; i < octals.length; ++i) {
            if(codes[octals[i]] >> 23 == p) return letters.charAt(i);
        }
        for(int i = 0; i < coctals.length; ++i) {
            if(codes[coctals[i]] >> 23 == p) return (char)(512 + i);
        }
        return UN_SYNC;//as is an error in spec
    }

    public int TXChar(char c) {//inverse of RXChar
        int p = letters.indexOf(c);
        if(p < 0 || p >= letters.length() - 1) {
            //not a char but a control
            c -= 512;
            if(c < 0 || c >= coctals.length - 1) {
                return codes[SYNC_OCTAL] & BITS_23;//as not transmittable
            }
            return codes[coctals[c]] & BITS_23;
        } else {
            return codes[octals[p]] & BITS_23;
        }
    }

    public int[] TXBlock(byte[] bytes) {//UTF-8 to codes
        bytes = base64Encode(bytes);
        int out[] = new int[bytes.length];
        for(int i = 0; i < out.length; ++i) {
            out[i] = TXChar(letters.charAt(bytes[i]));
        }
        return out;
    }

    public byte[] RXBlock(int[] code) {//codes to UTF-8
        byte out[] = new byte[code.length];
        for(int i = 0; i < out.length; ++i) {
            out[i] = (byte)letters.indexOf(RXChar(code[i]));
        }
        return base64Decode(out);
    }
}
