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

import java.util.ArrayList;
import java.util.List;


/**
 * Representation of continuations in a Web environment.
 *
 * <p>Because a user may click on the back button of the browser and
 * restart a saved computation in a continuation, each
 * <code>Continuation</code> becomes the parent of a subtree of
 * continuations.
 *
 * <p>If there is no parent <code>Continuation</code>, the created
 * continuation becomes the root of a tree of
 * <code>Continuation</code>s.
 *
 * @author <a href="mailto:ovidiu@cup.hp.com">Ovidiu Predescu</a>
 * @since March 19, 2002
 * @version CVS $Id: Continuation.java,v 1.1 2004/01/04 01:53:58 mrdon Exp $
 */
public class Continuation implements Comparable {

    /**
     * The continuation this object represents.
     */
    protected Object continuation;

    /**
     * The continuation id used to represent this instance in Web pages.
     */
    protected String id;

    /**
     * A user definable object. This is present for convenience, to
     * store any information associated with this
     * <code>Continuation</code> a particular implementation might
     * need.
     */
    protected Object userObject;

    /**
     * When was this continuation accessed last time. Each time the
     * continuation is accessed, this time is set to the time of the
     * access.
     */
    protected long lastAccessTime;

    /**
     * Create a <code>Continuation</code> object. Saves the object in
     * the hash table of continuations maintained by
     * <code>manager</code> (this is done as a side effect of obtaining
     * and identifier from it).
     *
     * @param continuation an <code>Object</code> value
     * @param parentContinuation a <code>Continuation</code> value
     * @param timeToLive time this continuation should live
     * @param disposer a <code>ContinuationsDisposer</code> to call when this
     * continuation gets invalidated.
     */
    Continuation(String id,
                    Object continuation) {
        this.id = id;
        this.continuation = continuation;
        this.updateLastAccessTime();
    }

    /**
     * Return the continuation object.
     *
     * @return an <code>Object</code> value
     */
    public Object getContinuation() {
        updateLastAccessTime();
        return continuation;
    }

    /**
     * Return the ancestor continuation situated <code>level</code>s
     * above the current continuation. The current instance is
     * considered to be at level 0. The parent continuation of the
     * receiving instance at level 1, its parent is at level 2 relative
     * to the receiving instance. If <code>level</code> is bigger than
     * the depth of the tree, the root of the tree is returned.
     *
     * @param level an <code>int</code> value
     * @return a <code>Continuation</code> value
     */
    public Continuation getContinuation(int level) {
            updateLastAccessTime();
            return this;
    }

    /**
     * Returns the string identifier of this
     * <code>Continuation</code>.
     *
     * @return a <code>String</code> value
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the last time this
     * <code>Continuation</code> was accessed.
     *
     * @return a <code>long</code> value
     */
    public long getLastAccessTime() {
        return lastAccessTime;
    }

    /**
     * Sets the user object associated with this instance.
     *
     * @param obj an <code>Object</code> value
     */
    public void setUserObject(Object obj) {
        this.userObject = obj;
    }

    /**
     * Obtains the user object associated with this instance.
     *
     * @return an <code>Object</code> value
     */
    public Object getUserObject() {
        return userObject;
    }

    /**
     * Returns the hash code of the associated identifier.
     *
     * @return an <code>int</code> value
     */
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * True if the identifiers are the same, false otherwise.
     *
     * @param another an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    public boolean equals(Object another) {
        if (another instanceof Continuation)
            return id.equals(((Continuation) another).id);
        return false;
    }

    /**
     * Compares the expiration time of this instance with that of the
     * Continuation passed as argument.
     *
     * <p><b>Note:</b> this class has a natural ordering that is
     * inconsistent with <code>equals</code>.</p>.
     *
     * @param other an <code>Object</code> value, which should be a
     * <code>Continuation</code> instance
     * @return an <code>int</code> value
     */
    public int compareTo(Object other) {
        Continuation wk = (Continuation) other;
        return (int) ((lastAccessTime)
                - (wk.lastAccessTime));
    }

    /**
     * Update the continuation in the
     */
    protected void updateLastAccessTime() {
        lastAccessTime = System.currentTimeMillis();
    }

}
