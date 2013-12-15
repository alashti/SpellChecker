/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.Word_bigram_LM;

import java.util.LinkedList;

/**
 *
 * @author epcpu
 */
public class Words {
    int freq;
    LinkedList similars = new LinkedList();
    int similars_freq = 0;

    public int getSimilars_freq() {
        return similars_freq;
    }

    public void setSimilars_freq(int similars_freq) {
        this.similars_freq += similars_freq;
    }
    
    public void incSimilars_Freq(){
        this.similars_freq++;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public void incFreq(){
        this.freq++;
    }
    
    public LinkedList getSimilars() {
        return similars;
    }

    public void setSimilars(LinkedList similars) {
        this.similars = similars;
    }
    public boolean addSimilars(String word){
        if(!similars.contains(word)){
            similars.add(word);
            return true;
        }
        else{
            return false;
        }
    }
}
