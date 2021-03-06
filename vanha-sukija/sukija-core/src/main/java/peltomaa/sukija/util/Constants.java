/*
Copyright (©) 2015 Hannu Väisänen

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

package peltomaa.sukija.util;

import java.util.regex.Pattern;
import org.apache.lucene.analysis.tokenattributes.FlagsAttribute;


public final class Constants {
  private Constants() {}

  public static final int WORD = 2;
  public static final int HYPHEN = 4;
  public static final int BRACKET = 8; // []

  public static final boolean hasFlag (FlagsAttribute flagsAtt, int flag)
  {
    return ((flagsAtt.getFlags() & flag) != 0);
  }


  public static final Pattern HYPHEN_REGEX = Pattern.compile ("-+|\"-+|–+|''-+|'-+|[.]-+");
}
