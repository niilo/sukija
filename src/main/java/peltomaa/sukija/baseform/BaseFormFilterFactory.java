/*
Copyright (©) 2015-2016 Hannu Väisänen

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

package peltomaa.sukija.baseform;

import java.io.InputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.solr.util.PropertiesUtil;
import org.puimula.libvoikko.*;
import peltomaa.sukija.suggestion.Suggestion;
import peltomaa.sukija.suggestion.SuggestionFilter;
import peltomaa.sukija.suggestion.SuggestionParser;
import peltomaa.sukija.util.SukijaFilterFactory;
import peltomaa.sukija.voikko.VoikkoUtils;


public class BaseFormFilterFactory extends SukijaFilterFactory implements ResourceLoaderAware {
  /** Tehdään uusi BaseFormFilterFactory.
   */
  public BaseFormFilterFactory (Map<String,String> args)
  {
    super (args);
    suggestionFile = getValue (args, "suggestionFile");
    successOnly    = getBoolean (args, "successOnly", false);
  }


  @Override
  public TokenFilter create (TokenStream input)
  {
    if (suggestion == null) {
      return new BaseFormFilter (input, voikko, successOnly);
    }
    else {
      return new SuggestionFilter (input, voikko, suggestion, successOnly);
    }
  }


  @Override
  public void inform (ResourceLoader loader) throws IOException
  {
    LOG.info ("inform1 " + loader.getClass().getName());
    createVoikko();

    if (suggestionFile != null && suggestion == null) {
      InputStream inputStream = loader.openResource (suggestionFile);
      try {
        SuggestionParser parser = new SuggestionParser (voikko, inputStream);
        suggestion = parser.getSuggestions();
      }
      catch (SuggestionParser.SuggestionParserException e)
      {
        LOG.error (e.getMessage());
        if (e.getCause() != null) LOG.error (e.getCause().getMessage());
      }
    }
    LOG.info ("inform2 " + loader.getClass().getName());
  }

  private String suggestionFile;
  private boolean successOnly;
  private Suggestion[] suggestion;
}
