function getSectorsInRange(s, range) {
    var sg = tw1606u.getBean("sector-graph");
    return sg.getSectorsWithinRange(s, range);
}

function getShortestPath(start, end) {
    var sg = tw1606u.getBean("sector-graph");
    return sg.shortestPath(start, end);;
}

function distanceBetweenSectors(start, end) {
    var sg = tw1606u.getBean("sector-graph");
    try {
        lst = sg.shortestPath(start, end);
    } catch (e) {
        log.error("Can't get shortest path from "+start+" to "+end, e);
    }
    if (lst != null) {
        return lst.size();
    } else {
        return -1;
    }
}

function checkPortForBusts(port) {
	if (port.getLastBust() != null) {
		if (confirm("Port "+port.getId()+" is listed as busted.  Do you want to clear this bust?", "Bust Clear Confirmation")) {
			port.setLastBust(null);
			portDao.update(port);
			return true;
		} else {
			return false;
		}
	} else {
		return true;
	}
}

function findBestSSMPair(sector) {
	var port = sector.port;
	var pairSector;
	for (x in sector.getWarps()) {
		var warp = sector.getWarps()[x];
		if (warp.getPort() != null && port.buysProduct(Port.EQUIPMENT) &&  
			(port.getLastBust() == null || (port.getLastBust() != null && !pairSector))) {
			pairSector = warp;
		}
	}
	return pairSector;
}

function findBestTradingPair(sector) {
	port = sector.getPort();
	max = -2;
	maxSector = null;
	for (x in sector.getWarps()) {
		warp = sector.getWarps()[x];
		if (warp.getPort() != null && port.isPairTradeable(warp.getPort())) {
			val = port.getMaxTradeable(warp.getPort());
			if (val > max || ((val > 500 || val == -1) && 
						      port.determineUntradeable(maxSector.getPort()) == Port.EQUIPMENT && 
							  port.determineUntradeable(warp.getPort()) != Port.EQUIPMENT)) {
				max = val;
				maxSector = warp;
			}
		}
	}
	if (max == -2) 
		return null;
	else
		return maxSector;
}
