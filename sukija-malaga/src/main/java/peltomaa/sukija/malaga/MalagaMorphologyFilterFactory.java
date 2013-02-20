/*
Copyright (©) 2012 Hannu Väisänen

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

package peltomaa.sukija.malaga;

import java.util.Map;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.TokenStream;;
import org.apache.solr.analysis.BaseTokenFilterFactory;
import peltomaa.sukija.morphology.MorphologyFilter;
import peltomaa.sukija.util.PropertiesUtil;

/**
 * Factory for {@link MorphologyFilter}. 
 * <pre class="prettyprint" >
 * &lt;fieldType name="text" class="solr.TextField"
 *   &lt;analyzer&gt;
 *     &lt;filter class="peltomaa.sukija.malaga.MalagaMorphologyFilterFactory"
              malagaProjectFile="${user.home}/.sukija/suomi.pro"/&gt;
 *   &lt;/analyzer&gt;
 * &lt;/fieldType&gt;</pre> 
 */
public class MalagaMorphologyFilterFactory extends BaseTokenFilterFactory {
  @Override
  public void init (Map<String,String> args)
  {
    super.init (args);
    malagaProjectFile = PropertiesUtil.replacePropertyNameWithValue (args.get ("malagaProjectFile"));
//System.out.println ("mal1 " + malagaProjectFile);
  }


  @Override
  public MorphologyFilter create (TokenStream input)
  {
//System.out.println ("mal2 " + malagaProjectFile);
    return new MorphologyFilter (input, MalagaMorphology.getInstance (malagaProjectFile));
  }


  private String malagaProjectFile;
}
