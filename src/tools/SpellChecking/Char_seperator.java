/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tools.SpellChecking;

import java.util.Hashtable;

/**
 *
 * @author epcpu
 */
public class Char_seperator {
    String word;
    Hashtable characters = new Hashtable();

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Hashtable getCharacters() {
        return characters;
    }

    public void setCharacters(Hashtable characters) {
        this.characters = characters;
    }
    
}
