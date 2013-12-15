/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.Tag_bigram_LM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author epcpu
 */
public class T_master {

    BufferedReader bfr;
    static PrintStream jout = System.out;
    static Hashtable tags = new Hashtable();
    static Hashtable T_Bi_Gram = new Hashtable();
    static Hashtable T_LM = new Hashtable();
    static int len = 0;

    public T_master(String path) throws FileNotFoundException {
        bfr = new BufferedReader(new FileReader(new File(path)));
    }

    public Hashtable Manager() throws IOException {
        String str;
        String[] Split;
        while ((str = bfr.readLine()) != null) {
            Split = str.split(" ");
            len = Split.length;
            T_master.Tag_freq(Split);
            T_master.T_Bi_freq(Split);
            T_master.T_LM_calc();
        }
        return T_LM;
    }

    private static void T_LM_calc() {
        Enumeration enume = T_Bi_Gram.keys();
        while (enume.hasMoreElements()) {
            String twin = (String) enume.nextElement();
            int tw_freq = (Integer) T_Bi_Gram.get(twin);
            String left = twin.substring(0, twin.indexOf(" "));
            int left_freq = 0;
            try{
                left_freq = (Integer) tags.get(left);
            } catch(Exception ex){
              jout.println("left: " + left);  
            }
            T_LM.put(twin, (float)tw_freq / (float)left_freq);
        }
    }

    private static void Tag_freq(String[] Split) {
        for (int i = 0; i < len; i++) {
            String tag = Split[i].substring(Split[i].indexOf("_") + 1, Split[i].length());
            if (!tags.containsKey(tag)) {
                tags.put(tag, 1);
            } else {
                tags.put(tag, (Integer) tags.get(tag) + 1);
            }
        }
    }

    private static void T_Bi_freq(String[] Split) {
        for (int i = 0; i < len - 1; i++) {
            String t_left = Split[i].substring(Split[i].indexOf("_") + 1, Split[i].length());
            String t_right = Split[i + 1].substring(Split[i + 1].indexOf("_") + 1, Split[i + 1].length());
            String twin = t_left + " " + t_right;
            if (!T_Bi_Gram.containsKey(twin)) {
                T_Bi_Gram.put(twin, 1);
            } else {
                T_Bi_Gram.put(twin, T_Bi_Gram.get(twin));
            }
        }
    }
}
