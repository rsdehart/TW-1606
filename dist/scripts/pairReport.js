function showTable(title, data, titles) {
    var view = kokua.getBean("view-frame");
    frame = new JFrame(title);
    table = new JTable(data, titles);
    table.doLayout();
    scroll = new JScrollPane(table);
    frame.getContentPane().add(scroll);
    frame.pack();
    frame.show();
}    

sectors = kokua.getBean("sectorDao").getAll().toArray();

var data = new Array();
var dataPos = -1;
for (x=0; x<sectors.length; x++) {
    s = sectors[x];
    p = s.port;
    if (p != null) {
        if (p.buysProduct(p.EQUIPMENT) && !p.buysProduct(p.ORGANICS)) {
            warps = s.warps;
            for (y=0; y<warps.length; y++) {
                wp = warps[y].port;
                if (wp != null) {
                    if (!wp.buysProduct(p.EQUIPMENT) && wp.buysProduct(wp.ORGANICS)) {
                        maxEquip = p.getCurProduct(p.EQUIPMENT);
                        maxOrg = p.getCurProduct(p.ORGANICS);
                        data[++dataPos] = new Array();
                        data[dataPos][0] = ""+s.id;
                        data[dataPos][1] = p.portClassName;
                        data[dataPos][2] = ""+wp.sector.id;
                        data[dataPos][3] = wp.portClassName;
                        data[dataPos][4] = maxEquip;
                        data[dataPos][5] = maxOrg;
                        data[dataPos][6] = Math.min(maxEquip, maxOrg);
                    }
                }    
            }
        }    
    }   
}

titles = ["From Sector", "From Port Type", "To Sector", "To Port Type", "Equipment", "Org", "Total Available"]
showTable("test", data, titles);
