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

 4. The names "Apache Kokua" and  "Apache Software Foundation" must  not  be
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
package org.twdata.TW1606U.script.flow.javascript;

import org.twdata.TW1606U.script.flow.ContinuationsManager;
import org.twdata.TW1606U.script.flow.Continuation;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.springframework.beans.factory.*;

/**
 *
 * @version CVS $Id: JSContinuation.java,v 1.1 2004/01/04 01:53:58 mrdon Exp $
 */
public class JSContinuation extends ScriptableObject
{
  protected JSTW1606u tw1606u;
  protected Continuation wk;
  protected ContinuationsManager continuationsMgr;

  public JSContinuation() {}

  public String getClassName()
  {
    return "ContinuationWrapper";
  }

  public JSTW1606u getJSTW1606u()
  {
    return tw1606u;
  }

  public Continuation getContinuation()
  {
    return wk;
  }

  public static Scriptable jsConstructor(Context cx, Object[] args,
                                         Function ctorObj, 
                                         boolean inNewExpr)
    throws Exception
  {
    JSTW1606u tw1606u = (JSTW1606u)args[0];
    BeanFactory factory = (BeanFactory)args[1];
    ContinuationsManager contMgr
      = (ContinuationsManager)factory.getBean("continuations-manager");

    Object kont = args[1];

    JSContinuation jswk = new JSContinuation();
    Continuation wk
      = contMgr.createContinuation(kont);
    wk.setUserObject(jswk);

    jswk.tw1606u = tw1606u;
    jswk.wk = wk;
    jswk.continuationsMgr = contMgr;

    return jswk;
  }

  public String jsGet_id()
  {
    return wk.getId();
  }

  public Object jsGet_continuation()
  {
    return wk.getContinuation();
  }

  public void jsFunction_remove()
  {
    continuationsMgr.removeContinuation(wk);
  }

}
