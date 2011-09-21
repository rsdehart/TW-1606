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

import java.security.SecureRandom;
import java.util.*;

/**
 * The default implementation of {@link ContinuationsManager}.
 *
 * @author <a href="mailto:ovidiu@cup.hp.com">Ovidiu Predescu</a>
 * @author <a href="mailto:Michael.Melhem@managesoft.com">Michael Melhem</a>
 * @since March 19, 2002
 * @see ContinuationsManager
 * @version CVS $Id: ContinuationsManagerImpl.java,v 1.1 2004/01/04 01:53:58 mrdon Exp $
 */
public class ContinuationsManagerImpl implements ContinuationsManager {

    static final int CONTINUATION_ID_LENGTH = 20;

    protected SecureRandom random = null;
    protected byte[] bytes;

    /**
     * Association between <code>Continuation</code> ids and the
     * corresponding <code>Continuation</code> object.
     */
    protected Map idToCont = Collections.synchronizedMap(new HashMap());

    public ContinuationsManagerImpl() throws Exception {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        }
        catch(java.security.NoSuchAlgorithmException nsae) {
            // maybe we are on IBM's SDK
            random = SecureRandom.getInstance("IBMSecureRandom");
        }
        random.setSeed(System.currentTimeMillis());
        bytes = new byte[CONTINUATION_ID_LENGTH];
    }

    public Continuation createContinuation(Object kont) {

        Continuation wk = generateContinuation(kont, null);

        return wk;
    }

    public void removeContinuation(Continuation wk) {
        idToCont.remove(wk.getId());
    }

    public Continuation lookupContinuation(String id) {
        // REVISIT: Is the folliwing check needed to avoid threading issues:
        // return wk only if !(wk.hasExpired) ?
        return (Continuation) idToCont.get(id);
    }

    /**
     * Create <code>Continuation</code> and generate unique identifier
     * for it. The identifier is generated using a cryptographically strong
     * algorithm to prevent people to generate their own identifiers.
     *
     * <p>It has the side effect of interning the continuation object in
     * the <code>idToCont</code> hash table.
     *
     * @param kont an <code>Object</code> value representing continuation
     * @param parent value representing parent <code>Continuation</code>
     * @param ttl <code>Continuation</code> time to live
     * @param disposer <code>ContinuationsDisposer</code> instance to use for
     * cleanup of the continuation.
     * @return the generated <code>Continuation</code> with unique identifier
     */
    private Continuation generateContinuation( Object kont, 
                                                  Continuation parent) {

        char[] result = new char[bytes.length * 2];
        Continuation wk = null;

        while (true) {
            random.nextBytes(bytes);

            for (int i = 0; i < CONTINUATION_ID_LENGTH; i++) {
                byte ch = bytes[i];
                result[2 * i] = Character.forDigit(Math.abs(ch >> 4), 16);
                result[2 * i + 1] = Character.forDigit(Math.abs(ch & 0x0f), 16);
            }

            String id = new String(result);
            synchronized (idToCont) {
                if (!idToCont.containsKey(id)) {
                    wk = new Continuation(id, kont);
                    idToCont.put(id, wk);
                    break;
                }
            }
        }

        return wk;
    }

    /**
     * Dump to Log file all <code>Continuation</code>s
     * in the system
     */
    public Collection getAllContinuations() {
        return idToCont.values();
    }

}
