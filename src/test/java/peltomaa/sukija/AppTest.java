package peltomaa.sukija;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.puimula.libvoikko.*;
import peltomaa.sukija.finnish.HVTokenizer;
import peltomaa.sukija.voikko.VoikkoFilter;
import peltomaa.sukija.voikko.VoikkoUtils;
import peltomaa.sukija.attributes.VoikkoAttribute;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;


/**
 * Unit test for simple App.
 */
public class AppTest  extends TestCase
{
  /**
   * Create the test case.
   *
   * @param testName name of the test case
   */
  public AppTest (String testName)
  {
    super (testName);
    voikko = VoikkoUtils.getVoikko ("fi", VOIKKO_PATH, LIBRARY_PATH, LIBVOIKKO);
  }


  /**
   * @return the suite of tests being tested.
   */
  public static Test suite()
  {
    return new TestSuite (AppTest.class);
  }


  /**
   * Rigourous Test :-)
   */
  public void testApp()
  {
    try {
      assertTrue (true);
      assertTrue (test ("alusta", ""));
      assertTrue (test ("huuhaahoo", ""));
      assertTrue (test ("dioksidiin", ""));
      assertTrue (test ("honka-mänty-petäjä", ""));
      assertTrue (test ("menisimmekö", ""));
      assertTrue (test ("kauniisti", ""));
    }
    catch (Throwable t)
    {
      t.printStackTrace (System.out);
    }
  }


  private boolean test (String input, String expectedOutput) throws IOException
  {
    Reader r = new StringReader (input);
    TokenStream t = new HVTokenizer();
    ((Tokenizer)t).setReader (r);
    t = new VoikkoFilter (t, voikko);
    t.reset();
    VoikkoAttribute sukijaAtt = t.addAttribute (VoikkoAttribute.class);
    CharTermAttribute termAtt = t.addAttribute (CharTermAttribute.class);

    while (t.incrementToken()) {
      System.out.println ("AppTest " + termAtt.toString());
      for (int i = 0; i < sukijaAtt.getAnalysis().size(); i++) {
        System.out.println (sukijaAtt.getAnalysis(i).get("BASEFORM"));
//        VoikkoUtils.printAnalysisResult (sukijaAtt.getAnalysis(i), System.out);
      }
      System.out.println ("");
    }

    return true;
  }

  private Voikko voikko;
  private static final String HOME = System.getProperty ("user.home");
  private static final String VOIKKO_PATH = HOME + "/vvfst/voikkodict";
  private static final String LIBRARY_PATH = HOME + "/vvfst/lib";
  private static final String LIBVOIKKO = HOME + "/vvfst/lib/libvoikko.so";
}
