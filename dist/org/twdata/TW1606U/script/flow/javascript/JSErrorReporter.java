package org.twdata.TW1606U.script.flow.javascript;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.tools.ToolErrorReporter;
import org.apache.log4j.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Implements a Rhino JavaScript {@link
 * org.mozilla.javascript.ErrorReporter}. 
 * Like ToolErrorReporter but logs to supplied logger instead of stdout
 *
 * @version CVS $Id: JSErrorReporter.java,v 1.3 2004/08/08 04:26:02 mrdon Exp $
 */
public class JSErrorReporter implements ErrorReporter
{
  private Logger logger;
  private JFrame frame;

  public JSErrorReporter() {}
  
  public void setLogger(Logger logger)
  {
      this.logger = logger;
  }
  
  public void setFrame(JFrame frame) {
      this.frame = frame;
  }

  public void error(String message,
                    String sourceName, int line,
                    String lineSrc, int column)
  {
      String errMsg = getErrorMessage("msg.error", message, 
                                      sourceName, line, lineSrc, column);
      if (frame != null) {
          JOptionPane.showMessageDialog(frame, errMsg, "Javascript Error", JOptionPane.ERROR_MESSAGE);
      }
      logger.error(errMsg);
  }

  public void warning(String message, String sourceName, int line,
                      String lineSrc, int column)
  {
      String errMsg = getErrorMessage("msg.warning", message, 
                                    sourceName, line, lineSrc, column);
      if (frame != null) {
          JOptionPane.showMessageDialog(frame, errMsg, "Javascript Warning", JOptionPane.WARNING_MESSAGE);
      }
      logger.warn(errMsg);
  }
    
  public EvaluatorException runtimeError(String message, String sourceName,
                                         int line, String lineSrc,
                                         int column)
  {
      String errMsg = getErrorMessage("msg.error", message,
                                      sourceName, line,
                                      lineSrc, column);
      if (frame != null) {
          JOptionPane.showMessageDialog(frame, errMsg, "Javascript Error", JOptionPane.ERROR_MESSAGE);
      }
      return new EvaluatorException(errMsg);
  }

  /**
   * Formats error message
   *
   * @param type a <code>String</code> value, indicating the error
   * type (error or warning)
   * @param message a <code>String</code> value, the error or warning
   * message
   * @param line an <code>int</code> value, the original cummulative
   * line number
   * @param lineSource a <code>String</code> value, the text of the
   * line in the file
   * @param column an <code>int</code> value, the column in
   * <code>lineSource</code> where the error appeared
   * @return a <code>String</code> value, the aggregated error
   * message, with the source file and line number adjusted to the
   * real values
   */
    String getErrorMessage(String type,
                           String message,
                           String sourceName, int line,
                           String lineSource, int column)
    {
        if (line > 0) {
            if (sourceName != null) {
                Object[] errArgs = { sourceName, new Integer(line), message };
                return ToolErrorReporter.getMessage("msg.format3", errArgs);
          } else {
              Object[] errArgs = { new Integer(line), message };
              return ToolErrorReporter.getMessage("msg.format2", errArgs);
            }
        } else {
            Object[] errArgs = { message };
            return ToolErrorReporter.getMessage("msg.format1", errArgs);
        }
    }
}
