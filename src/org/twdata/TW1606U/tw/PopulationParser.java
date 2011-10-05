package org.twdata.TW1606U.tw;

import java.util.*;
import java.util.regex.*;
import org.twdata.TW1606U.tw.model.*;

public class PopulationParser extends AbstractParser {

    private Pattern cmdPtn;
    private Pattern hopsPtn = Pattern.compile("\\s+(\\d+) hops, (\\d+) \\s+ (\\d+) to sector (\\d+) is:");
    private Pattern productPtn = Pattern.compile("[0-9,]+|N/A");
    private Pattern terraPtn = Pattern.compile("There are currently (\\d+) colonists ready to leave Terra.");
    private int disPos;
    private int prodPos;
    private Planet planet;
    private PlanetType planetType;
    private Ship ship;

    private Planet popPlanet;
    private Planet terra;
    private Integer colosOnTerra;

    private Sector[] mysectors;

    public boolean scanning;
    public boolean useTwarp;
    public boolean useFromPlanetTwarp;
    public boolean useToPlanetTwarp;
    public boolean fuelFromToPlanet;
    public String fuelFrom;

    public int fromSector;
    public int toSector;
    public String fromRes;
    public String toRes;
    public int fromPlanet;
    public int toPlanet;
    private String lastLanded;
    private String direction;
    public int hops = 0;
    public int fuelPerHop=3;

    public String curMode="";

    public void startParsing(){
        scanning=true;
    }

    public void stopParsing(){
        scanning=false;
    }

    public void setPlanet(String sector,String number){
        popPlanet = null;

    }

    public PopulationParser() {
        super();
        scanning=true;
        fromSector = 1;
        fromPlanet = 1;
        toSector = 212;
        toPlanet=2;
        useToPlanetTwarp=true;
        useTwarp=true;
        fuelFrom = "212.2";

        fromRes = "1";
        toRes = "2";
        direction = "from";
        cmdPtn = Pattern.compile("Command \\[TL=(\\d\\d):(\\d\\d):(\\d\\d)\\]:\\[(\\d+)\\]");

    }

    public String LandingOnWhich(String line) {
        String response="";
        curMode="Landing";
        if (scanning) {
            if (session.getSector().getId()==toSector) {
                response = Integer.toString(toPlanet);
            }
            else if (session.getSector().getId()==fromSector) {
                response = Integer.toString(fromPlanet);
            }

        }
        return response;
    }

    public void Landing(String line) {
        
    }



    public String Landed(String line) {
        String response = "";
        if (curMode=="Landing") {
            curMode="Landed";}
        if (scanning) {
            if (curMode=="Fueled") {
                if (useToPlanetTwarp) {
                    int destination;
                    if (onToPlanet()){
                        destination=fromSector;
                    }
                    else
                    {
                        destination=toSector;
                    }
                    response="CB"+destination;
                }
                else
                {
                    response="Q";
                }
            }else
            {
                lastLanded = Integer.toString(session.getSector().getId())+"."+Integer.toString(session.getPlanet().getTWId());
                if (onToPlanet() && curMode=="Landed") {
                   response=Populate();
                }else
                 if (onToPlanet() && session.getTrader().getCurShip().getHoldContents(3)==0) {
                   response=FuelUp();
                 }else
                 if (lastLanded==Integer.toString(toSector)+"."+Integer.toString(toPlanet)) {
                   response=GrabResources();
                }
            }
        }
        return response;
    }

    public String FuelUp(){
        String response = "";
        int fuelneeded = (hops*fuelPerHop);
        curMode="Fueling";
        if (fuelFrom!="both" && fuelFrom!="bothPorts" && (useToPlanetTwarp || useFromPlanetTwarp))
            { fuelneeded *= 2; }

        if (fuelFrom=="both" || fuelFrom==session.getSector().getId()+"."+session.getPlanet().getTWId()) {
            response="TNT"+fuelneeded;
        }
        if (fuelFrom=="bothPorts" || fuelFrom=="P"+session.getSector().getId()) {
            response="QPT"+fuelneeded+"^\n^\n";
        }
        if (useToPlanetTwarp && useFromPlanetTwarp) {
            response="D";
        }
        curMode="Fueled";
        return response;
    }

    public String GrabResources(){
        String response = "";
        Planet curplanet = session.getPlanet();
        if (scanning && session.getSector().getId() == toSector && session.getPlanet().getTWId()==toPlanet) {
            response += "SNT"+fromRes+"^\n";
            direction = "from";
        }
        return response;
    }

    public String Populate() {
        String response = "";
        Planet curplanet = session.getPlanet();

        if (scanning && session.getSector().getId() == toSector && session.getPlanet().getTWId()==toPlanet) {
            response= "SNL"+toRes+"^\n";
            direction = "from";
        }
        return response;
    }

    public String ReturnHome() {
        String response = "";
        if (scanning){
            response = Integer.toString(mysectors[1].getId())+"^\n";
        }
        return response;

    }




    public String Transwarp(String line) {
        if (scanning && useTwarp) {
            return "Y";
        }
        else
        {
            return "";
        }
    }

    public String parseTerra(String line) {
        Matcher m = terraPtn.matcher(line);

        if (m.find()) {
            colosOnTerra=parseInt(m.group(1));

        }

        if (scanning) {
            direction="to";
            lastLanded="1.1";
            
            return "TA";

        }
        else
        {
            return "";
        }

    }

    public void parseLanding(String line) {

    }

    public String ShortestPath(String line) {
        String response = "";
        if (curMode == null ? "figureHops" == null : curMode.equals("figureHops")){
            Matcher m = hopsPtn.matcher(line);
            if (curMode=="figureHops" && scanning) {
                if (m.find()) {hops=parseInt(m.group(1));}
                return "N";
            }
            curMode="Ready";
            response="N";
        }
        return response;
    }

    public String parseCommandPrompt(String line) {
        String response = "";
        if (scanning) {
            Sector currentSector = session.getSector();
            if (curMode==""){
                curMode = "figureHops";
                if (currentSector.getId()==fromSector ){
                    response=toSector+"^\nN";
                }else
                 if (currentSector.getId()==toSector) {
                    response=fromSector+"^\nN";
                 }
                 else
                 {

                 }
            }

            int sectorID = currentSector.getId();
            if ((currentSector.getId()==fromSector && direction=="from")||
                    (currentSector.getId()==toSector) && direction=="to"){
                response= "L";
            }
            else
            {
                if (direction=="from" && onFromPlanet()) {
                    direction="to";
                    response=Integer.toString(toSector)+"^\n";
                    
                }
                if (direction=="to" && onToPlanet()) {
                        direction="from";
                        response=Integer.toString(fromSector)+"^\n";
                    
                }
            
            }

        }
        return response;
    }

     public boolean onToPlanet() {
        return lastLanded==Integer.toString(toSector)+"."+Integer.toString(toPlanet);
    }


     public boolean onFromPlanet() {
        return lastLanded==Integer.toString(fromSector)+"."+Integer.toString(fromPlanet);
    }
}
