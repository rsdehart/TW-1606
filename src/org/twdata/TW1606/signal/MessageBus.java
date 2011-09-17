package org.twdata.TW1606.signal;

import org.werx.framework.bus.channels.channelmap.ChannelMap;
import org.werx.framework.bus.channels.identifiers.AddPluggable;
import org.werx.framework.bus.channels.identifiers.RemovePluggable;
import org.werx.framework.bus.signals.ResetBusSignal;
import org.werx.framework.commons.interfaces.IProcessor;
import org.werx.framework.commons.spinlock.SpinLock;
import java.util.HashMap;
import org.werx.framework.bus.channels.channelmap.processors.AddPluggableProcessor;
import org.werx.framework.bus.channels.channelmap.processors.ClassPropagation;
import org.werx.framework.bus.channels.channelmap.processors.InterfacePropagationProcessor;
import org.werx.framework.bus.channels.channelmap.processors.RemovePluggableProcessor;
import org.werx.framework.bus.channels.channelmap.processors.ResetBusProcessor;
import org.werx.framework.bus.channels.identifiers.AddPluggable;
import org.werx.framework.bus.channels.identifiers.RemovePluggable;
import org.werx.framework.bus.signals.ResetBusSignal;
import org.werx.framework.commons.interfaces.*;
import org.werx.framework.commons.processors.ProcessorList;
import org.werx.framework.commons.processors.ProcessorMap;
import java.util.*;


/**
 * WERX - Java Swing RAD Framework
 * Copyright 2003 Bradlee Johnson
 * Released under LGPL license
 *
 * The MessageBus is the heart of the framework. Objects "plug in"
 * to the bus. At that point reflection determines what channels the object
 * is listening on and registers it on those channels. When a signal is propagated
 * on that channel, all the objects on that channel are notified.
 * 
 * The MessageBus is a wrapper around a standard bus object. The bus object
 * is used in other subpackages that themseleves plug into this bus. I effect creating
 * a bus of buses.
 *
 * Imporant to note: channels are defined by message name parameters. For example, an
 * object listening for an AddJInternalFrameSignal would have a 
 * channel(AddJInternalFrameSignal) on it in 
 * order to receive that signal. The rest is automatic.
 * 
 *@author     Bradlee Johnson
 *@created    February 8,2004
 *@version    Final Release 1.0
 */
public class MessageBus
{
    private SpinLock processor;
    private final HashMap busMap = new HashMap();
    private ChannelMap channelMap;

    public MessageBus() {
        boolean isDaemon=false;
        channelMap = new ChannelMap("channel");
        processor  = new SpinLock("MessageBus", channelMap, isDaemon);
    }

	/**
     * broadcast ()
     *
     * broadcast method used by any object to propagate
     * a signal across the bus.
     *
     * @param  theMessageToSend  All signals must descend from IBusSignal
     *
     */
    public void broadcast(Object theMessageToSend)
    {
        broadcast(theMessageToSend, false);
    }
    
    public void broadcast(Object theMessageToSend, boolean synchronous) {
        if (synchronous) {
            channelMap.doProcess(theMessageToSend);
        } else {
            processor.doProcess(theMessageToSend);
        }
    }

	/**
	 * Unplug method to remove an object from the bus.
	 *
	 *@param  aComponent  The object to be removed from bus channels.
	 */
    public void unplug(Object aComponent)
    {
        processor.doProcess(new RemovePluggable(aComponent));
    }

	/**
	 * plug method to plug objects into bus channels. This uses
	 * reflection to figure out which channels the object is
	 * listenting on. Any object with a method starting with
	 * channelPrefix is added to that channel.
	 *
	 *@param  aComponent  The object to plug into the bus
	 */
    public void plug(Object aComponent)
    {
        processor.doProcess(new AddPluggable(aComponent));
    }

	/**
	 * resetbus ()
	 *
	 * Notifies the bus to get rid of all channels.
	 */
    public void resetbus()
    {
        processor.doProcess(new ResetBusSignal());
    }

    // resetbus ()
    public void stop()
    {
        channelMap.stop();
        processor.stop();
    }
    

    class ChannelMap implements IProcessor {
    
        private final ProcessorMap processors = new ProcessorMap();
    
        private final HashMap busMap = new HashMap();
    
        /**
         * The channel prefix passed in is what the map uses to determine what
         * method name part constitutes a channel. For example, the ReflectionBus
         * passes in "channel" but this could be set to some other value such as
         * "service" and this map would the look for methods with those names.
         * 
         * @param channelPrefix
         *            The prefix that specifies a channel name.
         */
        public ChannelMap(String channelPrefix) {
           
            ProcessorList list = new ProcessorList();
            list.add(new ClassPropagation(busMap));
            list.add(new InterfacePropagationProcessor(busMap));
            processors.setDefault(list);
    
            processors.add(ResetBusSignal.class, new ResetBusProcessor(busMap));
            processors.add(AddPluggable.class, new AddPluggableProcessor(channelPrefix, busMap));
            processors.add(RemovePluggable.class, new RemovePluggableProcessor(channelPrefix, busMap));
     
        }
    
        /**
         * Propagates the signal to the proper channel. Also propagates the generic
         * IBusSignal that all signals implement from if any listener is plugged
         * into that channel.
         * 
         * Future release will allow XML specification of either or processors.
         * 
         * @param theSignal
         *            The IBusSignal to propagate on the channel.
         */
        public final void doProcess(Object theSignal) {
    
                 processors.doProcess(theSignal);
    
        }
        
        public void stop() {
            Object o;
            for (Iterator i = busMap.values().iterator(); i.hasNext(); ) {
                o = i.next();
                if (o instanceof IStoppable) {
                    ((IStoppable)o).stop();
                } else {
                    System.err.println("Unable to stop "+o);
                }
                i.remove();
            }
        }
    }
}
