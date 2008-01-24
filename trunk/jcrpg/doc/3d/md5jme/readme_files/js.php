var DOKU_BASE   = '/wiki/';var alertText   = 'Please enter the text you want to format.\nIt will be appended to the end of the document.';var notSavedYet = 'Unsaved changes will be lost.\nReally continue?';var reallyDel   = 'Really delete selected item(s)?';LANG = {"keepopen":"Keep window open on selection","hidedetails":"Hide Details"};


/* XXXXXXXXXX begin of /home/jmonke2/public_html/wiki/lib/scripts/events.js XXXXXXXXXX */

// written by Dean Edwards, 2005
// with input from Tino Zijdel

// http://dean.edwards.name/weblog/2005/10/add-event/

function addEvent(element, type, handler) {
    // assign each event handler a unique ID
    if (!handler.$$guid) handler.$$guid = addEvent.guid++;
    // create a hash table of event types for the element
    if (!element.events) element.events = {};
    // create a hash table of event handlers for each element/event pair
    var handlers = element.events[type];
    if (!handlers) {
        handlers = element.events[type] = {};
        // store the existing event handler (if there is one)
        if (element["on" + type]) {
            handlers[0] = element["on" + type];
        }
    }
    // store the event handler in the hash table
    handlers[handler.$$guid] = handler;
    // assign a global event handler to do all the work
    element["on" + type] = handleEvent;
};
// a counter used to create unique IDs
addEvent.guid = 1;

function removeEvent(element, type, handler) {
    // delete the event handler from the hash table
    if (element.events && element.events[type]) {
        delete element.events[type][handler.$$guid];
    }
};

function handleEvent(event) {
    var returnValue = true;
    // grab the event object (IE uses a global event object)
    event = event || fixEvent(window.event);
    // get a reference to the hash table of event handlers
    var handlers = this.events[event.type];
    // execute each event handler
    for (var i in handlers) {
        this.$$handleEvent = handlers[i];
        if (this.$$handleEvent(event) === false) {
            returnValue = false;
        }
    }
    return returnValue;
};

function fixEvent(event) {
    // add W3C standard event methods
    event.preventDefault = fixEvent.preventDefault;
    event.stopPropagation = fixEvent.stopPropagation;
    // fix target
    event.target = event.srcElement;
    return event;
};
fixEvent.preventDefault = function() {
    this.returnValue = false;
};
fixEvent.stopPropagation = function() {
    this.cancelBubble = true;
};


/**
 * Pseudo event handler to be fired after the DOM was parsed or
 * on window load at last.
 *
 * @author based upon some code by Dean Edwards
 * @author Dean Edwards
 * @link   http://dean.edwards.name/weblog/2006/06/again/
 */
window.fireoninit = function() {
  // quit if this function has already been called
  if (arguments.callee.done) return;
  // flag this function so we don't do the same thing twice
  arguments.callee.done = true;
  // kill the timer
  if (_timer) {
     clearInterval(_timer);
     _timer = null;
  }

  if (typeof window.oninit == 'function') {
        window.oninit();
  }
};

/**
 * Run the fireoninit function as soon as possible after
 * the DOM was loaded, using different methods for different
 * Browsers
 *
 * @author Dean Edwards
 * @link   http://dean.edwards.name/weblog/2006/06/again/
 */
  // for Mozilla
  if (document.addEventListener) {
    document.addEventListener("DOMContentLoaded", window.fireoninit, null);
  }

  // for Internet Explorer (using conditional comments)
  /*@cc_on @*/
  /*@if (@_win32)
    document.write("<scr" + "ipt id=\"__ie_init\" defer=\"true\" src=\"//:\"><\/script>");
    var script = document.getElementById("__ie_init");
    script.onreadystatechange = function() {
        if (this.readyState == "complete") {
            window.fireoninit(); // call the onload handler
        }
    };
  /*@end @*/

  // for Safari
  if (/WebKit/i.test(navigator.userAgent)) { // sniff
    var _timer = setInterval(function() {
        if (/loaded|complete/.test(document.readyState)) {
            window.fireoninit(); // call the onload handler
        }
    }, 10);
  }

  // for other browsers
  window.onload = window.fireoninit;


/**
 * This is a pseudo Event that will be fired by the fireoninit
 * function above.
 *
 * Use addInitEvent to bind to this event!
 *
 * @author Andreas Gohr <andi@splitbrain.org>
 * @see fireoninit()
 */
window.oninit = function(){};

/**
 * Bind a function to the window.init pseudo event
 *
 * @author Simon Willison
 * @see http://simon.incutio.com/archive/2004/05/26/addLoadEvent
 */
function addInitEvent(func) {
  var oldoninit = window.oninit;
  if (typeof window.oninit != 'function') {
    window.oninit = func;
  } else {
    window.oninit = function() {
      oldoninit();
      func();
    };
  }
}




/* XXXXXXXXXX end of /home/jmonke2/public_html/wiki/lib/scripts/events.js XXXXXXXXXX */



/* XXXXXXXXXX begin of /home/jmonke2/public_html/wiki/lib/scripts/cookie.js XXXXXXXXXX */

/**
 * Handles the cookie used by several JavaScript functions
 *
 * Only a single cookie is written and read. You may only save
 * sime name-value pairs - no complex types!
 *
 * You should only use the getValue and setValue methods
 *
 * @author Andreas Gohr <andi@splitbrain.org>
 */
DokuCookie = {
    data: Array(),
    name: 'DOKU_PREFS',

    /**
     * Save a value to the cookie
     *
     * @author Andreas Gohr <andi@splitbrain.org>
     */
    setValue: function(key,val){
        DokuCookie.init();
        DokuCookie.data[key] = val;

        // prepare expire date
        var now = new Date();
        DokuCookie.fixDate(now);
        now.setTime(now.getTime() + 365 * 24 * 60 * 60 * 1000); //expire in a year

        //save the whole data array
        var text = '';
        for(var key in DokuCookie.data){
            text += '#'+escape(key)+'#'+DokuCookie.data[key];
        }
        DokuCookie.setCookie(DokuCookie.name,text.substr(1),now,DOKU_BASE);
    },

    /**
     * Get a Value from the Cookie
     *
     * @author Andreas Gohr <andi@splitbrain.org>
     */
    getValue: function(key){
        DokuCookie.init();
        return DokuCookie.data[key];
    },

    /**
     * Loads the current set cookie
     *
     * @author Andreas Gohr <andi@splitbrain.org>
     */
    init: function(){
        if(DokuCookie.data.length) return;
        var text  = DokuCookie.getCookie(DokuCookie.name);
        if(text){
            var parts = text.split('#');
            for(var i=0; i<parts.length; i+=2){
                DokuCookie.data[unescape(parts[i])] = unescape(parts[i+1]);
            }
        }
    },

    /**
     * This sets a cookie by JavaScript
     *
     * @link http://www.webreference.com/js/column8/functions.html
     */
    setCookie: function(name, value, expires, path, domain, secure) {
        var curCookie = name + "=" + escape(value) +
            ((expires) ? "; expires=" + expires.toGMTString() : "") +
            ((path) ? "; path=" + path : "") +
            ((domain) ? "; domain=" + domain : "") +
            ((secure) ? "; secure" : "");
        document.cookie = curCookie;
    },

    /**
     * This reads a cookie by JavaScript
     *
     * @link http://www.webreference.com/js/column8/functions.html
     */
    getCookie: function(name) {
        var dc = document.cookie;
        var prefix = name + "=";
        var begin = dc.indexOf("; " + prefix);
        if (begin == -1) {
            begin = dc.indexOf(prefix);
            if (begin !== 0){ return null; }
        } else {
            begin += 2;
        }
        var end = document.cookie.indexOf(";", begin);
        if (end == -1){
            end = dc.length;
        }
        return unescape(dc.substring(begin + prefix.length, end));
    },

    /**
     * This is needed for the cookie functions
     *
     * @link http://www.webreference.com/js/column8/functions.html
     */
    fixDate: function(date) {
        var base = new Date(0);
        var skew = base.getTime();
        if (skew > 0){
            date.setTime(date.getTime() - skew);
        }
    }
};


/* XXXXXXXXXX end of /home/jmonke2/public_html/wiki/lib/scripts/cookie.js XXXXXXXXXX */



/* XXXXXXXXXX begin of /home/jmonke2/public_html/wiki/lib/scripts/script.js XXXXXXXXXX */

/**
 * Some of these scripts were taken from wikipedia.org and were modified for DokuWiki
 */

/**
 * Some browser detection
 */
var clientPC  = navigator.userAgent.toLowerCase(); // Get client info
var is_gecko  = ((clientPC.indexOf('gecko')!=-1) && (clientPC.indexOf('spoofer')==-1) &&
                (clientPC.indexOf('khtml') == -1) && (clientPC.indexOf('netscape/7.0')==-1));
var is_safari = ((clientPC.indexOf('AppleWebKit')!=-1) && (clientPC.indexOf('spoofer')==-1));
var is_khtml  = (navigator.vendor == 'KDE' || ( document.childNodes && !document.all && !navigator.taintEnabled ));
if (clientPC.indexOf('opera')!=-1) {
    var is_opera = true;
    var is_opera_preseven = (window.opera && !document.childNodes);
    var is_opera_seven = (window.opera && document.childNodes);
}

/**
 * Rewrite the accesskey tooltips to be more browser and OS specific.
 *
 * Accesskey tooltips are still only a best-guess of what will work
 * on well known systems.
 *
 * @author Ben Coburn <btcoburn@silicodon.net>
 */
function updateAccessKeyTooltip() {
  // determin tooltip text (order matters)
  var tip = 'ALT+'; //default
  if (domLib_isMac) { tip = 'CTRL+'; }
  if (domLib_isOpera) { tip = 'SHIFT+ESC '; }
  // add other cases here...

  // do tooltip update
  if (tip=='ALT+') { return; }
  var exp = /\[ALT\+/i;
  var rep = '['+tip;
  var elements = domLib_getElementsByTagNames(['a', 'input', 'button']);
  for (var i=0; i<elements.length; i++) {
    if (elements[i].accessKey.length==1 && elements[i].title.length>0) {
      elements[i].title = elements[i].title.replace(exp, rep);
    }
  }
}

/**
 * Handy shortcut to document.getElementById
 *
 * This function was taken from the prototype library
 *
 * @link http://prototype.conio.net/
 */
function $() {
  var elements = new Array();

  for (var i = 0; i < arguments.length; i++) {
    var element = arguments[i];
    if (typeof element == 'string')
      element = document.getElementById(element);

    if (arguments.length == 1)
      return element;

    elements.push(element);
  }

  return elements;
}

/**
 * Simple function to check if a global var is defined
 *
 * @author Kae Verens
 * @link http://verens.com/archives/2005/07/25/isset-for-javascript/#comment-2835
 */
function isset(varname){
  return(typeof(window[varname])!='undefined');
}

/**
 * Select elements by their class name
 *
 * @author Dustin Diaz <dustin [at] dustindiaz [dot] com>
 * @link   http://www.dustindiaz.com/getelementsbyclass/
 */
function getElementsByClass(searchClass,node,tag) {
    var classElements = new Array();
    if ( node == null )
        node = document;
    if ( tag == null )
        tag = '*';
    var els = node.getElementsByTagName(tag);
    var elsLen = els.length;
    var pattern = new RegExp("(^|\\s)"+searchClass+"(\\s|$)");
    for (i = 0, j = 0; i < elsLen; i++) {
        if ( pattern.test(els[i].className) ) {
            classElements[j] = els[i];
            j++;
        }
    }
    return classElements;
}

/**
 * Get the X offset of the top left corner of the given object
 *
 * @link http://www.quirksmode.org/index.html?/js/findpos.html
 */
function findPosX(object){
  var curleft = 0;
  var obj = $(object);
  if (obj.offsetParent){
    while (obj.offsetParent){
      curleft += obj.offsetLeft;
      obj = obj.offsetParent;
    }
  }
  else if (obj.x){
    curleft += obj.x;
  }
  return curleft;
} //end findPosX function

/**
 * Get the Y offset of the top left corner of the given object
 *
 * @link http://www.quirksmode.org/index.html?/js/findpos.html
 */
function findPosY(object){
  var curtop = 0;
  var obj = $(object);
  if (obj.offsetParent){
    while (obj.offsetParent){
      curtop += obj.offsetTop;
      obj = obj.offsetParent;
    }
  }
  else if (obj.y){
    curtop += obj.y;
  }
  return curtop;
} //end findPosY function

/**
 * Escape special chars in JavaScript
 *
 * @author Andreas Gohr <andi@splitbrain.org>
 */
function jsEscape(text){
    var re=new RegExp("\\\\","g");
    text=text.replace(re,"\\\\");
    re=new RegExp("'","g");
    text=text.replace(re,"\\'");
    re=new RegExp('"',"g");
    text=text.replace(re,'&quot;');
    re=new RegExp("\\\\\\\\n","g");
    text=text.replace(re,"\\n");
    return text;
}

/**
 * This function escapes some special chars
 * @deprecated by above function
 */
function escapeQuotes(text) {
  var re=new RegExp("'","g");
  text=text.replace(re,"\\'");
  re=new RegExp('"',"g");
  text=text.replace(re,'&quot;');
  re=new RegExp("\\n","g");
  text=text.replace(re,"\\n");
  return text;
}

/**
 * Adds a node as the first childenode to the given parent
 *
 * @see appendChild()
 */
function prependChild(parent,element) {
    if(!parent.firstChild){
        parent.appendChild(element);
    }else{
        parent.insertBefore(element,parent.firstChild);
    }
}

/**
 * Prints a animated gif to show the search is performed
 *
 * Because we need to modify the DOM here before the document is loaded
 * and parsed completely we have to rely on document.write()
 *
 * @author Andreas Gohr <andi@splitbrain.org>
 */
function showLoadBar(){

  document.write('<img src="'+DOKU_BASE+'lib/images/loading.gif" '+
                 'width="150" height="12" alt="..." />');

  /* this does not work reliable in IE
  obj = $(id);

  if(obj){
    obj.innerHTML = '<img src="'+DOKU_BASE+'lib/images/loading.gif" '+
                    'width="150" height="12" alt="..." />';
    obj.style.display="block";
  }
  */
}

/**
 * Disables the animated gif to show the search is done
 *
 * @author Andreas Gohr <andi@splitbrain.org>
 */
function hideLoadBar(id){
  obj = $(id);
  if(obj) obj.style.display="none";
}

/**
 * Adds the toggle switch to the TOC
 */
function addTocToggle() {
    if(!document.getElementById) return;
    var header = $('toc__header');
  if(!header) return;

  var showimg     = document.createElement('img');
    showimg.id      = 'toc__show';
  showimg.src     = DOKU_BASE+'lib/images/arrow_down.gif';
  showimg.alt     = '+';
    showimg.onclick = toggleToc;
  showimg.style.display = 'none';

    var hideimg     = document.createElement('img');
    hideimg.id      = 'toc__hide';
  hideimg.src     = DOKU_BASE+'lib/images/arrow_up.gif';
  hideimg.alt     = '-';
    hideimg.onclick = toggleToc;

  prependChild(header,showimg);
  prependChild(header,hideimg);
}

/**
 * This toggles the visibility of the Table of Contents
 */
function toggleToc() {
  var toc = $('toc__inside');
  var showimg = $('toc__show');
  var hideimg = $('toc__hide');
  if(toc.style.display == 'none') {
    toc.style.display      = '';
    hideimg.style.display = '';
    showimg.style.display = 'none';
  } else {
    toc.style.display      = 'none';
    hideimg.style.display = 'none';
    showimg.style.display = '';
  }
}

/*
 * This enables/disables checkboxes for acl-administration
 *
 * @author Frank Schubert <frank@schokilade.de>
 */
function checkAclLevel(){
  if(document.getElementById) {
    var scope = $('acl_scope').value;

    //check for namespace
    if( (scope.indexOf(":*") > 0) || (scope == "*") ){
      document.getElementsByName('acl_checkbox[4]')[0].disabled=false;
      document.getElementsByName('acl_checkbox[8]')[0].disabled=false;
    }else{
      document.getElementsByName('acl_checkbox[4]')[0].checked=false;
      document.getElementsByName('acl_checkbox[8]')[0].checked=false;

      document.getElementsByName('acl_checkbox[4]')[0].disabled=true;
      document.getElementsByName('acl_checkbox[8]')[0].disabled=true;
    }
  }
}

/**
 * insitu footnote addition
 *
 * provide a wrapper for domTT javascript library
 * this function is placed in the onmouseover event of footnote references in the main page
 *
 * @author Chris Smith <chris [at] jalakai [dot] co [dot] uk>
 */
var currentFootnote = 0;
function fnt(id, e, evt) {

    if (currentFootnote && id != currentFootnote) {
        domTT_close($('insitu__fn'+currentFootnote));
    }

    // does the footnote tooltip already exist?
    var fnote = $('insitu__fn'+id);
    var footnote;
    if (!fnote) {
        // if not create it...

        // locate the footnote anchor element
        var a = $( "fn__"+id );
        if (!a){ return; }

        // anchor parent is the footnote container, get its innerHTML
        footnote = new String (a.parentNode.innerHTML);

        // strip the leading footnote anchors and their comma separators
        footnote = footnote.replace(/<a\s.*?href=\".*\#fnt__\d+\".*?<\/a>/gi, '');
        footnote = footnote.replace(/^\s+(,\s+)+/,'');

        // prefix ids on any elements with "insitu__" to ensure they remain unique
        footnote = footnote.replace(/\bid=\"(.*?)\"/gi,'id="insitu__$1');
    } else {
        footnote = new String(fnt.innerHTML);
    }

    // activate the tooltip
    domTT_activate(e, evt, 'content', footnote, 'type', 'velcro', 'id', 'insitu__fn'+id, 'styleClass', 'insitu-footnote JSpopup dokuwiki', 'maxWidth', document.body.offsetWidth*0.4);
    currentFootnote = id;
}


/**
 * Add the edit window size controls
 */
function initSizeCtl(ctlid,edid){
    if(!document.getElementById){ return; }

    var ctl      = $(ctlid);
    var textarea = $(edid);
    if(!ctl || !textarea) return;

    var hgt = DokuCookie.getValue('sizeCtl');
    if(hgt){
      textarea.style.height = hgt;
    }else{
      textarea.style.height = '300px';
    }

    var l = document.createElement('img');
    var s = document.createElement('img');
    var w = document.createElement('img');
    l.src = DOKU_BASE+'lib/images/larger.gif';
    s.src = DOKU_BASE+'lib/images/smaller.gif';
    w.src = DOKU_BASE+'lib/images/wrap.gif';
    addEvent(l,'click',function(){sizeCtl(edid,100);});
    addEvent(s,'click',function(){sizeCtl(edid,-100);});
    addEvent(w,'click',function(){toggleWrap(edid);});
    ctl.appendChild(l);
    ctl.appendChild(s);
    ctl.appendChild(w);
}

/**
 * This sets the vertical size of the editbox
 */
function sizeCtl(edid,val){
  var textarea = $(edid);
  var height = parseInt(textarea.style.height.substr(0,textarea.style.height.length-2));
  height += val;
  textarea.style.height = height+'px';

  DokuCookie.setValue('sizeCtl',textarea.style.height);
}

/**
 * Toggle the wrapping mode of a textarea
 *
 * @author Fluffy Convict <fluffyconvict@hotmail.com>
 * @link   http://news.hping.org/comp.lang.javascript.archive/12265.html
 * @author <shutdown@flashmail.com>
 * @link   https://bugzilla.mozilla.org/show_bug.cgi?id=302710#c2
 */
function toggleWrap(edid){
    var txtarea = $(edid);
    var wrap = txtarea.getAttribute('wrap');
    if(wrap && wrap.toLowerCase() == 'off'){
        txtarea.setAttribute('wrap', 'soft');
    }else{
        txtarea.setAttribute('wrap', 'off');
    }
    // Fix display for mozilla
    var parNod = txtarea.parentNode;
    var nxtSib = txtarea.nextSibling;
    parNod.removeChild(txtarea);
    parNod.insertBefore(txtarea, nxtSib);
}

/**
 * Handler to close all open Popups
 */
function closePopups(){
  if(!document.getElementById){ return; }

  var divs = document.getElementsByTagName('div');
  for(var i=0; i < divs.length; i++){
    if(divs[i].className.indexOf('JSpopup') != -1){
            divs[i].style.display = 'none';
    }
  }
}

/**
 * Looks for an element with the ID scroll__here at scrolls to it
 */
function scrollToMarker(){
    var obj = $('scroll__here');
    if(obj) obj.scrollIntoView();
}

/**
 * Looks for an element with the ID focus__this at sets focus to it
 */
function focusMarker(){
    var obj = $('focus__this');
    if(obj) obj.focus();
}

/**
 * Remove messages
 */
function cleanMsgArea(){
    var elems = getElementsByClass('(success|info|error)',document,'div');
    if(elems){
        for(var i=0; i<elems.length; i++){
            elems[i].style.display = 'none';
        }
    }
}


/* XXXXXXXXXX end of /home/jmonke2/public_html/wiki/lib/scripts/script.js XXXXXXXXXX */



/* XXXXXXXXXX begin of /home/jmonke2/public_html/wiki/lib/scripts/tw-sack.js XXXXXXXXXX */

/* Simple AJAX Code-Kit (SACK) */
/* ©2005 Gregory Wild-Smith */
/* www.twilightuniverse.com */
/* Software licenced under a modified X11 licence, see documentation or authors website for more details */

function sack(file){
  this.AjaxFailedAlert = "Your browser does not support the enhanced functionality of this website, and therefore you will have an experience that differs from the intended one.\n";
  this.requestFile = file;
  this.method = "POST";
  this.URLString = "";
  this.encodeURIString = true;
  this.execute = false;

  this.onLoading = function() { };
  this.onLoaded = function() { };
  this.onInteractive = function() { };
  this.onCompletion = function() { };
  this.afterCompletion = function() { };

  this.createAJAX = function() {
    try {
      this.xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e) {
      try {
        this.xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
      } catch (err) {
        this.xmlhttp = null;
      }
    }
    if(!this.xmlhttp && typeof XMLHttpRequest != "undefined"){
      this.xmlhttp = new XMLHttpRequest();
    }
    if (!this.xmlhttp){
      this.failed = true;
    }
  };

  this.setVar = function(name, value){
    if (this.URLString.length < 3){
      this.URLString = name + "=" + value;
    } else {
      this.URLString += "&" + name + "=" + value;
    }
  };

  this.encVar = function(name, value){
    var varString = encodeURIComponent(name) + "=" + encodeURIComponent(value);
  return varString;
  };

  this.encodeURLString = function(string){
    varArray = string.split('&');
    for (i = 0; i < varArray.length; i++){
      urlVars = varArray[i].split('=');
      if (urlVars[0].indexOf('amp;') != -1){
        urlVars[0] = urlVars[0].substring(4);
      }
      varArray[i] = this.encVar(urlVars[0],urlVars[1]);
    }
  return varArray.join('&');
  };

  this.runResponse = function(){
    eval(this.response);
  };

  this.runAJAX = function(urlstring){
    this.responseStatus = new Array(2);
    if(this.failed && this.AjaxFailedAlert){
      alert(this.AjaxFailedAlert);
    } else {
      if (urlstring){
        if (this.URLString.length){
          this.URLString = this.URLString + "&" + urlstring;
        } else {
          this.URLString = urlstring;
        }
      }
      if (this.encodeURIString){
        var timeval = new Date().getTime();
        this.URLString = this.encodeURLString(this.URLString);
        this.setVar("rndval", timeval);
      }
      if (this.element) { this.elementObj = document.getElementById(this.element); }
      if (this.xmlhttp) {
        var self = this;
        if (this.method == "GET") {
          var totalurlstring = this.requestFile + "?" + this.URLString;
          this.xmlhttp.open(this.method, totalurlstring, true);
        } else {
          this.xmlhttp.open(this.method, this.requestFile, true);
        }
        if (this.method == "POST"){
          try {
             this.xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded; charset=UTF-8');
          } catch (e) {}
        }

        this.xmlhttp.onreadystatechange = function() {
          switch (self.xmlhttp.readyState){
            case 1:
              self.onLoading();
            break;
            case 2:
              self.onLoaded();
            break;
            case 3:
              self.onInteractive();
            break;
            case 4:
              self.response = self.xmlhttp.responseText;
              self.responseXML = self.xmlhttp.responseXML;
              self.responseStatus[0] = self.xmlhttp.status;
              self.responseStatus[1] = self.xmlhttp.statusText;
              self.onCompletion();
              if(self.execute){ self.runResponse(); }
              if (self.elementObj) {
                var elemNodeName = self.elementObj.nodeName;
                elemNodeName.toLowerCase();
                if (elemNodeName == "input" || elemNodeName == "select" || elemNodeName == "option" || elemNodeName == "textarea"){
                  self.elementObj.value = self.response;
                } else {
                  self.elementObj.innerHTML = self.response;
                }
              }
              self.afterCompletion();
              self.URLString = "";
            break;
          }
        };
        this.xmlhttp.send(this.URLString);
      }
    }
  };
this.createAJAX();
}


/* XXXXXXXXXX end of /home/jmonke2/public_html/wiki/lib/scripts/tw-sack.js XXXXXXXXXX */



/* XXXXXXXXXX begin of /home/jmonke2/public_html/wiki/lib/scripts/ajax.js XXXXXXXXXX */

/**
 * AJAX functions for the pagename quicksearch
 *
 * We're using a global object with self referencing methods
 * here to make callbacks work
 *
 * @license  GPL2 (http://www.gnu.org/licenses/gpl.html)
 * @author   Andreas Gohr <andi@splitbrain.org>
 */

//prepare class
function ajax_qsearch_class(){
  this.sack = null;
  this.inObj = null;
  this.outObj = null;
  this.timer = null;
}

//create global object and add functions
var ajax_qsearch = new ajax_qsearch_class();
ajax_qsearch.sack = new sack(DOKU_BASE + 'lib/exe/ajax.php');
ajax_qsearch.sack.AjaxFailedAlert = '';
ajax_qsearch.sack.encodeURIString = false;

ajax_qsearch.init = function(inID,outID){
  ajax_qsearch.inObj  = document.getElementById(inID);
  ajax_qsearch.outObj = document.getElementById(outID);

  // objects found?
  if(ajax_qsearch.inObj === null){ return; }
  if(ajax_qsearch.outObj === null){ return; }

  // attach eventhandler to search field
  addEvent(ajax_qsearch.inObj,'keyup',ajax_qsearch.call);

  // attach eventhandler to output field
  addEvent(ajax_qsearch.outObj,'click',function(){ ajax_qsearch.outObj.style.display='none'; });
};

ajax_qsearch.clear = function(){
  ajax_qsearch.outObj.style.display = 'none';
  ajax_qsearch.outObj.innerHTML = '';
  if(ajax_qsearch.timer !== null){
    window.clearTimeout(ajax_qsearch.timer);
    ajax_qsearch.timer = null;
  }
};

ajax_qsearch.exec = function(){
  ajax_qsearch.clear();
  var value = ajax_qsearch.inObj.value;
  if(value === ''){ return; }
  ajax_qsearch.sack.runAJAX('call=qsearch&q='+encodeURI(value));
};

ajax_qsearch.sack.onCompletion = function(){
  var data = ajax_qsearch.sack.response;
  if(data === ''){ return; }

  ajax_qsearch.outObj.innerHTML = data;
  ajax_qsearch.outObj.style.display = 'block';
};

ajax_qsearch.call = function(){
  ajax_qsearch.clear();
  ajax_qsearch.timer = window.setTimeout("ajax_qsearch.exec()",500);
};



/* XXXXXXXXXX end of /home/jmonke2/public_html/wiki/lib/scripts/ajax.js XXXXXXXXXX */



/* XXXXXXXXXX begin of /home/jmonke2/public_html/wiki/lib/scripts/domLib.js XXXXXXXXXX */

/** $Id: domLib.js 1952 2005-07-17 16:24:05Z dallen $ */
// {{{ license

/*
 * Copyright 2002-2005 Dan Allen, Mojavelinux.com (dan.allen@mojavelinux.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// }}}
// {{{ intro

/**
 * Title: DOM Library Core
 * Version: 0.70
 *
 * Summary:
 * A set of commonly used functions that make it easier to create javascript
 * applications that rely on the DOM.
 *
 * Updated: 2005/05/17
 *
 * Maintainer: Dan Allen <dan.allen@mojavelinux.com>
 * Maintainer: Jason Rust <jrust@rustyparts.com>
 *
 * License: Apache 2.0
 */

// }}}
// {{{ global constants (DO NOT EDIT)

// -- Browser Detection --
var domLib_userAgent = navigator.userAgent.toLowerCase();
var domLib_isMac = navigator.appVersion.indexOf('Mac') != -1;
var domLib_isWin = domLib_userAgent.indexOf('windows') != -1;
// NOTE: could use window.opera for detecting Opera
var domLib_isOpera = domLib_userAgent.indexOf('opera') != -1;
var domLib_isOpera7up = domLib_userAgent.match(/opera.(7|8)/i);
var domLib_isSafari = domLib_userAgent.indexOf('safari') != -1;
var domLib_isKonq = domLib_userAgent.indexOf('konqueror') != -1;
// Both konqueror and safari use the khtml rendering engine
var domLib_isKHTML = (domLib_isKonq || domLib_isSafari || domLib_userAgent.indexOf('khtml') != -1);
var domLib_isIE = (!domLib_isKHTML && !domLib_isOpera && (domLib_userAgent.indexOf('msie 5') != -1 || domLib_userAgent.indexOf('msie 6') != -1 || domLib_userAgent.indexOf('msie 7') != -1));
var domLib_isIE5up = domLib_isIE;
var domLib_isIE50 = (domLib_isIE && domLib_userAgent.indexOf('msie 5.0') != -1);
var domLib_isIE55 = (domLib_isIE && domLib_userAgent.indexOf('msie 5.5') != -1);
var domLib_isIE5 = (domLib_isIE50 || domLib_isIE55);
// safari and konq may use string "khtml, like gecko", so check for destinctive /
var domLib_isGecko = domLib_userAgent.indexOf('gecko/') != -1;
var domLib_isMacIE = (domLib_isIE && domLib_isMac);
var domLib_isIE55up = domLib_isIE5up && !domLib_isIE50 && !domLib_isMacIE;
var domLib_isIE6up = domLib_isIE55up && !domLib_isIE55;

// -- Browser Abilities --
var domLib_standardsMode = (document.compatMode && document.compatMode == 'CSS1Compat');
var domLib_useLibrary = (domLib_isOpera7up || domLib_isKHTML || domLib_isIE5up || domLib_isGecko || domLib_isMacIE || document.defaultView);
// fixed in Konq3.2
var domLib_hasBrokenTimeout = (domLib_isMacIE || (domLib_isKonq && domLib_userAgent.match(/konqueror\/3.([2-9])/) === null));
var domLib_canFade = (domLib_isGecko || domLib_isIE || domLib_isSafari || domLib_isOpera);
var domLib_canDrawOverSelect = (domLib_isMac || domLib_isOpera || domLib_isGecko);
var domLib_canDrawOverFlash = (domLib_isMac || domLib_isWin);

// -- Event Variables --
var domLib_eventTarget = domLib_isIE ? 'srcElement' : 'currentTarget';
var domLib_eventButton = domLib_isIE ? 'button' : 'which';
var domLib_eventTo = domLib_isIE ? 'toElement' : 'relatedTarget';
var domLib_stylePointer = domLib_isIE ? 'hand' : 'pointer';
// NOTE: a bug exists in Opera that prevents maxWidth from being set to 'none', so we make it huge
var domLib_styleNoMaxWidth = domLib_isOpera ? '10000px' : 'none';
var domLib_hidePosition = '-1000px';
var domLib_scrollbarWidth = 14;
var domLib_autoId = 1;
var domLib_zIndex = 100;

// -- Detection --
var domLib_collisionElements;
var domLib_collisionsCached = false;

var domLib_timeoutStateId = 0;
var domLib_timeoutStates = new Hash();

// }}}
// {{{ DOM enhancements

if (!document.ELEMENT_NODE)
{
	document.ELEMENT_NODE = 1;
	document.ATTRIBUTE_NODE = 2;
	document.TEXT_NODE = 3;
	document.DOCUMENT_NODE = 9;
	document.DOCUMENT_FRAGMENT_NODE = 11;
}

function domLib_clone(obj)
{
	var copy = {};
	for (var i in obj)
	{
		var value = obj[i];
		try
		{
			if (value !== null && typeof(value) == 'object' && value != window && !value.nodeType)
			{
				copy[i] = domLib_clone(value);
			}
			else
			{
				copy[i] = value;
			}
		}
		catch(e)
		{
			copy[i] = value;
		}
	}

	return copy;
}

// }}}
// {{{ class Hash()

function Hash()
{
	this.length = 0;
	this.numericLength = 0; 
	this.elementData = [];
	for (var i = 0; i < arguments.length; i += 2)
	{
		if (typeof(arguments[i + 1]) != 'undefined')
		{
			this.elementData[arguments[i]] = arguments[i + 1];
			this.length++;
			if (arguments[i] == parseInt(arguments[i])) 
			{
				this.numericLength++;
			}
		}
	}
}

// using prototype as opposed to inner functions saves on memory 
Hash.prototype.get = function(in_key)
{
	return this.elementData[in_key];
};

Hash.prototype.set = function(in_key, in_value)
{
	if (typeof(in_value) != 'undefined')
	{
		if (typeof(this.elementData[in_key]) == 'undefined')
		{
			this.length++;
			if (in_key == parseInt(in_key)) 
			{
				this.numericLength++;
			}
		}

		this.elementData[in_key] = in_value;
    return this.elementData[in_key];
	}

	return false;
};

Hash.prototype.remove = function(in_key)
{
	var tmp_value;
	if (typeof(this.elementData[in_key]) != 'undefined')
	{
		this.length--;
		if (in_key == parseInt(in_key)) 
		{
			this.numericLength--;
		}

		tmp_value = this.elementData[in_key];
		delete this.elementData[in_key];
	}

	return tmp_value;
};

Hash.prototype.size = function()
{
	return this.length;
};

Hash.prototype.has = function(in_key)
{
	return typeof(this.elementData[in_key]) != 'undefined';
};

Hash.prototype.find = function(in_obj)
{
	for (var tmp_key in this.elementData) 
	{
		if (this.elementData[tmp_key] == in_obj) 
		{
			return tmp_key;
		}
	}
};

Hash.prototype.merge = function(in_hash)
{
	for (var tmp_key in in_hash.elementData) 
	{
		if (typeof(this.elementData[tmp_key]) == 'undefined') 
		{
			this.length++;
			if (tmp_key == parseInt(tmp_key)) 
			{
				this.numericLength++;
			}
		}

		this.elementData[tmp_key] = in_hash.elementData[tmp_key];
	}
};

Hash.prototype.compare = function(in_hash)
{
	if (this.length != in_hash.length) 
	{
		return false;
	}

	for (var tmp_key in this.elementData) 
	{
		if (this.elementData[tmp_key] != in_hash.elementData[tmp_key]) 
		{
			return false;
		}
	}
	
	return true;
};

// }}}
// {{{ domLib_isDescendantOf()

function domLib_isDescendantOf(in_object, in_ancestor)
{
	if (in_object == in_ancestor)
	{
		return true;
	}

	while (in_object != document.documentElement)
	{
		try
		{
			if ((tmp_object = in_object.offsetParent) && tmp_object == in_ancestor)
			{
				return true;
			}
			else if ((tmp_object = in_object.parentNode) == in_ancestor)
			{
				return true;
			}
			else
			{
				in_object = tmp_object;
			}
		}
		// in case we get some wierd error, just assume we haven't gone out yet
		catch(e)
		{
			return true;
		}
	}

	return false;
}

// }}}
// {{{ domLib_detectCollisions()

/**
 * For any given target element, determine if elements on the page
 * are colliding with it that do not obey the rules of z-index.
 */
function domLib_detectCollisions(in_object, in_recover, in_useCache)
{
	// the reason for the cache is that if the root menu is built before
	// the page is done loading, then it might not find all the elements.
	// so really the only time you don't use cache is when building the
	// menu as part of the page load
	if (!domLib_collisionsCached)
	{
		var tags = [];

		if (!domLib_canDrawOverFlash)
		{
			tags[tags.length] = 'object';
		}

		if (!domLib_canDrawOverSelect)
		{
			tags[tags.length] = 'select';
		}

		domLib_collisionElements = domLib_getElementsByTagNames(tags, true);
		domLib_collisionsCached = in_useCache;
	}

	// if we don't have a tip, then unhide selects
	if (in_recover)
	{
		for (var cnt = 0; cnt < domLib_collisionElements.length; cnt++)
		{
			var thisElement = domLib_collisionElements[cnt];

			if (!thisElement.hideList)
			{
				thisElement.hideList = new Hash();
			}

			thisElement.hideList.remove(in_object.id);
			if (!thisElement.hideList.length)
			{
				domLib_collisionElements[cnt].style.visibility = 'visible';
				if (domLib_isKonq)
				{
					domLib_collisionElements[cnt].style.display = '';
				}
			}
		}

		return;
	}
	else if (domLib_collisionElements.length === 0)
	{
		return;
	}

	// okay, we have a tip, so hunt and destroy
	var objectOffsets = domLib_getOffsets(in_object);

	for (cnt = 0; cnt < domLib_collisionElements.length; cnt++)
	{
		thisElement = domLib_collisionElements[cnt];

		// if collision element is in active element, move on
		// WARNING: is this too costly?
		if (domLib_isDescendantOf(thisElement, in_object))
		{
			continue;
		}

		// konqueror only has trouble with multirow selects
		if (domLib_isKonq &&
			thisElement.tagName == 'SELECT' &&
			(thisElement.size <= 1 && !thisElement.multiple))
		{
			continue;
		}

		if (!thisElement.hideList)
		{
			thisElement.hideList = new Hash();
		}

		var selectOffsets = domLib_getOffsets(thisElement); 
		var center2centerDistance = Math.sqrt(Math.pow(selectOffsets.get('leftCenter') - objectOffsets.get('leftCenter'), 2) + Math.pow(selectOffsets.get('topCenter') - objectOffsets.get('topCenter'), 2));
		var radiusSum = selectOffsets.get('radius') + objectOffsets.get('radius');
		// the encompassing circles are overlapping, get in for a closer look
		if (center2centerDistance < radiusSum)
		{
			// tip is left of select
			if ((objectOffsets.get('leftCenter') <= selectOffsets.get('leftCenter') && objectOffsets.get('right') < selectOffsets.get('left')) ||
			// tip is right of select
				(objectOffsets.get('leftCenter') > selectOffsets.get('leftCenter') && objectOffsets.get('left') > selectOffsets.get('right')) ||
			// tip is above select
				(objectOffsets.get('topCenter') <= selectOffsets.get('topCenter') && objectOffsets.get('bottom') < selectOffsets.get('top')) ||
			// tip is below select
				(objectOffsets.get('topCenter') > selectOffsets.get('topCenter') && objectOffsets.get('top') > selectOffsets.get('bottom')))
			{
				thisElement.hideList.remove(in_object.id);
				if (!thisElement.hideList.length)
				{
					thisElement.style.visibility = 'visible';
					if (domLib_isKonq)
					{
						thisElement.style.display = '';
					}
				}
			}
			else
			{
				thisElement.hideList.set(in_object.id, true);
				thisElement.style.visibility = 'hidden';
				if (domLib_isKonq)
				{
					thisElement.style.display = 'none';
				}
			}
		}
	}
}

// }}}
// {{{ domLib_getOffsets()

function domLib_getOffsets(in_object)
{
	var originalObject = in_object;
	var originalWidth = in_object.offsetWidth;
	var originalHeight = in_object.offsetHeight;
	var offsetLeft = 0;
	var offsetTop = 0;

	while (in_object)
	{
		offsetLeft += in_object.offsetLeft;
		offsetTop += in_object.offsetTop;
		in_object = in_object.offsetParent;
	}

	// MacIE misreports the offsets (even with margin: 0 in body{}), still not perfect
	if (domLib_isMacIE) {
		offsetLeft += 10;
		offsetTop += 10;
	}

	return new Hash(
		'left',		offsetLeft,
		'top',		offsetTop,
		'right',	offsetLeft + originalWidth,
		'bottom',	offsetTop + originalHeight,
		'leftCenter',	offsetLeft + originalWidth/2,
		'topCenter',	offsetTop + originalHeight/2,
		'radius',	Math.max(originalWidth, originalHeight)	);
}

// }}}
// {{{ domLib_setTimeout()

function domLib_setTimeout(in_function, in_timeout, in_args)
{
	if (typeof(in_args) == 'undefined')
	{
		in_args = [];
	}

	if (in_timeout == -1)
	{
		// timeout event is disabled
		return;
	}
	else if (in_timeout === 0)
	{
		in_function(in_args);
		return 0;
	}

	// must make a copy of the arguments so that we release the reference
	var args = domLib_clone(in_args);

	if (!domLib_hasBrokenTimeout)
	{
		return setTimeout(function() { in_function(args); }, in_timeout);
	}
	else
	{
		var id = domLib_timeoutStateId++;
		var data = new Hash();
		data.set('function', in_function);
		data.set('args', args);
		domLib_timeoutStates.set(id, data);

		data.set('timeoutId', setTimeout('domLib_timeoutStates.get(' + id + ').get(\'function\')(domLib_timeoutStates.get(' + id + ').get(\'args\')); domLib_timeoutStates.remove(' + id + ');', in_timeout));
		return id;
	}
}

// }}}
// {{{ domLib_clearTimeout()

function domLib_clearTimeout(in_id)
{
	if (!domLib_hasBrokenTimeout)
	{
		clearTimeout(in_id);
	}
	else
	{
		if (domLib_timeoutStates.has(in_id))
		{
			clearTimeout(domLib_timeoutStates.get(in_id).get('timeoutId'));
			domLib_timeoutStates.remove(in_id);
		}
	}
}

// }}}
// {{{ domLib_getEventPosition()

function domLib_getEventPosition(in_eventObj)
{
	var eventPosition = new Hash('x', 0, 'y', 0, 'scrollX', 0, 'scrollY', 0);

	// IE varies depending on standard compliance mode
	if (domLib_isIE)
	{
		var doc = (domLib_standardsMode ? document.documentElement : document.body);
		// NOTE: events may fire before the body has been loaded
		if (doc)
		{
			eventPosition.set('x', in_eventObj.clientX + doc.scrollLeft);
			eventPosition.set('y', in_eventObj.clientY + doc.scrollTop);
			eventPosition.set('scrollX', doc.scrollLeft);
			eventPosition.set('scrollY', doc.scrollTop);
		}
	}
	else
	{
		eventPosition.set('x', in_eventObj.pageX);
		eventPosition.set('y', in_eventObj.pageY);
		eventPosition.set('scrollX', in_eventObj.pageX - in_eventObj.clientX);
		eventPosition.set('scrollY', in_eventObj.pageY - in_eventObj.clientY);
	}

	return eventPosition;
}

// }}}
// {{{ domLib_cancelBubble()

function domLib_cancelBubble(in_event)
{
	var eventObj = in_event ? in_event : window.event;
	eventObj.cancelBubble = true;
}

// }}}
// {{{ domLib_getIFrameReference()

function domLib_getIFrameReference(in_frame)
{
	if (domLib_isGecko || domLib_isIE)
	{
		return in_frame.frameElement;
	}
	else
	{
		// we could either do it this way or require an id on the frame
		// equivalent to the name
		var name = in_frame.name;
		if (!name || !in_frame.parent)
		{
			return;
		}

		var candidates = in_frame.parent.document.getElementsByTagName('iframe');
		for (var i = 0; i < candidates.length; i++)
		{
			if (candidates[i].name == name)
			{
				return candidates[i];
			}
		}
	}
}

// }}}
// {{{ domLib_getElementsByClass()

function domLib_getElementsByClass(in_class)
{
	var elements = domLib_isIE5 ? document.all : document.getElementsByTagName('*');	
	var matches = [];	
	var cnt = 0;
	for (var i = 0; i < elements.length; i++)
	{
		if ((" " + elements[i].className + " ").indexOf(" " + in_class + " ") != -1)
		{
			matches[cnt++] = elements[i];
		}
	}

	return matches;
}

// }}}
// {{{

function domLib_getElementsByTagNames(in_list, in_excludeHidden)
{
	var elements = [];
	for (var i = 0; i < in_list.length; i++)
	{
		var matches = document.getElementsByTagName(in_list[i]);
		for (var j = 0; j < matches.length; j++)
		{
			if (in_excludeHidden && domLib_getComputedStyle(matches[j], 'visibility') == 'hidden')
			{
				continue;
			}

			elements[elements.length] = matches[j];	
		}
	}

	return elements;
}

// }}}
// {{{

function domLib_getComputedStyle(in_obj, in_property)
{
	if (domLib_isIE)
	{
		var humpBackProp = in_property.replace(/-(.)/, function (a, b) { return b.toUpperCase(); });
		return eval('in_obj.currentStyle.' + humpBackProp);
	}
	// getComputedStyle() is broken in konqueror, so let's go for the style object
	else if (domLib_isKonq)
	{
		humpBackProp = in_property.replace(/-(.)/, function (a, b) { return b.toUpperCase(); });
		return eval('in_obj.style.' + in_property);
	}
	else
	{
		return document.defaultView.getComputedStyle(in_obj, null).getPropertyValue(in_property);
	}
}

// }}}
// {{{ makeTrue()

function makeTrue()
{
	return true;
}

// }}}
// {{{ makeFalse()

function makeFalse()
{
	return false;
}

// }}}



/* XXXXXXXXXX end of /home/jmonke2/public_html/wiki/lib/scripts/domLib.js XXXXXXXXXX */



/* XXXXXXXXXX begin of /home/jmonke2/public_html/wiki/lib/scripts/domTT.js XXXXXXXXXX */

/** $Id: domTT.js 1951 2005-07-17 16:22:34Z dallen $ */
// {{{ license

/*
 * Copyright 2002-2005 Dan Allen, Mojavelinux.com (dan.allen@mojavelinux.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// }}}
// {{{ intro

/**
 * Title: DOM Tooltip Library
 * Version: 0.7.1
 *
 * Summary:
 * Allows developers to add custom tooltips to the webpages.  Tooltips are
 * generated using the domTT_activate() function and customized by setting
 * a handful of options.
 *
 * Maintainer: Dan Allen <dan.allen@mojavelinux.com>
 * Contributors:
 * 		Josh Gross <josh@jportalhome.com>
 *		Jason Rust <jason@rustyparts.com>
 *
 * License: Apache 2.0
 * However, if you use this library, you earn the position of official bug
 * reporter :) Please post questions or problem reports to the newsgroup:
 *
 *   http://groups-beta.google.com/group/dom-tooltip
 *
 * If you are doing this for commercial work, perhaps you could send me a few
 * Starbucks Coffee gift dollars or PayPal bucks to encourage future
 * developement (NOT REQUIRED).  E-mail me for my snail mail address.

 *
 * Homepage: http://www.mojavelinux.com/projects/domtooltip/
 *
 * Newsgroup: http://groups-beta.google.com/group/dom-tooltip
 *
 * Freshmeat Project: http://freshmeat.net/projects/domtt/?topic_id=92
 *
 * Updated: 2005/07/16
 *
 * Supported Browsers:
 * Mozilla (Gecko), IE 5.5+, IE on Mac, Safari, Konqueror, Opera 7
 *
 * Usage:
 * Please see the HOWTO documentation.
**/

// }}}
// {{{ settings (editable)

// IE mouse events seem to be off by 2 pixels
var domTT_offsetX = (domLib_isIE ? -2 : 0);
var domTT_offsetY = (domLib_isIE ? 4 : 2);
var domTT_direction = 'southeast';
var domTT_mouseHeight = domLib_isIE ? 13 : 19;
var domTT_closeLink = 'X';
var domTT_closeAction = 'hide';
var domTT_activateDelay = 500;
var domTT_maxWidth = false;
var domTT_styleClass = 'domTT';
var domTT_fade = 'neither';
var domTT_lifetime = 0;
var domTT_grid = 0;
var domTT_trailDelay = 200;
var domTT_useGlobalMousePosition = true;
var domTT_screenEdgeDetection = true;
var domTT_screenEdgePadding = 4;
var domTT_oneOnly = false;
var domTT_draggable = false;
if (typeof(domTT_dragEnabled) == 'undefined')
{
	domTT_dragEnabled = false;
}

// }}}
// {{{ globals (DO NOT EDIT)

var domTT_predefined = new Hash();
// tooltips are keyed on both the tip id and the owner id,
// since events can originate on either object
var domTT_tooltips = new Hash();
var domTT_lastOpened = 0;

// }}}
// {{{ document.onmousemove

if (domLib_useLibrary && domTT_useGlobalMousePosition)
{
	var domTT_mousePosition = new Hash();
	document.onmousemove = function(in_event)
	{
		if (typeof(in_event) == 'undefined')
		{
			in_event = event;
		}

		domTT_mousePosition = domLib_getEventPosition(in_event);
		if (domTT_dragEnabled && domTT_dragMouseDown)
		{
			domTT_dragUpdate(in_event);
		}
	};
}

// }}}
// {{{ domTT_activate()

function domTT_activate(in_this, in_event)
{
	if (!domLib_useLibrary) { return false; }

	// make sure in_event is set (for IE, some cases we have to use window.event)
	if (typeof(in_event) == 'undefined')
	{
		in_event = window.event;
	}

	var owner = document.body;
	// we have an active event so get the owner
	if (in_event.type.match(/key|mouse|click|contextmenu/i))
	{
		// make sure we have nothing higher than the body element
		if (in_this.nodeType && in_this.nodeType != document.DOCUMENT_NODE)
		{
			var owner = in_this;
		}
	}
	// non active event (make sure we were passed a string id)
	else
	{
		if (typeof(in_this) != 'object' && !(owner = domTT_tooltips.get(in_this)))
		{
			owner = document.body.appendChild(document.createElement('div'));
			owner.style.display = 'none';
			owner.id = in_this;
		}
	}

	// make sure the owner has a unique id
	if (!owner.id)
	{
		owner.id = '__autoId' + domLib_autoId++;
	}

	// see if we should only be openning one tip at a time
	// NOTE: this is not "perfect" yet since it really steps on any other
	// tip working on fade out or delayed close, but it get's the job done
	if (domTT_oneOnly && domTT_lastOpened)
	{
		domTT_deactivate(domTT_lastOpened);
	}

	domTT_lastOpened = owner.id;

	var tooltip = domTT_tooltips.get(owner.id);
	if (tooltip)
	{
		if (tooltip.get('eventType') != in_event.type)
		{
			if (tooltip.get('type') == 'greasy')
			{
				tooltip.set('closeAction', 'destroy');
				domTT_deactivate(owner.id);
			}
			else if (tooltip.get('status') != 'inactive')
			{
				return owner.id;
			}
		}
		else
		{
			if (tooltip.get('status') == 'inactive')
			{
				tooltip.set('status', 'pending');
				tooltip.set('activateTimeout', domLib_setTimeout(domTT_runShow, tooltip.get('delay'), [owner.id, in_event]));

				return owner.id;
			}
			// either pending or active, let it be
			else
			{
				return owner.id;
			}
		}
	}

	// setup the default options hash
	var options = new Hash(
		'caption',		'',
		'content',		'',
		'clearMouse',	true,
		'closeAction',	domTT_closeAction,
		'closeLink',	domTT_closeLink,
		'delay',		domTT_activateDelay,
		'direction',	domTT_direction,
		'draggable',	domTT_draggable,
		'fade',			domTT_fade,
		'fadeMax',		100,
		'grid',			domTT_grid,
		'id',			'[domTT]' + owner.id,
		'inframe',		false,
		'lifetime',		domTT_lifetime,
		'offsetX',		domTT_offsetX,
		'offsetY',		domTT_offsetY,
		'parent',		document.body,
		'position',		'absolute',
		'styleClass',	domTT_styleClass,
		'type',			'greasy',
		'trail',		false,
		'lazy',			false
	);

	// load in the options from the function call
	for (var i = 2; i < arguments.length; i += 2)
	{
		// load in predefined
		if (arguments[i] == 'predefined')
		{
			var predefinedOptions = domTT_predefined.get(arguments[i + 1]);
			for (var j in predefinedOptions.elementData)
			{
				options.set(j, predefinedOptions.get(j));
			}
		}
		// set option
		else
		{
			options.set(arguments[i], arguments[i + 1]);
		}
	}

	options.set('eventType', in_event.type);

	// immediately set the status text if provided
	if (options.has('statusText'))
	{
		try { window.status = options.get('statusText'); } catch(e) {}
	}

	// if we didn't give content...assume we just wanted to change the status and return
	if (!options.has('content') || options.get('content') == '' || options.get('content') == null)
	{
		if (typeof(owner.onmouseout) != 'function')
		{
			owner.onmouseout = function(in_event) { domTT_mouseout(this, in_event); };
		}

		return owner.id;
	}

	options.set('owner', owner);

	domTT_create(options);

	// determine the show delay
	options.set('delay', in_event.type.match(/click|mousedown|contextmenu/i) ? 0 : parseInt(options.get('delay')));
	domTT_tooltips.set(owner.id, options);
	domTT_tooltips.set(options.get('id'), options);
	options.set('status', 'pending');
	options.set('activateTimeout', domLib_setTimeout(domTT_runShow, options.get('delay'), [owner.id, in_event]));

	return owner.id;
};

// }}}
// {{{ domTT_create()

function domTT_create(in_options)
{
	var tipOwner = in_options.get('owner');
	var parentObj = in_options.get('parent');
	var parentDoc = parentObj.ownerDocument || parentObj.document;

	// create the tooltip and hide it
	var tipObj = parentObj.appendChild(parentDoc.createElement('div'));
	tipObj.style.position = 'absolute';
	tipObj.style.left = '0px';
	tipObj.style.top = '0px';
	tipObj.style.visibility = 'hidden';
	tipObj.id = in_options.get('id');
	tipObj.className = in_options.get('styleClass');

	// content of tip as object
	var content;
	var tableLayout = false;

	if (in_options.get('caption') || (in_options.get('type') == 'sticky' && in_options.get('caption') !== false))
	{
		tableLayout = true;
		// layout the tip with a hidden formatting table
		var tipLayoutTable = tipObj.appendChild(parentDoc.createElement('table'));
		tipLayoutTable.style.borderCollapse = 'collapse';
		if (domLib_isKHTML)
		{
			tipLayoutTable.cellSpacing = 0;
		}

		var tipLayoutTbody = tipLayoutTable.appendChild(parentDoc.createElement('tbody'));

		var numCaptionCells = 0;
		var captionRow = tipLayoutTbody.appendChild(parentDoc.createElement('tr'));
		var captionCell = captionRow.appendChild(parentDoc.createElement('td'));
		captionCell.style.padding = '0px';
		var caption = captionCell.appendChild(parentDoc.createElement('div'));
		caption.className = 'caption';
		if (domLib_isIE50)
		{
			caption.style.height = '100%';
		}

		if (in_options.get('caption').nodeType)
		{
			caption.appendChild(in_options.get('caption').cloneNode(1));
		}
		else
		{
			caption.innerHTML = in_options.get('caption');
		}

		if (in_options.get('type') == 'sticky')
		{
			var numCaptionCells = 2;
			var closeLinkCell = captionRow.appendChild(parentDoc.createElement('td'));
			closeLinkCell.style.padding = '0px';
			var closeLink = closeLinkCell.appendChild(parentDoc.createElement('div'));
			closeLink.className = 'caption';
			if (domLib_isIE50)
			{
				closeLink.style.height = '100%';
			}

			closeLink.style.textAlign = 'right';
			closeLink.style.cursor = domLib_stylePointer;
			// merge the styles of the two cells
			closeLink.style.borderLeftWidth = caption.style.borderRightWidth = '0px';
			closeLink.style.paddingLeft = caption.style.paddingRight = '0px';
			closeLink.style.marginLeft = caption.style.marginRight = '0px';
			if (in_options.get('closeLink').nodeType)
			{
				closeLink.appendChild(in_options.get('closeLink').cloneNode(1));
			}
			else
			{
				closeLink.innerHTML = in_options.get('closeLink');
			}

			closeLink.onclick = function() { domTT_deactivate(tipOwner.id); };
			closeLink.onmousedown = function(in_event) { if (typeof(in_event) == 'undefined') { in_event = event; } in_event.cancelBubble = true; };
			// MacIE has to have a newline at the end and must be made with createTextNode()
			if (domLib_isMacIE)
			{
				closeLinkCell.appendChild(parentDoc.createTextNode("\n"));
			}
		}

		// MacIE has to have a newline at the end and must be made with createTextNode()
		if (domLib_isMacIE)
		{
			captionCell.appendChild(parentDoc.createTextNode("\n"));
		}

		var contentRow = tipLayoutTbody.appendChild(parentDoc.createElement('tr'));
		var contentCell = contentRow.appendChild(parentDoc.createElement('td'));
		contentCell.style.padding = '0px';
		if (numCaptionCells)
		{
			if (domLib_isIE || domLib_isOpera)
			{
				contentCell.colSpan = numCaptionCells;
			}
			else
			{
				contentCell.setAttribute('colspan', numCaptionCells);
			}
		}

		content = contentCell.appendChild(parentDoc.createElement('div'));
		if (domLib_isIE50)
		{
			content.style.height = '100%';
		}
	}
	else
	{
		content = tipObj.appendChild(parentDoc.createElement('div'));
	}

	content.className = 'contents';

	if (in_options.get('content').nodeType)
	{
		content.appendChild(in_options.get('content').cloneNode(1));
	}
	else
	{
		content.innerHTML = in_options.get('content');
	}

	// adjust the width if specified
	if (in_options.has('width'))
	{
		tipObj.style.width = parseInt(in_options.get('width')) + 'px';
	}

	// check if we are overridding the maxWidth
	// if the browser supports maxWidth, the global setting will be ignored (assume stylesheet)
	var maxWidth = domTT_maxWidth;
	if (in_options.has('maxWidth'))
	{
		if ((maxWidth = in_options.get('maxWidth')) === false)
		{
			tipObj.style.maxWidth = domLib_styleNoMaxWidth;
		}
		else
		{
			maxWidth = parseInt(in_options.get('maxWidth'));
			tipObj.style.maxWidth = maxWidth + 'px';
		}
	}

	// HACK: fix lack of maxWidth in CSS for KHTML and IE
	if (maxWidth !== false && (domLib_isIE || domLib_isKHTML) && tipObj.offsetWidth > maxWidth)
	{
		tipObj.style.width = maxWidth + 'px';
	}

	in_options.set('offsetWidth', tipObj.offsetWidth);
	in_options.set('offsetHeight', tipObj.offsetHeight);

	// konqueror miscalcuates the width of the containing div when using the layout table based on the
	// border size of the containing div
	if (domLib_isKonq && tableLayout && !tipObj.style.width)
	{
		var left = document.defaultView.getComputedStyle(tipObj, '').getPropertyValue('border-left-width');
		var right = document.defaultView.getComputedStyle(tipObj, '').getPropertyValue('border-right-width');
		
		left = left.substring(left.indexOf(':') + 2, left.indexOf(';'));
		right = right.substring(right.indexOf(':') + 2, right.indexOf(';'));
		var correction = 2 * ((left ? parseInt(left) : 0) + (right ? parseInt(right) : 0));
		tipObj.style.width = (tipObj.offsetWidth - correction) + 'px';
	}

	// if a width is not set on an absolutely positioned object, both IE and Opera
	// will attempt to wrap when it spills outside of body...we cannot have that
	if (domLib_isIE || domLib_isOpera)
	{
		if (!tipObj.style.width)
		{
			// HACK: the correction here is for a border
			tipObj.style.width = (tipObj.offsetWidth - 2) + 'px';
		}

		// HACK: the correction here is for a border
		tipObj.style.height = (tipObj.offsetHeight - 2) + 'px';
	}

	// store placement offsets from event position
	var offsetX, offsetY;

	// tooltip floats
	if (in_options.get('position') == 'absolute' && !(in_options.has('x') && in_options.has('y')))
	{
		// determine the offset relative to the pointer
		switch (in_options.get('direction'))
		{
			case 'northeast':
				offsetX = in_options.get('offsetX');
				offsetY = 0 - tipObj.offsetHeight - in_options.get('offsetY');
			break;
			case 'northwest':
				offsetX = 0 - tipObj.offsetWidth - in_options.get('offsetX');
				offsetY = 0 - tipObj.offsetHeight - in_options.get('offsetY');
			break;
			case 'north':
				offsetX = 0 - parseInt(tipObj.offsetWidth/2);
				offsetY = 0 - tipObj.offsetHeight - in_options.get('offsetY');
			break;
			case 'southwest':
				offsetX = 0 - tipObj.offsetWidth - in_options.get('offsetX');
				offsetY = in_options.get('offsetY');
			break;
			case 'southeast':
				offsetX = in_options.get('offsetX');
				offsetY = in_options.get('offsetY');
			break;
			case 'south':
				offsetX = 0 - parseInt(tipObj.offsetWidth/2);
				offsetY = in_options.get('offsetY');
			break;
		}

		// if we are in an iframe, get the offsets of the iframe in the parent document
		if (in_options.get('inframe'))
		{
			var iframeObj = domLib_getIFrameReference(window);
			if (iframeObj)
			{
				var frameOffsets = domLib_getOffsets(iframeObj);
				offsetX += frameOffsets.get('left');
				offsetY += frameOffsets.get('top');
			}
		}
	}
	// tooltip is fixed
	else
	{
		offsetX = 0;
		offsetY = 0;
		in_options.set('trail', false);
	}

	// set the direction-specific offsetX/Y
	in_options.set('offsetX', offsetX);
	in_options.set('offsetY', offsetY);
	if (in_options.get('clearMouse') && in_options.get('direction').indexOf('south') != -1)
	{
		in_options.set('mouseOffset', domTT_mouseHeight);
	}
	else
	{
		in_options.set('mouseOffset', 0);
	}

	if (domLib_canFade && typeof(Fadomatic) == 'function')
	{
		if (in_options.get('fade') != 'neither')
		{
			var fadeHandler = new Fadomatic(tipObj, 10, 0, 0, in_options.get('fadeMax'));
			in_options.set('fadeHandler', fadeHandler);
		}
	}
	else
	{
		in_options.set('fade', 'neither');
	}

	// setup mouse events
	if (in_options.get('trail') && typeof(tipOwner.onmousemove) != 'function')
	{
		tipOwner.onmousemove = function(in_event) { domTT_mousemove(this, in_event); };
	}

	if (typeof(tipOwner.onmouseout) != 'function')
	{
		tipOwner.onmouseout = function(in_event) { domTT_mouseout(this, in_event); };
	}

	if (in_options.get('type') == 'sticky')
	{
		if (in_options.get('position') == 'absolute' && domTT_dragEnabled && in_options.get('draggable'))
		{
			if (domLib_isIE)
			{
				captionRow.onselectstart = function() { return false; };
			}

			// setup drag
			captionRow.onmousedown = function(in_event) { domTT_dragStart(tipObj, in_event);  };
			captionRow.onmousemove = function(in_event) { domTT_dragUpdate(in_event); };
			captionRow.onmouseup = function() { domTT_dragStop(); };
		}
	}
	else if (in_options.get('type') == 'velcro')
	{
		tipObj.onmouseout = function(in_event) { if (typeof(in_event) == 'undefined') { in_event = event; } if (!domLib_isDescendantOf(in_event[domLib_eventTo], tipObj)) { domTT_deactivate(tipOwner.id); }};
	}

	if (in_options.get('position') == 'relative')
	{
		tipObj.style.position = 'relative';
	}

	in_options.set('node', tipObj);
	in_options.set('status', 'inactive');
};

// }}}
// {{{ domTT_show()

// in_id is either tip id or the owner id
function domTT_show(in_id, in_event)
{
	// should always find one since this call would be cancelled if tip was killed
	var tooltip = domTT_tooltips.get(in_id);
	var status = tooltip.get('status');
	var tipObj = tooltip.get('node');

	if (tooltip.get('position') == 'absolute')
	{
		var mouseX, mouseY;

		if (tooltip.has('x') && tooltip.has('y'))
		{
			mouseX = tooltip.get('x');
			mouseY = tooltip.get('y');
		}
		else if (!domTT_useGlobalMousePosition || status == 'active' || tooltip.get('delay') == 0)
		{
			var eventPosition = domLib_getEventPosition(in_event);
			var eventX = eventPosition.get('x');
			var eventY = eventPosition.get('y');
			if (tooltip.get('inframe'))
			{
				eventX -= eventPosition.get('scrollX');
				eventY -= eventPosition.get('scrollY');
			}

			// only move tip along requested trail axis when updating position
			if (status == 'active' && tooltip.get('trail') !== true)
			{
				var trail = tooltip.get('trail');
				if (trail == 'x')
				{
					mouseX = eventX;
					mouseY = tooltip.get('mouseY');
				}
				else if (trail == 'y')
				{
					mouseX = tooltip.get('mouseX');
					mouseY = eventY;
				}
			}
			else
			{
				mouseX = eventX;
				mouseY = eventY;
			}
		}
		else
		{
			mouseX = domTT_mousePosition.get('x');
			mouseY = domTT_mousePosition.get('y');
			if (tooltip.get('inframe'))
			{
				mouseX -= domTT_mousePosition.get('scrollX');
				mouseY -= domTT_mousePosition.get('scrollY');
			}
		}

		// we are using a grid for updates
		if (tooltip.get('grid'))
		{
			// if this is not a mousemove event or it is a mousemove event on an active tip and
			// the movement is bigger than the grid
			if (in_event.type != 'mousemove' || (status == 'active' && (Math.abs(tooltip.get('lastX') - mouseX) > tooltip.get('grid') || Math.abs(tooltip.get('lastY') - mouseY) > tooltip.get('grid'))))
			{
				tooltip.set('lastX', mouseX);
				tooltip.set('lastY', mouseY);
			}
			// did not satisfy the grid movement requirement
			else
			{
				return false;
			}
		}

		// mouseX and mouseY store the last acknowleged mouse position,
		// good for trailing on one axis
		tooltip.set('mouseX', mouseX);
		tooltip.set('mouseY', mouseY);

		var coordinates;
		if (domTT_screenEdgeDetection)
		{
			coordinates = domTT_correctEdgeBleed(
				tooltip.get('offsetWidth'),
				tooltip.get('offsetHeight'),
				mouseX,
				mouseY,
				tooltip.get('offsetX'),
				tooltip.get('offsetY'),
				tooltip.get('mouseOffset'),
				tooltip.get('inframe') ? window.parent : window
			);
		}
		else
		{
			coordinates = {
				'x' : mouseX + tooltip.get('offsetX'),
				'y' : mouseY + tooltip.get('offsetY') + tooltip.get('mouseOffset')
			};
		}

		// update the position
		tipObj.style.left = coordinates.x + 'px';
		tipObj.style.top = coordinates.y + 'px';

		// increase the tip zIndex so it goes over previously shown tips
		tipObj.style.zIndex = domLib_zIndex++;
	}

	// if tip is not active, active it now and check for a fade in
	if (status == 'pending')
	{
		// unhide the tooltip
		tooltip.set('status', 'active');
		tipObj.style.display = '';
		tipObj.style.visibility = 'visible';

		var fade = tooltip.get('fade');
		if (fade != 'neither')
		{
			var fadeHandler = tooltip.get('fadeHandler');
			if (fade == 'out' || fade == 'both')
			{
				fadeHandler.haltFade();
				if (fade == 'out')
				{
					fadeHandler.halt();
				}
			}

			if (fade == 'in' || fade == 'both')
			{
				fadeHandler.fadeIn();
			}
		}

		if (tooltip.get('type') == 'greasy' && tooltip.get('lifetime') != 0)
		{
			tooltip.set('lifetimeTimeout', domLib_setTimeout(domTT_runDeactivate, tooltip.get('lifetime'), [tipObj.id]));
		}
	}

	if (tooltip.get('position') == 'absolute')
	{
		domLib_detectCollisions(tipObj);
	}
}

// }}}
// {{{ domTT_close()

// in_handle can either be an child object of the tip, the tip id or the owner id
function domTT_close(in_handle)
{
	var id;
	if (typeof(in_handle) == 'object' && in_handle.nodeType)
	{
		var obj = in_handle;
		while (!obj.id || !domTT_tooltips.get(obj.id))
		{
			obj = obj.parentNode;
	
			if (obj.nodeType != document.ELEMENT_NODE) { return; }
		}

		id = obj.id;
	}
	else
	{
		id = in_handle;
	}

	domTT_deactivate(id);
}

// }}}
// {{{ domTT_deactivate()

// in_id is either the tip id or the owner id
function domTT_deactivate(in_id)
{
	var tooltip = domTT_tooltips.get(in_id);
	if (tooltip)
	{
		var status = tooltip.get('status');
		if (status == 'pending')
		{
			// cancel the creation of this tip if it is still pending
			domLib_clearTimeout(tooltip.get('activateTimeout'));
			tooltip.set('status', 'inactive');
		}
		else if (status == 'active')
		{
			if (tooltip.get('lifetime'))
			{
				domLib_clearTimeout(tooltip.get('lifetimeTimeout'));
			}

			var tipObj = tooltip.get('node');
			if (tooltip.get('closeAction') == 'hide')
			{
				var fade = tooltip.get('fade');
				if (fade != 'neither')
				{
					var fadeHandler = tooltip.get('fadeHandler');
					if (fade == 'out' || fade == 'both')
					{
						fadeHandler.fadeOut();
					}
					else
					{
						fadeHandler.hide();
					}
				}
				else
				{
					tipObj.style.display = 'none';
				}
			}
			else
			{
				tooltip.get('parent').removeChild(tipObj);
				domTT_tooltips.remove(tooltip.get('owner').id);
				domTT_tooltips.remove(tooltip.get('id'));
			}

			tooltip.set('status', 'inactive');
			// unhide all of the selects that are owned by this object
			domLib_detectCollisions(tipObj, true); 
		}
	}
}

// }}}
// {{{ domTT_mouseout()

function domTT_mouseout(in_owner, in_event)
{
	if (!domLib_useLibrary) { return false; }

	if (typeof(in_event) == 'undefined')
	{
		in_event = event;
	}

	var toChild = domLib_isDescendantOf(in_event[domLib_eventTo], in_owner);
	var tooltip = domTT_tooltips.get(in_owner.id);
	if (tooltip && (tooltip.get('type') == 'greasy' || tooltip.get('status') != 'active'))
	{
		// deactivate tip if exists and we moved away from the owner
		if (!toChild)
		{
			domTT_deactivate(in_owner.id);
			try { window.status = window.defaultStatus; } catch(e) {}
		}
	}
	else if (!toChild)
	{
		try { window.status = window.defaultStatus; } catch(e) {}
	}
}

// }}}
// {{{ domTT_mousemove()

function domTT_mousemove(in_owner, in_event)
{
	if (!domLib_useLibrary) { return false; }

	if (typeof(in_event) == 'undefined')
	{
		in_event = event;
	}

	var tooltip = domTT_tooltips.get(in_owner.id);
	if (tooltip && tooltip.get('trail') && tooltip.get('status') == 'active')
	{
		// see if we are trailing lazy
		if (tooltip.get('lazy'))
		{
			domLib_setTimeout(domTT_runShow, domTT_trailDelay, [in_owner.id, in_event]);
		}
		else
		{
			domTT_show(in_owner.id, in_event);
		}
	}
}

// }}}
// {{{ domTT_addPredefined()

function domTT_addPredefined(in_id)
{
	var options = new Hash();
	for (var i = 1; i < arguments.length; i += 2)
	{
		options.set(arguments[i], arguments[i + 1]);
	}

	domTT_predefined.set(in_id, options);
}

// }}}
// {{{ domTT_correctEdgeBleed()

function domTT_correctEdgeBleed(in_width, in_height, in_x, in_y, in_offsetX, in_offsetY, in_mouseOffset, in_window)
{
	var win, doc;
	var bleedRight, bleedBottom;
	var pageHeight, pageWidth, pageYOffset, pageXOffset;

	var x = in_x + in_offsetX;
	var y = in_y + in_offsetY + in_mouseOffset;

	win = (typeof(in_window) == 'undefined' ? window : in_window);

	// Gecko and IE swaps values of clientHeight, clientWidth properties when
	// in standards compliance mode from documentElement to document.body
	doc = ((domLib_standardsMode && (domLib_isIE || domLib_isGecko)) ? win.document.documentElement : win.document.body);

	// for IE in compliance mode
	if (domLib_isIE)
	{
		pageHeight = doc.clientHeight;
		pageWidth = doc.clientWidth;
		pageYOffset = doc.scrollTop;
		pageXOffset = doc.scrollLeft;
	}
	else
	{
		pageHeight = doc.clientHeight;
		pageWidth = doc.clientWidth;

		if (domLib_isKHTML)
		{
			pageHeight = win.innerHeight;
		}

		pageYOffset = win.pageYOffset;
		pageXOffset = win.pageXOffset;
	}

	// we are bleeding off the right, move tip over to stay on page
	// logic: take x position, add width and subtract from effective page width
	if ((bleedRight = (x - pageXOffset) + in_width - (pageWidth - domTT_screenEdgePadding)) > 0)
	{
		x -= bleedRight;
	}

	// we are bleeding to the left, move tip over to stay on page
	// if tip doesn't fit, we will go back to bleeding off the right
	// logic: take x position and check if less than edge padding
	if ((x - pageXOffset) < domTT_screenEdgePadding)
	{
		x = domTT_screenEdgePadding + pageXOffset;
	}

	// if we are bleeding off the bottom, flip to north
	// logic: take y position, add height and subtract from effective page height
	if ((bleedBottom = (y - pageYOffset) + in_height - (pageHeight - domTT_screenEdgePadding)) > 0)
	{
		y = in_y - in_height - in_offsetY;
	}

	// if we are bleeding off the top, flip to south
	// if tip doesn't fit, we will go back to bleeding off the bottom
	// logic: take y position and check if less than edge padding
	if ((y - pageYOffset) < domTT_screenEdgePadding)
	{
		y = in_y + domTT_mouseHeight + in_offsetY;
	}

	return {'x' : x, 'y' : y};
}

// }}}
// {{{ domTT_isActive()

// in_id is either the tip id or the owner id
function domTT_isActive(in_id)
{
	var tooltip = domTT_tooltips.get(in_id);
	if (!tooltip || tooltip.get('status') != 'active')
	{
		return false;
	}
	else
	{
		return true;
	}
}

// }}}
// {{{ domTT_runXXX()

// All of these domMenu_runXXX() methods are used by the event handling sections to
// avoid the circular memory leaks caused by inner functions
function domTT_runDeactivate(args) { domTT_deactivate(args[0]); }
function domTT_runShow(args) { domTT_show(args[0], args[1]); }

// }}}
// {{{ domTT_replaceTitles()

function domTT_replaceTitles(in_decorator)
{
	var elements = domLib_getElementsByClass('tooltip');
	for (var i = 0; i < elements.length; i++)
	{
		if (elements[i].title)
		{
			var content;
			if (typeof(in_decorator) == 'function')
			{
				content = in_decorator(elements[i]);
			}
			else
			{
				content = elements[i].title;
			}

			content = content.replace(new RegExp('\'', 'g'), '\\\'');
			elements[i].onmouseover = new Function('in_event', "domTT_activate(this, in_event, 'content', '" + content + "')");
			elements[i].title = '';
		}
	}
}

// }}}
// {{{ domTT_update()

// Allow authors to update the contents of existing tips using the DOM
function domTT_update(handle, content, type)
{
	// type defaults to 'content', can also be 'caption'
	if (typeof(type) == 'undefined')
	{
		type = 'content';
	}

	var tip = domTT_tooltips.get(handle);
	if (!tip)
	{
		return;
	}

	var tipObj = tip.get('node');
	var updateNode;
	if (type == 'content')
	{
		// <div class="contents">...
		updateNode = tipObj.firstChild;
		if (updateNode.className != 'contents')
		{
			// <table><tbody><tr>...</tr><tr><td><div class="contents">...
			updateNode = updateNode.firstChild.firstChild.nextSibling.firstChild.firstChild;
		}
	}
	else
	{
		updateNode = tipObj.firstChild;
		if (updateNode.className == 'contents')
		{
			// missing caption
			return;
		}

		// <table><tbody><tr><td><div class="caption">...
		updateNode = updateNode.firstChild.firstChild.firstChild.firstChild;
	}

	// TODO: allow for a DOM node as content
	updateNode.innerHTML = content;
}

// }}}



/* XXXXXXXXXX end of /home/jmonke2/public_html/wiki/lib/scripts/domTT.js XXXXXXXXXX */



/* XXXXXXXXXX begin of /home/jmonke2/public_html/wiki/lib/tpl/default/script.js XXXXXXXXXX */



/* XXXXXXXXXX end of /home/jmonke2/public_html/wiki/lib/tpl/default/script.js XXXXXXXXXX */

addInitEvent(function(){ ajax_qsearch.init('qsearch__in','qsearch__out'); });
addInitEvent(function(){ addEvent(document,'click',closePopups); });
addInitEvent(function(){ addTocToggle(); });
addInitEvent(function(){ updateAccessKeyTooltip(); });
addInitEvent(function(){ scrollToMarker(); });
addInitEvent(function(){ focusMarker(); });
