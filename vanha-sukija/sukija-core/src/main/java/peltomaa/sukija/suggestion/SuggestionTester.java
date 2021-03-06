/*
Copyright (©) 2013-2015 Hannu Väisänen

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package peltomaa.sukija.suggestion;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import peltomaa.sukija.finnish.HVTokenizer;
import peltomaa.sukija.hyphen.HyphenFilter;
import peltomaa.sukija.morphology.Morphology;


/**
 * Test suggestions.
 */
public class SuggestionTester {
  public SuggestionTester (Morphology morphology)
  {
    this.morphology = morphology;
    data.add (new Object[]{"wanhoille",    "vanha",    new CharSuggestion (morphology, "w", "v")});
    data.add (new Object[]{"kuuusikolle",  "kuusikko", new Length3Suggestion (morphology)});
    data.add (new Object[]{"Bordeaux'iin", "Bordeaux", new ApostropheSuggestion (morphology)});
  }


  public boolean test (String input, String expectedOutput, Suggestion suggestion) throws IOException
  {
    final String result = analyze (input, suggestion);
    System.out.println ("Syöte " + input + ", tulos [" + result + "], odotettu tulos [" + expectedOutput + "].");
    return ((result != null) && result.equals (expectedOutput));
  }


  public boolean test (Object[] parameter) throws IOException
  {
    return test ((String)parameter[0], (String)parameter[1], (Suggestion)parameter[2]);
  }


  private String analyze (String input, Suggestion suggestion) throws IOException
  {
    Set<String> set = new TreeSet<String>();

    Reader r = new StringReader (input);
    Tokenizer t = new HVTokenizer();
    t.setReader (r);
    CharTermAttribute word = t.getAttribute (CharTermAttribute.class);

    try {
      t.reset();
      while (t.incrementToken()) {
        final String w = word.toString();
//        System.out.println ("Sana: " + w);
        set.clear();
        if (morphology.analyzeLowerCase (w, set)) {
          print ("m", set);
        }
        else if (suggestion.suggest (w)) {
          Set<String> result = suggestion.getResult();
//          print ("s", result);
          if (result.size() == 1) {
            return result.iterator().next();
          }
        }
        else {
          return null;
        }
      }
      t.end();
    }
    finally {
      t.close();
    }

    return null;
  }


  public static void analyze (Reader reader, Morphology morphology, Vector<Suggestion> suggestion, boolean stopOnSuccess) throws IOException
  {
    Set<String> set = new TreeSet<String>();
    Tokenizer u = new HVTokenizer();
    u.setReader (reader);
    CharTermAttribute word = u.addAttribute (CharTermAttribute.class);
    OffsetAttribute offsetAtt = u.getAttribute (OffsetAttribute.class);
    PositionIncrementAttribute posIncrAtt = u.getAttribute (PositionIncrementAttribute.class);
    TokenStream t = new HyphenFilter (u);

    Vector<String> vword = new Vector<String>();
    Set<String> vset = new TreeSet<String>();
    boolean output = false;

    try {
      t.reset();
      set.clear();
      while (t.incrementToken()) {
        final String w = word.toString();
        System.out.print ("Sana: " + posIncrAtt.getPositionIncrement() + " " + offsetAtt.startOffset() + " " + w);
        set.clear();
        if (morphology.analyzeLowerCase (w, set)) {
          print ("M", set);
        }
        else if (suggestion != null) {
          for (Suggestion s : suggestion) {
            if (s.suggest(w)) {
              print ("S", s.getResult());
              if (stopOnSuccess) {
                break;
              }
            }
          }
        }
        System.out.println ("");
      }
      t.end();
    }
    finally {
      t.close();
    }
  }


  public static void testSuggestionFilter (Reader reader, Morphology morphology, Vector<Suggestion> suggestion, boolean stopOnSuccess) throws IOException
  {
    Tokenizer u = new HVTokenizer();
    u.setReader (reader);
    TokenStream t = new SuggestionFilter (u, morphology, suggestion, stopOnSuccess);
    CharTermAttribute word = t.getAttribute (CharTermAttribute.class);

    try {
      t.reset();
      while (t.incrementToken()) {
//        System.out.println ("Sana: " + word.toString());
      }
      t.end();
    }
    finally {
      t.close();
    }
  }


  private static void print (String prefix, Collection<String> c)
  {
    System.out.print (" " + prefix);
    for (String s: c) {
      System.out.print (" " + s);
    }
  }


  private Morphology morphology;

  /** Eri morfologioille yhteiset testit.
   */
  public static final Vector<Object[]> data = new Vector<Object[]>();
}
