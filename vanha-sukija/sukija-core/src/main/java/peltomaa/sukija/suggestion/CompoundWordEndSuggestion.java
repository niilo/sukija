/*
Copyright (©) 2014-2015 Hannu Väisänen

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import peltomaa.sukija.morphology.Morphology;
import peltomaa.sukija.util.RegexUtil;


public class CompoundWordEndSuggestion extends Suggestion {
  public CompoundWordEndSuggestion (Morphology morphology, String regex, String last, boolean addStart, boolean addEnd)
  {
    super (morphology);
    pattern.add (RegexUtil.makePattern (regex));
    end.add (last);
    this.addStart = addStart;
    this.addEnd = addEnd;
  }


  public CompoundWordEndSuggestion (Morphology morphology, String regex, String last)
  {
    this (morphology, regex, last, true, true);
  }


  public CompoundWordEndSuggestion (Morphology morphology, List<String> s, boolean addStart, boolean addEnd)
  {
    super (morphology);
    for (int i = 0; i < s.size(); i += 2) {
      pattern.add (RegexUtil.makePattern (s.get(i)));
      end.add (s.get(i+1));
    }
    this.addStart = addStart;
    this.addEnd = addEnd;
  }


  public CompoundWordEndSuggestion (Morphology morphology, List<String> s)
  {
    this (morphology, s, true, true);
  }


  public boolean suggest (String word)
  {
    for (int i = 0; i < pattern.size(); i++) {
      Matcher matcher = pattern.get(i).matcher (word);
//System.out.println ("Huu " + word + " " + pattern.get(i).pattern());
      if (matcher.find()) {
//System.out.println ("Haa " + word + " " + pattern.get(i).pattern());
        reset();
        set.clear();
        result.clear();
        if (analyse (word.substring(matcher.start()), set)) {
          final String start = word.substring (0, matcher.start());
          if (addStart) addStart (start);
          if (addEnd) result.add (end.get(i));
          for (String s: set) {
            if (s.endsWith (end.get(i))) {
//System.out.println ("Huu [" + word + "] [" + s + "] [" + start + "] [" + s + "] [" + (start+s) + "] [" + end.get(i) + "]");
              result.add (start + s);
              found = true;
            }
          }
//System.out.println (result.toString());
          return found;
        }
      }
    }
    return false;
  }


  private void addStart (String s)
  {
    startSet.clear();
    analyse (s, startSet);
    for (String p: startSet) {
      if (p.endsWith ("-")) {
        result.add (p.substring (0, p.length()-1));
      }
      else {
        result.add (p);
      }
    }
  }


  private Vector<Pattern> pattern = new Vector<Pattern>();
  private Vector<String> end = new Vector<String>();
  private Set<String> set = new TreeSet<String>();
  private Set<String> startSet = new TreeSet<String>();
  private boolean addStart;
  private boolean addEnd;
}
