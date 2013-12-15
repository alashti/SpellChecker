/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.Word_bigram_LM;

import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author epcpu
 */
public class W_master {

    static PrintStream jout = System.out;
    static String path;
    static BufferedReader bfr;
    static Hashtable words = new Hashtable();
    static Hashtable W_Bi_Gram = new Hashtable();
    static Hashtable W_LM = new Hashtable();

    public W_master(String path) throws FileNotFoundException {
        this.path = path;
        bfr = new BufferedReader(new FileReader(new File(path)));
    }
    
    public W_master(){
        
    }

    public Hashtable Manager() throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException {
        String line;
        String[] L_Split;
        W_master mast = new W_master();
//        PTBTokenizer tokenize = new PTBTokenizer(bfr, new CoreLabelTokenFactory(), null);
//        BufferedWriter bfw = new BufferedWriter(new FileWriter(new File("tokenized_corpus.txt")));

        while ((line = bfr.readLine()) != null) {
            L_Split = line.split(" ");
            L_Split = mast.Tokenize(L_Split);
            //line = edu.stanford.nlp.util.StringUtils.join(L_Split);
//            bfw.append(edu.stanford.nlp.util.StringUtils.join(L_Split));
//            bfw.newLine();
//            bfw.flush();
            //L_Split = line.split(" ");
            W_master.Word_freq(L_Split);
            W_master.W_BiW_freq(L_Split);
        }
        W_master.Ret_Rel_words();
        W_master.W_LM_calc();
        return W_LM;
    }
    
    private static void W_LM_calc() {
        Enumeration enume_word = words.keys();
        while (enume_word.hasMoreElements()) {
            Words word = (Words) words.get(enume_word.nextElement());
            LinkedList similars = word.getSimilars();
            //word.setSimilars_freq(word.getFreq());
            for (Iterator iter = similars.iterator(); iter.hasNext();) {
                String temp = (String) iter.next();
                if (words.containsKey(temp)) {
                    Words sim = (Words) words.get(temp);
                    word.setSimilars_freq(sim.getFreq());
                }
            }
        }
        Enumeration enume_bi = W_Bi_Gram.keys();
        while (enume_bi.hasMoreElements()) {
            String bi = (String) enume_bi.nextElement();
            String first = bi.substring(0, bi.indexOf(" "));
            String second = bi.substring(bi.indexOf(" ") + 1, bi.length());
            Words fi_word = (Words) words.get(first);
            Words se_word = (Words) words.get(second);
            LinkedList fi_ll = fi_word.getSimilars();
            LinkedList se_ll = se_word.getSimilars();
            int freq = 0;
            float probab = 0;
            for (Iterator iter1 = fi_ll.iterator(); iter1.hasNext();) {
                String left = (String) iter1.next();
                for (Iterator iter2 = se_ll.iterator(); iter2.hasNext();) {
                    String new_bi = left + " " + (String) iter2.next();
                    if (W_Bi_Gram.containsKey(new_bi)) {
                        freq += (Integer) W_Bi_Gram.get(new_bi);
                    }
                }
            }
            if (freq > 0) {
                probab = (Float)((float)freq / (float)fi_word.getSimilars_freq());
                W_LM.put(bi, probab);
            }
        }
    }

    public String[] Tokenize(String[] L_Split) {
        char quote = '\'', dot = '.', DQ = '\"', And = ',', Semi_colon = ';';
        int L_len = L_Split.length;
        for (int i = 0; i < L_len; i++) {
            String str = L_Split[i];
            int S_len = str.length();
            int index;
            if (str.indexOf(quote) == 0) {
                str = quote + " " + str.substring(1);
                S_len = str.length();
            }
            if (str.lastIndexOf(quote) == S_len - 1) {
                str = str.substring(0, S_len - 1) + " " + quote;
                S_len = str.length();
            }
            if (str.indexOf(DQ) == 0) {
                str = DQ + " " + str.substring(1);
                S_len = str.length();
            }
            if (str.lastIndexOf(DQ) == S_len - 1) {
                str = str.substring(0, S_len - 1) + " " + DQ;
                S_len = str.length();
            }
            try {
                if ((index = str.lastIndexOf(DQ)) < S_len - 1 && index > 0 && str.charAt(index - 1) != ' ') {
                    str = str.substring(0, index) + " " + DQ + str.substring(index + 1, S_len);
                    S_len = str.length();
                }
            } catch (Exception ex) {
                jout.println("sth.");
            }
            if (str.lastIndexOf(dot) == S_len - 1 && i == L_len - 1) {
                str = str.substring(0, S_len - 1) + " " + dot;
                S_len = str.length();
            }
            if (((index = str.indexOf(dot)) < S_len - 1) && str.charAt(index + 1) == ' ' && str.charAt(index - 1) != ' ') {
                str = str.substring(0, index) + " " + dot + str.substring(index + 1, S_len);
                S_len = str.length();
            }
            if (str.indexOf(And) == S_len - 1) {// && str.charAt(S_len - 2) != ' '){
                str = str.substring(0, S_len - 1) + " " + And;
                S_len = str.length();
            }
            if (str.indexOf(Semi_colon) == S_len - 1) {// && str.charAt(S_len - 2) != ' '){//agar space ghabl az har kodam az in alaem vojod dasht dige nabayad
                //space ezafe konim
                str = str.substring(0, S_len - 1) + " " + Semi_colon;
                S_len = str.length();
            }
            L_Split[i] = str;//kalameye pardazesh shode ra jaygozine kalameye ghabli mikonim
        }
        L_Split = edu.stanford.nlp.util.StringUtils.join(L_Split).split(" ");
        return L_Split;
    }

    private static void Word_freq(String[] split) {
        Words word;
        for (int i = 0; i < split.length; i++) {
            if (split[i].equals("")) {
                jout.println("here.");
            }
            if (!words.containsKey(split[i])) {
                word = new Words();
                word.setFreq(1);
                words.put(split[i], word);
            } else {
                word = (Words) words.get(split[i]);
                word.incFreq();
            }
        }
    }

    private static void W_BiW_freq(String[] split) {
        for (int i = 0; i < split.length - 1; i++) {
            String key = split[i] + " " + split[i + 1];
            if (!W_Bi_Gram.containsKey(key)) {
                W_Bi_Gram.put(key, 1);
            } else {
                W_Bi_Gram.put(key, (Integer) W_Bi_Gram.get(key) + 1);
            }
        }
    }

    private static void Ret_Rel_words() throws IOException {
        Wordnet_Works SimWords = new Wordnet_Works();
        Words Wop;
        LinkedList similars;
        Enumeration enume = words.keys();
        while (enume.hasMoreElements()) {
            String word = (String) enume.nextElement();
            Wop = (Words) words.get(word);
            similars = Wop.getSimilars();
            List stems = null;
            try {
                stems = SimWords.stem(word);
            } catch (Exception ex) {
                jout.println(word);
            }
            if (stems.size() > 0) {
                for (Iterator iter = stems.iterator(); iter.hasNext();) {//be ezaye tamame stem haye yek kalame
                    LinkedList temp_syns = SimWords.Synonyms((String) iter.next());
                    for (Iterator iter2 = temp_syns.iterator(); iter2.hasNext();) {//be ezaye tamame synonym haye yek stem
                        String syn = (String) iter2.next();
                        if (!similars.contains(syn)) {
                            similars.add(syn);
                        }
                    }
                }
            } else {
                similars.addAll(SimWords.Synonyms(word));
            }
            for (Iterator iter = stems.iterator(); iter.hasNext();) {
                String next = (String) iter.next();
                if (!similars.contains(next)) {
                    similars.add(next);
                }
            }
            if(!similars.contains(word)){
                similars.add(word);
            }
        }
    }
}
