/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.Word_bigram_LM;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author epcpu
 */
public class Wordnet_Works {

    IDictionary dict;
    WordnetStemmer stem;
    static PrintStream jout = System.out;
    String path = "F:\\Program Files\\WordNet\\2.1\\dict";

    public Wordnet_Works() throws IOException {
        dict = new Dictionary(new File(path));
        stem = new WordnetStemmer(dict);
        dict.open();
    }
    
    public LinkedList Synonyms(String kalame) {
        IIndexWord idx_word;
//        int i = 0;
        LinkedList syns = new LinkedList();
        POS[] pos = new POS[4];
        pos[0] = POS.NOUN; pos[1] = POS.VERB; pos[2] = POS.ADJECTIVE; pos[3] = POS.ADVERB;
        for(int k = 0; k < 4; k++) {
            if ((idx_word = dict.getIndexWord(kalame, pos[k])) != null) {
                List li = idx_word.getWordIDs();
                for (int j = 0; j < li.size(); j++) {
                    IWordID word_id = (IWordID) li.get(j);
                    IWord word = dict.getWord(word_id);
                    ISynset Isynset = word.getSynset();
                    for (IWord w : Isynset.getWords()) {
                        if(!syns.contains(w.getLemma())){
                            syns.add(w.getLemma());
                        }
                    }
                }
            }
        }
        return syns;
    }

    public List<String> stem(String word) {
        return stem.findStems(word, null);
    }
    
    public static void similarity() {
        
    }
}
