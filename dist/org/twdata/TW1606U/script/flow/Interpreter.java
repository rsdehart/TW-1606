/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Cocoon" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/
package org.twdata.TW1606U.script.flow;

import java.util.List;
import java.net.URL;

public interface Interpreter
{


  public static class Argument
  {
    public String name;
    public String value;

    public Argument(String name, String value)
    {
      this.name = name;
      this.value = value;
    }

    public String toString()
    {
      return name + ": " + value;
    }
  }
  
  public void register(String id, String code) throws Exception;
  
  public void exec(URL url, boolean ownThread) throws Exception;
  
  public void exec(String id, boolean ownThread) throws Exception;
  
  public void stopAll();
  
  /**
   * The method will execute the named function, which must be defined
   * in the given language. There is no assumption made on how various
   * arguments are passed to the function.
   *
   * <p>The <code>params</code> argument is a <code>List</code> object
   * that contains <code>Interpreter.Argument</code> instances,
   * representing the parameters to be passed to the called
   * function. An <code>Argument</code> instance is a key-value pair,
   * where the key is the name of the parameter, and the value is its
   * desired value. Most languages will ignore the name value and
   * simply pass to the function, in a positional order, the values of
   * the argument. Some languages however can pass the arguments in a
   * different order than the original prototype of the function. For
   * these languages the ability to associate the actual argument with
   * a formal parameter using its name is essential.
   *
   * @param funName a <code>String</code> value, the name of the
   * function to call
   * @param params a <code>List</code> object whose components are
   * CallFunctionNode.Argument instances. The interpretation of the
   * parameters is left to the actual implementation of the
   * interpreter.
   */
  public void exec(String id, Object arg, boolean newThread)
    throws Exception;

  /**
   * Continues a previously started processing. The continuation
   * object where the processing should start from is indicated by the
   * <code>continuationId</code> string.
   *
   * @param continuationId a <code>String</code> value
   *
   * @param params a <code>List</code> value, containing the
   * parameters to be passed when invoking the continuation. As
   * opposed to the parameters passed by <code>callFunction</code>,
   * these parameters will only become available in the language's
   * environment, if at all.
   *
   * @exception Exception if an error occurs
   */
  public void handleContinuation(String continuationId, List params)
    throws Exception;
}
