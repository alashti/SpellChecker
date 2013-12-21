/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.SpellChecking;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Pattern;
import javax.swing.text.html.HTMLDocument;
import spellchecker.SpellChecker;
import tools.Word_bigram_LM.W_master;

/**
 *
 * @author epcpu
 */
public class checker {

    //manages all jobs of this class
    static PrintStream jout = System.out;
    static BufferedReader bfr;
    static BufferedReader bfr_test;
    static MaxentTagger tagger;
    static ArrayList lexicon = new ArrayList();
    static Hashtable size_div = new Hashtable();
    static int Max_dif = 1;
    static String original_word;
    static int stack;
    static ArrayList chosen = new ArrayList();
    static int dif;

    public checker(String Dic_path, String test_set, String tagger_training_set) throws FileNotFoundException, IOException, ClassNotFoundException {
        bfr = new BufferedReader(new FileReader(new File(Dic_path)));
        bfr_test = new BufferedReader(new FileReader(new File(test_set)));
        tagger = new MaxentTagger(tagger_training_set);
    }

    public void manager() throws IOException {
        checker.Dic_Loader();
//        jout.println("dictionary loading has been finished, ensha allah.");
        String[] Split;
//        ArrayList chosen = new ArrayList();
        String str;
        W_master wmaster = new W_master();
        while ((str = bfr_test.readLine()) != null) {
            str = str.trim();
            Split = wmaster.Tokenize_eng(str.split(" "));
            int len = Split.length;
            for (int i = 0; i < len; i++) {
//                if (Split[i].equals("acompanied")) {
//                    jout.println("acompanied");
//                }
                if (lexicon.contains(Split[i]) || lexicon.contains(Split[i].toLowerCase()) || lexicon.contains(Split[i].toUpperCase())) {
                    jout.println("may be real word.");
                } else if (pattern(Split[i])) {
                    jout.println("it's pattern has been detected. " + Split[i]);
                } else {
                    jout.println("nonword: " + Split[i]);
//                    if (i == 0) {
//                    if (Split[i].equals("discusion")) {
//                        jout.println(Split[i]);
//                    }
                    checker.Gen_Word_Candids(Split[i]);//"NA", Split[i], Split[i + 1]);
                    int k = 0;
                    while (k < chosen.size()) {
                        jout.println(chosen.get(k));
                        k++;
                    }
                    jout.println("See chosens.");
//                    } else if (i == len - 1) {
//                        chosen = checker.Gen_NonWord_Candids(Split[i - 1], Split[i], "NA");
//                    } else {
//                        chosen = checker.Gen_NonWord_Candids(Split[i - 1], Split[i], Split[i + 1]);
//                    }
                }
//                jout.println("See chosens.");
                chosen.clear();
            }
            jout.println("***************");
        }
//        jout.println("testing manager finished.");
    }

    //Generates Candidates for a Word by traversing the dictionary tree
    private static void Gen_Word_Candids(String word) {
        int len = word.length();
        original_word = word;
        Char_seperator c_sep, c_temp;
        Size_seperator s_temp;
        for (int i = len - 1; i <= len + 1; i++) {//be ezaye kalamate ba tole yeki kamtar va kalamate hamtol va kalamate ba tole yeki bishtar iterate mikonim
            s_temp = (Size_seperator) size_div.get(i);//object marbote be on tol ra bedast miavarim
            Enumeration enume = s_temp.getCharacter().keys();
            while (enume.hasMoreElements()) {
                char ch_t = (Character) enume.nextElement();
                c_temp = (Char_seperator) s_temp.getCharacter().get(ch_t);
                if (ch_t == 'I') {
                    jout.println(ch_t);
                }
                if (String.valueOf(ch_t).equalsIgnoreCase(String.valueOf(word.charAt(0)))) {
                    traverse(c_temp, word.substring(1), len - 1, 0, false);//substitution
                } else {
                    traverse(c_temp, word.substring(1), len - 1, 1, false);//substitution
                    traverse(c_temp, word, len, 0, true);//deletion
                }
            }
        }
    }

    private static int traverse(Char_seperator c_temp, String word, int len, int start_dif, boolean one_more_chance) {
        char Word_ch = ' ', Ht_ch;
        boolean incr = false;
//        for (int l = 0; l < 2; l++) {
        if (word.length() != 0) {
//                try {
            Word_ch = word.charAt(0);//l);
//                } catch (Exception ex) {
//                    jout.println("index out of bound.");
//                    break;
//                }
            Enumeration enume2 = c_temp.getCharacters().keys();//be character e avale tamame kalamate ba tole i dast resi peyda mikonim
            while (enume2.hasMoreElements()) {
                dif = start_dif;
                Ht_ch = (Character) enume2.nextElement();
                Char_seperator next_char = (Char_seperator) c_temp.getCharacters().get(Ht_ch);
                if (Word_ch != Ht_ch) {
                    dif++;
                    incr = true;
                }
                if (dif > Max_dif && !one_more_chance) {
//                    traverse(c_temp, word.substring(1), len - 1, dif - 1, true);
                } else if (dif <= Max_dif && c_temp.getWord() == null) {
                    traverse(next_char, word.substring(1), len - 1, dif, one_more_chance);
                    if (incr && !one_more_chance) {
                        stack = dif;
                        traverse(next_char, word, len, dif - 1, true);
                        dif = stack;
                        traverse(c_temp, word.substring(1), len - 1, dif - 1, true);
                        dif = stack;
                        incr = false;
                    }
                } else if (dif <= Max_dif && c_temp.getWord() != null) {
                    if (!chosen.contains(c_temp.getWord())) {
                        chosen.add(c_temp.getWord());
                    }
                }
            }
        } else if (c_temp.getWord() != null && (Math.abs(c_temp.getWord().length() - original_word.length()) + dif) <= Max_dif) {
//            chosen.add(c_temp.getWord());
            if (!chosen.contains(c_temp.getWord())) {
                chosen.add(c_temp.getWord());
            }
        } else if ((dif + 1) <= Max_dif && c_temp.getWord() == null) {
            Enumeration iter_enume = c_temp.getCharacters().keys();
            while (iter_enume.hasMoreElements()) {
                char harf = (Character) iter_enume.nextElement();
                Char_seperator iter_insert = (Char_seperator) c_temp.getCharacters().get(harf);
                chosen.add(iter_insert.getWord());
            }
        }
        dif = start_dif;
//        }
        return chosen.size();
    }

    //loads dictionary file as a tree
    private static void Dic_Loader() throws IOException {
        String str;
        char ch;
        Size_seperator size_p;
        Char_seperator char_p, temp_op = null, c_op;
        while ((str = bfr.readLine()) != null) {
            int index, len = 0;
            String word = str.substring(0, index = str.indexOf("_"));
//            String tag = str.substring(index + 1, str.length());
            word = word.trim();
            if (word.equals("accompanied")) {
                jout.println("accompanied");
            }
//            tag = tag.trim();
            len = word.length();
            lexicon.add(word);
            if (!size_div.containsKey(len)) {
                size_p = new Size_seperator();
                size_p.setLen(len);//size e kalame ra set mikonim
                char_p = new Char_seperator();//hala yek kalame ba in tol darim ke ghabl az in kalamate ba in tool ba harfe shoroe manande in shoroe nashode and. 
                //pas harfe shoroe in kalame ra be onvane yek objecte jadid darj mikonim
                ch = word.charAt(0);
                size_p.getCharacter().put(ch, char_p);
                temp_op = char_p;
                for (int i = 1; i < len; i++) {
                    ch = word.charAt(i);
                    c_op = new Char_seperator();
                    temp_op.getCharacters().put(ch, c_op);
                    temp_op = c_op;
                }
                size_div.put(len, size_p);
            } else {
                size_p = (Size_seperator) size_div.get(len);
                Hashtable temp_ht = size_p.getCharacter();
                ch = word.charAt(0);
                if (temp_ht.containsKey(ch)) {
                    temp_op = (Char_seperator) temp_ht.get(ch);
                } else {
                    temp_op = new Char_seperator();
                    temp_ht.put(ch, temp_op);
                }
                for (int i = 1; i < len; i++) {
                    ch = word.charAt(i);
                    if (temp_op.getCharacters().containsKey(ch)) {
                        temp_op = (Char_seperator) temp_op.getCharacters().get(ch);
                    } else {
                        c_op = new Char_seperator();
                        temp_op.getCharacters().put(ch, c_op);
                        if (i != len - 1) {
                            temp_op = c_op;
                        } else {
                            c_op.setWord(word);
                        }
                    }
                }
//                temp_op.setWord(word);
            }
        }
    }

    private static boolean pattern(String str) {
        //it checks if inupt String's pattern is same as some defined patterns. 
        //patterns like numbers and dates and something which is obvious that they are not real words in Spell Checking
        //then they do not need to generate candidate words for them. 
        if(str.matches("[0-9]+")){
            return true;
        }
        if(str.matches("[0-9]+th")){
            return true;
        }
        return false;
    }

    /*private static String[][] Gen_NonWord_Candids(String left_w, String word, String right_w) {
     Size_seperator size_p;
     String[][] chosen = new String[3][2];
     int index = 0;
     float max = 0, temp = 0;
     Hashtable poses;
     String left_tag = null;
     String right_tag = null;
     if (!left_w.equals("NA")) {
     left_tag = tagger.tagTokenizedString(left_w);
     left_tag = left_tag.substring(left_tag.indexOf("_") + 1, left_tag.length());
     left_tag = left_tag.trim();
     }
     if (!right_w.equals("NA")) {
     right_tag = tagger.tagTokenizedString(right_w);
     right_tag = right_tag.substring(right_tag.indexOf("_") + 1, right_tag.length());
     right_tag = right_tag.trim();
     }
     int len = word.length();
     for (int i = len - 1; i <= len + 1; i++) {
     size_p = (Size_seperator) size_div.get(i);
     poses = size_p.getPos();
     Enumeration enume = poses.keys();
     while (enume.hasMoreElements()) {
     String tag = (String) enume.nextElement();
     float fi, se;
     if (!left_w.equals("NA")) {
     try {
     fi = (Float) SpellChecker.T_LM.get(left_tag + " " + tag);
     } catch (NullPointerException ex) {
     fi = 0;
     }
     } else {
     fi = 1;
     }
     if (!right_w.equals("NA")) {
     try {
     se = (Float) SpellChecker.T_LM.get(tag + " " + right_tag);
     } catch (NullPointerException ex) {
     se = 0;
     }
     } else {
     se = 1;
     }
     temp = fi * se;
     if (temp > max) {
     max = temp;
     chosen[index][0] = i + "";
     chosen[index][1] = tag;
     }
     }
     max = temp = 0;
     index++;
     }
     return chosen;
     }*/
}
