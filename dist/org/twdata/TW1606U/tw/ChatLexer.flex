   
package org.twdata.TW1606U.tw;
import java.io.PipedInputStream;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.signal.*;
import org.twdata.TW1606U.signal.*;
import org.twdata.TW1606U.tw.model.*;
import org.apache.log4j.Logger;
import org.twdata.TW1606U.Lexer;
import java.util.regex.*;

%%
    
%public
%class ChatLexer
%implements Lexer
%unicode
%integer

%{
   private PlayerDao playerDao;
   private TWSession session;
   private MessageBus bus;
   private Logger log;
   
   private Player privTarget; 
    
    public void setPlayerDao(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }  
    
    public void setMessageBus(MessageBus bus) {
        this.bus = bus;
    }
    
    public void setSession(TWSession session) {
      this.session = session;
    }
   
   public void init()
   {
       log = Logger.getLogger(getClass());
       log.info("Initializing chat lexer debug:"+log.isDebugEnabled());
       yybegin(YYINITIAL);
   }   
   
   public void setState(int state) {
     yybegin(state);
   }
   
   public int getState() {
    return yystate();
   } 
   
%}
   
%pack

%%

^("'"|"`").* { 
    int type = ChatSignal.SUBSPACE;
    if (yytext().charAt(0) == '`') {
        type = ChatSignal.FEDCOM;
    }
    String msg = yytext().substring(1, yytext().length()-1);
    bus.broadcast(new ChatSignal(type, session.getTrader(), null, msg));
    
    if (log.isDebugEnabled()) {
      log.debug("chat message from you - type:"+type+" msg:"+msg);
    }
}

^"Requesting comm-link with ".* {
  String name = yytext().substring(26, yytext().length() -2);
  if (log.isDebugEnabled()) {
    log.debug("determined sending msg to "+name);
  }
  privTarget = playerDao.get(name, Player.TRADER, true);
} 

^"P: ".* {
    if (yytext().length() > 4)
    {
        String txt = yytext().substring(3, yytext().length() -2);
        if (log.isDebugEnabled()) {
          log.debug("sending priv to "+privTarget.getName()+" msg:"+txt);
        }
        bus.broadcast(new ChatSignal(ChatSignal.PRIV, session.getTrader(), privTarget, txt));
    }
}

^[PFR]" ".* {
  char typeChar = yytext().charAt(0);
  int type = -1;
  switch (typeChar) {
    case 'P' : type = ChatSignal.PRIV; break;
    case 'F' : type = ChatSignal.FEDCOM; break;
    case 'R' : type = ChatSignal.SUBSPACE; break;
  }
  String txt = yytext().substring(2, yytext().length() - 1);
  
  Pattern ptn = Pattern.compile("[ ]{2,}");
  Matcher m = ptn.matcher(txt);
  if (m.find()) {
    String name = txt.substring(0, m.start());
    String msg = txt.substring(m.end());
    Player p = playerDao.get(name, Player.TRADER, true);
    if (log.isDebugEnabled()) {
      log.debug("Receiving message - type:"+type+" from:"+name+" msg:"+msg);
    }
    bus.broadcast(new ChatSignal(type, p, session.getTrader(), msg));
  } else {
    log.warn("Unknown input:"+txt);
  }
}

.|\n {}
