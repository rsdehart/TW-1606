   
package org.twdata.TW1606U.tw;
import java.io.PipedInputStream;
import org.twdata.TW1606U.tw.data.*;
import org.twdata.TW1606U.data.*;
import org.twdata.TW1606U.tw.model.*;
import org.apache.log4j.Logger;
import org.twdata.TW1606U.Lexer;
import org.twdata.TW1606U.StreamReader;

%%
    
%public
%class TWLexer
%implements Lexer
%unicode
%integer

%{
   private boolean firstCommand = true; 

   private PopulationParser popParser;
   private TradeParser tradeParser;
   private SectorDisplayParser sectorDisplayParser;
   private StatusParser statusParser;
   private ScanParser scanParser;
   private ComputerParser computerParser;
   private CimParser cimParser;
   private PlanetParser planetParser;
   private StreamReader streamReader;
   private ShipTypeDao shipTypeDao;
   private Logger log;

    public void setPopulationParser(PopulationParser pop) {
        this.popParser = pop;
    }
    
    public void setStreamReader(StreamReader sr) {
        this.streamReader = sr;
    }  
   
    public void setTradeParser(TradeParser tp) {
        tradeParser = tp;
    }
    
    public void setComputerParser(ComputerParser cp) {
        this.computerParser = cp;
    }    
    
    public void setShipTypeDao(ShipTypeDao dao) {
      this.shipTypeDao = dao;
    }
    
    public void setPlanetParser(PlanetParser pp) {
        planetParser = pp;
    }
    
    public void setCimParser(CimParser tp) {
        cimParser = tp;
    }
    
    public void setSectorDisplayParser(SectorDisplayParser sd) {
        sectorDisplayParser = sd;
    }
    
    public void setStatusParser(StatusParser sp) {
        statusParser = sp;
    }
    
    public void setScanParser(ScanParser sp) {
        scanParser = sp;
    }

    public void setFirstCommand(boolean first) {
        this.firstCommand = first;
    }    

   public void init()
   {
       log = Logger.getLogger(getClass());
       log.info("Initializing lexer debug:"+log.isDebugEnabled());
       setState(DUMMYSTATE);
   }   
   
   public void setState(int state) {
     yybegin(state);
     if (state != DUMMYSTATE) {
      setFirstCommand(false);
     } 
   }
   
   public int getState() {
    return yystate();
   } 
   
%}
   
%pack

%state COMMERCEREPORT
%state STATUS
%state SECTORDISPLAY
%state INFO
%state COMPUTER
%state SHIPCATALOG
%state DENSITYSCAN
%state DEPLOYEDFTRS
%state COURSEPLOT
%state CIMCOURSEPLOT
%state CIM
%state PLANETDISPLAY
%state PLANETPROMPT
%state STARDOCK
%state PLANETLAND
%state PLANETPOP

%state DUMMYSTATE

CHAR=[A-Za-z]
WORD=[0-9A-Za-z']+

DIGIT=[0-9]
NUMBER=[0-9]+(","[0-9]+)*
FLOAT={NUMBER}"."{NUMBER}
PORT_TYPE=(((S|B)(S|B)(S|B))|"Special")
TIME=({DIGIT}{DIGIT})":"({DIGIT}{DIGIT})":"({DIGIT}{DIGIT}) 
CLOCK={TIME}" "{CHAR}{2}
DATE={CHAR}{3}" "{CHAR}{3}" "{DIGIT}{2}", "{DIGIT}{4}
TIMESTAMP={CLOCK}" "{DATE}
FUEL="Fuel Ore"
ORG="Organics"
EQU="Equipment"
COL="Colonists"
EMP="Empty"
ANYPRODUCT=({FUEL}|{ORG}|{EQU}|{COL}|{EMP})
RANK={BAD_RANK}|{GOOD_RANK}
NAME=([A-Za-z0-9"!""-"".""'"" ""*"])+

%%

<DUMMYSTATE> "==-- Trade Wars 2002 --=="  {
    log.debug("Starting initial state");
    yybegin(YYINITIAL);
}

//{{{ movement
<YYINITIAL> "Auto Warping to sector "{NUMBER} {
    statusParser.parseAutoWarpMove(yytext());    
}
<YYINITIAL> "Warping to Sector " {NUMBER} {
    statusParser.parseWarpMove(yytext());    
}
//}}}

//{{{ sector display
<YYINITIAL> "Sector  : " {NUMBER}.* {
    sectorDisplayParser.parseSectorNumber(yytext());
    log.debug("Starting sector display");
    yybegin(SECTORDISPLAY);
}

<SECTORDISPLAY> "Beacon  : ".* {
    sectorDisplayParser.parseBeacon(yytext());
}

<SECTORDISPLAY> "Ports   : ".* {
    sectorDisplayParser.parsePorts(yytext());
}

<SECTORDISPLAY>"NavHaz  : "{NUMBER} {
    sectorDisplayParser.parseNavHaz(yytext());
}

<SECTORDISPLAY> "Fighters: "{NUMBER}.* {
	sectorDisplayParser.parseFighters(yytext());
}

<SECTORDISPLAY> "Planets : ".* {
	sectorDisplayParser.parsePlanets(yytext());
}
    
<SECTORDISPLAY> "          (".* {
    sectorDisplayParser.parsePlanets(yytext());
}
    
<SECTORDISPLAY> "          <<<< (".* {
    sectorDisplayParser.parsePlanets(yytext());
}      
    
<SECTORDISPLAY>"Warps to Sector(s) :".* {
    sectorDisplayParser.parseWarps(yytext());
}

<SECTORDISPLAY> ^\r {
    log.debug("Ending sector display");
    yybegin(YYINITIAL);
}//}}}

//{{{ ether probe

<YYINITIAL> "Probe entering sector : " {NUMBER} {
  log.debug("starting ether probing");
  scanParser.parseEtherProbe(yytext());
}

<YYINITIAL> "Probe Self Destructs"|"Probe Destroyed" {
  log.debug("end ether probing");
  scanParser.endEtherProbe();
}

//}}}

//{{{ planetpop


<YYINITIAL> "There are currently "{NUMBER}" colonists ready to leave Terra." {
    String str = popParser.parseTerra(yytext());
    if (str=="TA") {
      streamReader.write("T^\n");
    }
}


<YYINITIAL> "Land on which planet <Q to abort> ?" {
    log.debug("Planet Landing");
    String which = popParser.LandingOnWhich(yytext());
    if (which!=""){
        streamReader.write(which);
    }

}

<YYINITIAL> "The Colonists file aboard your ship, eager to head out to new frontiers." {
  String response = popParser.ReturnHome();
  if (response!="") {
    streamReader.write(response);
  }

}

<YYINITIAL> "Do you want to engage the TransWarp drive?" {
  String response = popParser.Transwarp(yytext());
  if (response=="Y"){
    streamReader.write("Y");
  }
}

<YYINITIAL> "All Systems Ready, shall we engage?" {
  String response = popParser.Transwarp(yytext());
  if (response=="Y"){
    streamReader.write("Y");
  }
}

<YYINITIAL> "Planet #"{NUMBER}" in sector "{NUMBER}": "{NAME} {
    popParser.Landing(yytext());

}

<YYINITIAL> "Planet command (?=help) [D]" {
  String response = popParser.Landed(yytext());
  if (response!=""){
    streamReader.write(response);
  }
}

<YYINITIAL> "The shortest path ("{NUMBER}" hops, "{NUMBER}" turns) from sector "{NUMBER}" to sector "{NUMBER}" is:" {
    String response = popParser.ShortestPath(yytext());
    if (response!=""){
      streamReader.write(response);
    }
}


//}}}
  
//{{{ commerce report 
<YYINITIAL> "merce report for " {
    tradeParser.reset();
    log.debug("Starting commerce report");
	yybegin(COMMERCEREPORT);

}



<COMMERCEREPORT> .*(" just left"|" docked ").* {
    tradeParser.parseLastDocked(yytext());
}    

<COMMERCEREPORT> ({FUEL}|{ORG}|{EQU}).* {
    tradeParser.parseReportLine(yytext());
}

<COMMERCEREPORT> "You have "{NUMBER}" credits and "{NUMBER}" empty cargo holds" {
    tradeParser.parseCreditsAndHolds(yytext());
}

<COMMERCEREPORT> "We'll buy them for "{NUMBER}" credits." {
    String counter = tradeParser.parseBuy(yytext());
    if (counter!=""){
        streamReader.write(counter+"^\n");
    }
}

<COMMERCEREPORT> "We'll sell them for "{NUMBER}" credits." {
    String counter = tradeParser.parseSell(yytext());
    if (counter!=""){
        streamReader.write(counter+"^\n");
    }
}

<YYINITIAL> "fines you "{NUMBER}" Cargo Hold" {
    tradeParser.parseBusted(yytext());
}

//}}}
    
//{{{ status
<YYINITIAL> " Sect "{NUMBER}.* {
   statusParser.reset();
   statusParser.parseCompactStatus(yytext());
   log.debug("Starting status report");
   yybegin(STATUS);
}
   
<STATUS> ^" ".* {
    String l = yytext();
    log.debug("parsing line:"+l+":"+l.length());
    if (l.length() < 5) {
        StringBuffer sb = new StringBuffer();
        for (int x=0; x<yytext().length(); x++) {
            sb.append("code "+(int)yytext().charAt(x)+":");
        }
        log.debug("code: "+sb.toString());
    }
            
    statusParser.parseCompactStatus(yytext());
}
    
<STATUS> ^\r {
    log.debug("Ending status report");
    yybegin(YYINITIAL);
}       
//}}}

//{{{ Info
<YYINITIAL> "Trader Name    :".* {
    statusParser.reset();
    statusParser.parseInfoLine(yytext());
    log.debug("Starting info report");
    yybegin(INFO);
}
    
<INFO> "Credits        : ".* {
    statusParser.parseInfoLine(yytext());
    yybegin(YYINITIAL);    
}
    
<INFO> [A-Za-z" "]+[:#]" ".* {
    statusParser.parseInfoLine(yytext());
}
    
//}}}

//{{{ keepAlive
<YYINITIAL>"Your session will be terminated" {
    log.debug("Session Timeout received - Sending keepalive");
    streamReader.write(" ");
}
//}}}

//{{{ scans
<YYINITIAL>"Relative Density Scan" {
    log.debug("Starting density scan");
	yybegin(DENSITYSCAN);
}
    
<DENSITYSCAN> "Sector ".* {
    scanParser.parseDensityScan(yytext());
}    

<DENSITYSCAN> ^\n {
    log.debug("Ending density scan");
	yybegin(YYINITIAL);
}
 
<YYINITIAL> "Sector "{NUMBER}" has warps to sector(s) :".* {
    scanParser.parseSectorWarps(yytext());
}   
    
<YYINITIAL> "oyed  Fighter  Sc" {
    log.debug("Starting deployed fighters");
	yybegin(DEPLOYEDFTRS);
}

<DEPLOYEDFTRS> {NUMBER}.* {
	scanParser.parseDeployedFighters(yytext());
}

//<DEPLOYEDFTRS> ({NUMBER}|{NUMBER}{CHAR})" Total" {
//    log.debug("Ending deployed fighters");
//	yybegin(YYINITIAL);
//}
    
//}}}

//{{{ cim
<YYINITIAL> "The shortest path ("{NUMBER}" hops, "{NUMBER}" turns) from sector "{NUMBER}" to sector "{NUMBER}" is:" {
    // cimParser.parseComputerPlot(yytext());
    log.debug("Starting course plot");
    yybegin(COURSEPLOT);
}

<COURSEPLOT> ("("?{NUMBER}+")"?" > "?)+ {
    cimParser.parseCoursePlot(yytext());
    log.debug("Ending course plot");
    yybegin(YYINITIAL);
}

<YYINITIAL> ^": "\r {
    log.debug("Starting cim");
    yybegin(CIM);
}

<CIM> "TO > "{NUMBER}+ {
    yybegin(CIMCOURSEPLOT); 
}

<CIMCOURSEPLOT> ("("?{NUMBER}+")"?" > "?)+ {
    cimParser.parseCoursePlot(yytext());
    log.debug("Starting cim from course plot");
    yybegin(CIM);
}

<CIM> " "+{NUMBER}+(" "+{NUMBER}+)+ {
    cimParser.parseWarps(yytext());
}

<CIM> " "+{NUMBER}+([" ""-"]+{NUMBER}+" "+{NUMBER}+"%"){3} {
    cimParser.parsePort(yytext());
}

<CIM> ": ENDINTERROG" {
    log.debug("Ending cim");
    yybegin(YYINITIAL);
}
//}}}

//{{{ planet
<YYINITIAL> "Planet #"{NUMBER}" in sector "{NUMBER}": ".* {
    planetParser.parsePlanetDisplay(yytext());
//    sessionDao.update(session);

    log.debug("Starting planet display");
    yybegin(PLANETDISPLAY);
}

<PLANETDISPLAY> ("Fuel Ore"|"Organics"|"Equipment"|"Fighters") " "+ [0-9N].* {
    planetParser.parseProductDisplay(yytext());   
}

<PLANETDISPLAY> ^[A-Z].* {
    planetParser.parsePlanetDisplay(yytext());
}

"Planet command (?=help) [D] ?" {
    statusParser.parsePlanetPrompt(yytext());
    log.debug("Starting planet prompt");
    yybegin(PLANETPROMPT);
}

//}}}

//{{{ stardock
"<StarDock>" {
    statusParser.parseStardockPrompt(yytext());
    log.debug("Starting stardock");
	yybegin(STARDOCK);
}
//}}}

//{{{ computer
"Computer command [TL="{TIME}"]:["{NUMBER}"]" {
  if (log.isDebugEnabled()) {
    log.debug("Starting computer state");
  }
  yybegin(COMPUTER);  
}

<COMPUTER>"Which ship are you interested" {
  if (log.isDebugEnabled()) {
    log.debug("Starting ship catalog");
  }
  yybegin(SHIPCATALOG);  
}

<SHIPCATALOG>"<"[A-PR-Z]">".* {
  computerParser.parseShipEntry(yytext());
}
//}}}

//{{{ global events
"you receive "{NUMBER}" experience point(s)." {   
    statusParser.parseGainExperience(yytext());
}

"and you LOSE "{NUMBER}" experience point(s)." {            
    statusParser.parseLoseExperience(yytext());
}

"your alignment went " ("up"|"down") " by "{NUMBER}" point" {   
    statusParser.parseChangeAlignment(yytext());
}


", "{NUMBER}" turn"("s")?" left" {
    statusParser.parseTurnsLeft(yytext());
}
//}}}

"Command [TL="{TIME}"]:["{NUMBER}"]".*  {
    log.debug("At command prompt");
    
    // Initialize the session on first command prompt
    if (firstCommand) {
      if (shipTypeDao.getAll().size() == 0) {
        streamReader.write("CC?+QQ".getBytes("Cp1252"));
      }
      streamReader.write("TDRQ");
      streamReader.write("I".getBytes("Cp1252"));
      streamReader.write("/#".getBytes("Cp1252"));
      streamReader.write("V".getBytes("Cp1252"));
      // streamReader.write("CTQ".getBytes("Cp1252"));
      firstCommand = false;
    }

    statusParser.parseCommandPrompt(yytext());
    String response = popParser.parseCommandPrompt(yytext());
    if (response!=""){
        streamReader.write(response);
    }

    yybegin(YYINITIAL);
}


.|\n {}
