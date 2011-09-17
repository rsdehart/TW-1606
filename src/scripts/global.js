//
// CVS $Id: global.js,v 1.10 2004/12/02 06:18:23 mrdon Exp $
//
// JavaScript definitions
//
// Author: Ovidiu Predescu <ovidiu@apache.org>
// Date: March 19, 2002
//

var suicide = new Continuation();

importPackage(Packages.org.twdata.TW1606.tw.model);
importPackage(Packages.org.twdata.TW1606.tw.data);
importPackage(Packages.org.twdata.TW1606.tw.signal);
importPackage(Packages.org.twdata.TW1606.signal);
importPackage(Packages.org.twdata.TW1606.data);
importPackage(Packages.org.twdata.TW1606.gui);
importPackage(Packages.org.werx.framework.bus);
importPackage(Packages.javax.swing);
importPackage(Packages.java.awt);

function waitFor() {
    var txt = arguments[0];
    var timeout = arguments[1];
    var wholeLine = arguments[2];
    if (!wholeLine) wholeLine = false;
    if (!timeout) timeout = 10;
    
    var res = tw1606.textEvents.waitForString(txt, timeout, wholeLine);
    //if (!res) {
    //    suicide();
    //} else {
        return res;
    //}
}

function getLastLine() {
    return new String(tw1606.textEvents.getLastLine());
}

function send(txt) {
    tw1606.send(txt);
}

function waitMux() {
    var arr = arguments[0];
    var timeout = arguments[1];
    var wholeLine = arguments[2];
    if (!wholeLine) wholeLine = true;
    
    return tw1606.textEvents.waitMux(arr, timeout, wholeLine);
}

function alert(msg) {
    var view = tw1606.getBean("view-frame");
    JOptionPane.showMessageDialog(view, msg);
}

function confirm(msg, title) {
	if (!title)
		title = msg;
    var view = tw1606.getBean("view-frame");
    opt = JOptionPane.showConfirmDialog(view, msg, title, JOptionPane.OK_CANCEL_OPTION);
    return (opt == JOptionPane.OK_OPTION);
}

function prompt(msg, defValue) {
	if (!defValue)
		defValue = "";
    var view = tw1606.getBean("view-frame");
    return JOptionPane.showInputDialog(view, msg, defValue);
}

function showTable(title, data, titles, sortIndex) {
    var view = tw1606.getBean("view-frame");
    frame = new JFrame(title);
    if (sortIndex == null) {
        table = new SortedTable(data, titles);
    } else {
        table = new SortedTable(data, titles, sortIndex);
    }
    table.setAutoResizeMode(table.AUTO_RESIZE_ALL_COLUMNS);
    table.doLayout();
    scroll = new JScrollPane(table);
    frame.getContentPane().add(scroll);
    frame.pack();
    d = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation((d.width-frame.width)/2, (d.height-frame.height)/2);
    frame.show();
} 


function sendPageAndWait(uri, bizData, timeToLive)
{
  var kont = _sendPageAndWait(uri, bizData, timeToLive);
  return kont;
}

function _sendPageAndWait(uri, bizData, timeToLive)
{
  var k = new Continuation();
  var kont = new ContinuationWrapper(tw1606, k);
  // do stuff
  suicide();
}

function sendPageAndContinue(uri, bizData)
{
    log.error("Deprecated: Please use sendPage instead");
}

function sendPage(uri, bizData)
{
  //tw1606.forwardTo("tw1606://" + tw1606.environment.getURIPrefix() + uri,
  //                 bizData, null);
}

// This function is called to restart a previously saved continuation
// passed as argument.
function handleContinuation(kont)
{
  kont.continuation(kont);
}


