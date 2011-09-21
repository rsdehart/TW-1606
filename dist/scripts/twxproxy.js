kokua.load("/scripts/tw-basic.js");

function stopAllScripts() {
	send("$z");
}

function move(sector, game) {
	var gameSize = 20000;
	if (game)
		gameSize = parseInt(game.getGameSettings(Game.MAX_SECTORS));
	
	var noReturn = false;	 
	if (gameSize >= 10000) {
		if (sector.id >= 10000) noReturn = true;
	} else if (gameSize >= 1000) {
		if (sector.id >= 1000) noReturn = true;
	} else if (gameSize >= 100) {
		if (sector.id >= 100) noReturn = true;
	}
	send(sector.getId()+(noReturn ? "" : "*"));
}

function trade(trader, sector, warp) {
	port = sector.getPort();
	if (port == null)
		alert("The current sector must have a port");
	else {
		max = port.getMaxTradeable(warp.getPort());
		times = 10;
		
		// try to determine the most number of times if possible
		if (max > 0) {
			if (trader.getCurShip() != null) {
				holds = trader.getCurShip().getHolds();
				if (holds > 0)
					times = max / holds;
			}
		}
		
		send("$ss1_Port.ts\n"+warp.getId()+"\n"+times+"\n5\n");
	}			
}

function ssm(sector, pairSector) {
	port = sector.getPort();
	if (port == null || !port.buysProduct(Port.EQUIPMENT)) {
		alert("The current sector must have a port and buy equipment");
	} else if (checkPortForBusts(port)) {
		if (checkPortForBusts(pairSector.getPort())) {
			send("$ss1_SSM.ts\n30\n"+pairSector.getId()+"\n");
		}
	}
}

