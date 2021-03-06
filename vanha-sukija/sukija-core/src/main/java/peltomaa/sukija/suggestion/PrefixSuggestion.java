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

import peltomaa.sukija.morphology.Morphology;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Try to recognise a word without a prefix. If succesful, return
 * prefix + recognised word.
 *
 * For example, if prefix is "aasian" and word is "aasianleijonaa",
 * try to recognise "leijonaa". If succesful, return "aasianleijona".
 */
public class PrefixSuggestion extends Suggestion {
  public PrefixSuggestion (Morphology morphology, String prefix, boolean savePrefix, boolean saveWord)
  {
    super (morphology);
    prefixSet = new HashSet<String>();
    prefixSet.add (prefix);
    minLength = prefix.length();
    maxLength = minLength;
    this.savePrefix = savePrefix;
    this.saveWord = saveWord;
  }


  public PrefixSuggestion (Morphology morphology, String prefix)
  {
    this (morphology, prefix, true, true);
  }


  public PrefixSuggestion (Morphology morphology, List<String> prefix, boolean savePrefix, boolean saveWord)
  {
    super (morphology);
    prefixSet = new HashSet<String> (prefix);
    minLength = 1000000;
    maxLength = 0;
    for (String s: prefixSet) {
      if (minLength > s.length()) minLength = s.length();
      if (maxLength < s.length()) maxLength = s.length();
    }
    this.savePrefix = savePrefix;
    this.saveWord = saveWord;
  }


  public PrefixSuggestion (Morphology morphology, List<String> prefix)
  {
    this (morphology, prefix, true, true);
  }


  public boolean suggest (String word)
  {
    reset();

    // Käydään läpi kaikki etuliitteet pisimmästä alkaen ja
    // lopetetaan, kun löytyy eka tunnistettu sana.
    //
    for (int i = Math.min (maxLength, word.length()); i >= minLength; i--) {
      final String p = word.substring (0, i);    // Sanan alku.
      if (prefixSet.contains (p)) {              // Onko etuliite?
        set.clear();
        if (analyse (word.substring(i), set)) {  // Tunnistetaanko etuliitteetön sana?
          result.clear();
          if (savePrefix) result.add (p);
          for (String s: set) {
            result.add (p + s);
            if (saveWord) result.add (s);
          }
          return true;
        }
      }
    }
    return false;
  }

  private Set<String> set = new TreeSet<String>();
  private Set<String> prefixSet;  // Etuliitteet.
  private int minLength;  // Lyhimmän etuliitteen pituus.
  private int maxLength;  // Pisimmän etuliitteen pituus.
  private boolean savePrefix;
  private boolean saveWord;
}
