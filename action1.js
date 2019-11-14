function httpGet(theUrl)
{
    var xmlHttp = new XMLHttpRequest();
	
    xmlHttp.open( "GET", theUrl, false ); // false for synchronous request
	xmlHttp.setRequestHeader('Access-Control-Allow-Origin', 'http://localhost:8000');
    xmlHttp.send( null );
    return xmlHttp.responseText;
	
}

function proceed(){
	document.getElementById('bod').innerHTML = "<div style='border:3px solid gray; border-radius:3px; background-color: whitesmoke'><img src='http://localhost:5000/true.png'/></div>";
}

function populate(){
	 
		var v = JSON.parse(httpGet('http://localhost:3000/head'));
		document.getElementById('header').innerHTML = v[0];

	    var b = JSON.parse(httpGet('http://localhost:3000/body'));
		document.getElementById('bod').innerHTML = b[0];

		var c = JSON.parse(httpGet('https://google.com'));
 
}


 


