/**
 *   JGAAP -- Java Graphical Authorship Attribution Program
 *   Copyright (C) 2009 Patrick Juola
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation under version 3 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 **/
package com.jgaap.canonicizers;

import java.awt.Color;
import java.util.Vector;

import com.jgaap.generics.Canonicizer;

/** 
 * Strips all non-punctuation from the document. 
 *
 * @since 4.1
 */
public class StripNonPunc extends Canonicizer {

    private String punc = ",.?!\"\'`;:-()&$"; // Characters which will be
    // considered "punctuation"

    @Override
    public String displayName(){
    	return "Strip AlphaNumeric";
    }
    
    @Override
    public String tooltipText(){
    	return "Strip all non-punctuation/non-whitespace characters from the text.";
    }
    
    @Override
    public boolean showInGUI(){
    	return true;
    }
    
    @Override
    public Color guiColor(){
    	return Color.YELLOW;
    }
    

    /**
     * Strip non-punctuation from input characters
     * @param procText Vector of characters to be processed.
     * @return Vector of processed characters.
     */
    @Override
    public Vector<Character> process(Vector<Character> procText) {
        Vector<Character> processed = new Vector<Character>();
        for (int i = 0; i < procText.size(); i++) {
            if (punc.indexOf(procText.elementAt(i)) == -1 &&
		!Character.isWhitespace(procText.elementAt(i)) ) { 
		// If the character is not punctuation
                ; // Do nothing
            } else {
                processed.add(procText.elementAt(i));
            }
        }
        return processed;
    }
}
