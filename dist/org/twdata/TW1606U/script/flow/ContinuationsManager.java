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

import java.util.Collection;

/**
 * The interface of the Continuations manager.
 *
 * The continuation manager maintains a forrest of {@link
 * Continuation} trees. Each tree defines the flow of control for a
 * user within the application.
 *
 * A <code>Continuation</code> is created for a continuation object
 * from the scripting language used. A continuation object in the
 * implementation of the scripting language is an opaque object
 * here. It is only stored inside the <code>Continuation</code>,
 * without being interpreted in any way.
 *
 * @author <a href="mailto:ovidiu@cup.hp.com">Ovidiu Predescu</a>
 * @since March 19, 2002
 * @see Continuation
 * @version CVS $Id: ContinuationsManager.java,v 1.1 2004/01/04 01:53:58 mrdon Exp $
 */
public interface ContinuationsManager {

    /**
     * Create a <code>Continuation</code> object given a native
     * continuation object and its parent. If the parent continuation is
     * null, the <code>Continuation</code> returned becomes the root
     * of a tree in the forrest.
     *
     * @param kont an <code>Object</code> value
     * @return a <code>Continuation</code> value
     * @see Continuation
     */
    public Continuation createContinuation(Object kont);

    /**
     * Invalidates a <code>Continuation</code>. This effectively
     * means that the continuation object associated with it will no
     * longer be accessible from Web pages. Invalidating a
     * <code>Continuation</code> invalidates all the
     * <code>Continuation</code>s which are children of it.
     *
     * @param k a <code>Continuation</code> value
     */
    public void removeContinuation(Continuation k);

    /**
     * Given a <code>Continuation</code> id, retrieve the associated
     * <code>Continuation</code> object.
     *
     * @param id a <code>String</code> value
     * @return a <code>Continuation</code> object, or null if no such
     * <code>Continuation</code> could be found.
     */
    public Continuation lookupContinuation(String id);

    /**
     * Prints debug information about all web continuations into the log file.
     * @see Continuation#display()
     */
    public Collection getAllContinuations();
}
