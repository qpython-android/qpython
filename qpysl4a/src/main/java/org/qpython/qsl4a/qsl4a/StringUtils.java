package org.qpython.qsl4a.qsl4a;

import java.util.Collection;
import java.util.Iterator;

public class StringUtils {

  private StringUtils() {
    // Utility class.
  }
  public static String addSlashes(String txt)
  {
    if (null != txt)
    {
      txt = txt.replace("\\", "\\\\") ;
      txt = txt.replace("\'", "\\\'") ;
      //txt = txt.replace(" ", "\\ ") ;

    }

    return txt ;
  }

  public static String join(Collection<String> collection, String delimiter) {
    StringBuffer buffer = new StringBuffer();
    Iterator<String> iter = collection.iterator();
    while (iter.hasNext()) {
      buffer.append(iter.next());
      if (iter.hasNext()) {
        buffer.append(delimiter);
      }
    }
    return buffer.toString();
  }
}
