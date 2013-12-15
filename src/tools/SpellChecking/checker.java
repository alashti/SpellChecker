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

    public checker(String Dic_path, String test_set, String tagger_training_set) throws FileNotFoundException, IOException, ClassNotFoundException {
        bfr = new BufferedReader(new FileReader(new File(Dic_path)));
        bfr_test = new BufferedReader(new FileReader(new File(test_set)));
        tagger = new MaxentTagger(tagger_training_set);
    }

    public void manager() throws IOException {
        checker.Dic_Loader();
//        jout.println("dictionary loading has been finished, ensha allah.");
        String[] Split;
        String[][] chosen = new String[3][2];
        String str;
        str = bfr_test.readLine();
        W_master wmaster = new W_master();
        Split = wmaster.Tokenize(str.split(" "));
        int len = Split.length;
        for (int i = 0; i < len; i++) {
            if (lexicon.contains(Split[i]) || lexicon.contains(Split[i].toLowerCase()) || lexicon.contains(Split[i].toUpperCase())) {
                jout.println("may be real word.");
            } else {
                jout.println("nonword: " + Split[i]);
                if(i == 0){
                    chosen = checker.Gen_NonWord_Candids("NA", Split[i], Split[i + 1]);
                }
                else if(i == len - 1){
                    chosen = checker.Gen_NonWord_Candids(Split[i - 1], Split[i], "NA");
                }
                else{
                    chosen = checker.Gen_NonWord_Candids(Split[i - 1], Split[i], Split[i + 1]);
                }
            }
            jout.println("See chosens.");
        }
        jout.println("testing manager finished.");
    }
    //Generates Candidates for a Word
    private static String[][] Gen_NonWord_Candids(String left_w, String word, String right_w) {
        Size_seperator size_p;
        String[][] chosen = new String[3][2];
        int index = 0;
        float max = 0, temp = 0;
        Hashtable poses;
        String left_tag = null;
        String right_tag = null;
        if(!left_w.equals("NA")){
            left_tag = tagger.tagTokenizedString(left_w);
            left_tag = left_tag.substring(left_tag.indexOf("_") + 1, left_tag.length());
            left_tag = left_tag.trim();
        }
        else if(!right_w.equals("NA")){
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
                if(!left_w.equals("NA")){
                    try{
                        fi = (Float) SpellChecker.T_LM.get(left_tag + " " + tag);
                    }
                    catch(NullPointerException ex){
                        fi = 0;
                    }
                }
                else{
                    fi = 1;
                }
                if(!right_w.equals("NA")){
                    try{
                        se = (Float) SpellChecker.T_LM.get(tag + " " + right_tag);
                    }
                    catch(NullPointerException ex){
                        se = 0;
                    }
                }
                else{
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
    }

    //loads dictionary file
    private static void Dic_Loader() throws IOException {
        String str;
        Size_seperator size_p;
        Pos_seperator pos_p;
        while ((str = bfr.readLine()) != null) {
            int index, len = 0;
            String word = str.substring(0, index = str.indexOf("_"));
            String tag = str.substring(index + 1, str.length());
            word = word.trim();
            tag = tag.trim();
            len = word.length();
            lexicon.add(word);
            if (!size_div.containsKey(len)) {
                size_p = new Size_seperator();
                pos_p = new Pos_seperator();
                size_p.setLen(len);
                size_p.getPos().put(tag, pos_p);
                pos_p.getWords().add(word);
                size_div.put(len, size_p);
            } else {
                size_p = (Size_seperator) size_div.get(len);
                Hashtable temp = size_p.getPos();
                if (temp.containsKey(tag)) {
                    pos_p = (Pos_seperator) temp.get(tag);
                    pos_p.getWords().add(word);
                } else {
                    pos_p = new Pos_seperator();
                    pos_p.getWords().add(word);
                    temp.put(tag, pos_p);
                }
            }
        }
    }
}
