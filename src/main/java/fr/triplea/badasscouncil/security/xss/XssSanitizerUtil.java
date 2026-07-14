package fr.triplea.badasscouncil.security.xss;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.web.util.HtmlUtils;

public class XssSanitizerUtil 
{
  
  private XssSanitizerUtil() {}
  
  private static List<Pattern> patterns = new ArrayList<Pattern>();
  
  static 
  {
    patterns.add(Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE));
    patterns.add(Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
    patterns.add(Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
    patterns.add(Pattern.compile("</script>", Pattern.CASE_INSENSITIVE));
    patterns.add(Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
    patterns.add(Pattern.compile("<input(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
    patterns.add(Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
    patterns.add(Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
    patterns.add(Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE));
    patterns.add(Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE));
    patterns.add(Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
    patterns.add(Pattern.compile("onfocus(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
    patterns.add(Pattern.compile("<form[^>]*>.*?</form>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL));
  }

  private static Pattern htmlPattern = Pattern.compile("<(\"[^\"]*\"|'[^']*'|[^'\">])*>", Pattern.MULTILINE);
  
  public static String stripXSS(String value) 
  {
    if (value != null) 
    { 
      value = value.replaceAll("\0", "");
      
      for (Pattern pattern : patterns) { value = pattern.matcher(value).replaceAll(""); } 
      
      if (htmlPattern.matcher(value).find()) { value = HtmlUtils.htmlEscape(value); }
    }
    return value;
  }
  
}
