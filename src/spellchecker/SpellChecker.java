/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spellchecker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import tools.SpellChecking.checker;
import tools.Tag_bigram_LM.T_master;
import tools.Word_bigram_LM.W_master;
/**
 *
 * @author epcpu
 */
public class SpellChecker {

    /**
     * @param args the command line arguments
     */
    //////
    static PrintStream jout = System.out;
    //////
    public static Hashtable W_LM;
    public static Hashtable T_LM;
    public static void main(String[] args) throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        // TODO code application logic here
        String W_corpus_dir = "English_corpus.txt";
        String T_corpus_dir = "Tagged_English_corpus.txt";
        String T_dictionary = "Tagged_dictionary.txt";
        String test_set = "test_eng.txt";
        String tagger_training_set = "left3words-wsj-0-18.tagger";
        W_master w_lm = new W_master(W_corpus_dir);
        T_master t_lm = new T_master(T_corpus_dir);
//        token(W_corpus_dir);
        W_LM = w_lm.Manager();//makes language model out of words
        T_LM = t_lm.Manager();//makes language model out of words' tags
//        jout.println("language modeling has been finished, ensha allah.");
        checker test_phase = new checker(T_dictionary, test_set, tagger_training_set);
        test_phase.manager();
    }
//    public static void token(String dir) throws IOException{
//        String path = "tokenized_corpus.txt";
//        BufferedReader bfr = new BufferedReader(new FileReader(new File(dir)));
//        BufferedWriter bfw = new BufferedWriter(new FileWriter(new File(path)));
//        String str;
//        String[] Split;
//        W_master tokenize = new W_master();
//        while((str = bfr.readLine()) != null){
//            Split = str.split(" ");
//            Split = tokenize.Tokenize_eng(Split);
//            str = edu.stanford.nlp.util.StringUtils.join(Split);
//            bfw.append(str);
//            bfw.newLine();
//            bfw.flush();
//        }
//    }
}
