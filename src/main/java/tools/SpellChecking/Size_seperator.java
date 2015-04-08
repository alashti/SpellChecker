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
public class Size_seperator {
    int len;
    Hashtable character = new Hashtable();

    public Hashtable getCharacter() {
        return character;
    }

    public void setCharacter(Hashtable character) {
        this.character = character;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }
    
}
