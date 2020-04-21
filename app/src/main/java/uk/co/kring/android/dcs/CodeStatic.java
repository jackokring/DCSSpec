package uk.co.kring.android.dcs;

public class CodeStatic {

    static CodeStatic p;

    public static CodeStatic getInstance() {
        if(p != null) return p;
        return new CodeStatic();
    }

    int codes[] = new int[1024];
    int primaries[];

    //character translations
    static final char letters[] = {
        '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '*', '#',
        '\n', '\'', '(', ')', '+', ',', '-', '.', '/', '\\',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        ':', ';', '<', '=', '>', '?',
        ' ', '!', '"', '£', '$', '%', '^', '&'
    };

    static final char octals[] = {
        023, 025, 026, 031, 032, 043, 047, 051, 054, 065, 071, 072, 073, 074,
        0114, 0115, 0116, 0125, 0131, 0132, 0134, 0143, 0152, 0155, 0156, 0162, 0165, 0172, 0174,
        0306, 0311, 0315, 0331, 0343, 0346, 0351, 0364, 0365, 0371,
        0411, 0412, 0413, 0423, 0431, 0432, 0445, 0464, 0465, 0466,
        0503, 0506, 0516, 0532, 0546, 0565,
        0703, 0712, 0723, 0731, 0732, 0734, 0743, 0754
    };

    //static final int controls[] = {
        //r+, r-, g+, g-, b+, b-, up, dn, le, ri, dl,
        //sy, rp, ra, re, ri, rd, ok, un, cq
    //};

    static final char coctals[] = {
        0205, 0223, 0226, 0243, 0244, 0245, 0251, 0261, 0263, 0265, 0271,
        0606, 0612, 0624, 0627, 0631, 0632, 0654, 0662, 0664
    };

    public CodeStatic() {
        genCodes();
    }

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
            codes[i + 512] = (~codes[i]) & 0x8FFFFF;//23 BIT INVERSE
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
                if(codes[s & 1023] != s) {
                    continue;//is not same code
                }
                codes[i] += (c << 23);//assign same group
            }
        }
        primaries = new int[c];
        c = 1;
        for(int i = 0; i < 1024; ++i) {
            if(codes[i] >> 23 == c) {
                primaries[c - 1] = i;//look up
                c++;
            }
        }
    }
}
