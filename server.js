var http = require('http');
var action = require('./action1');


function onRequest(request, response){
    response.writeHead(200,{'Content-Type':'html'});
	// response.writeHead("Access-Control-Allow-Origin", "*");
	// response.writeHead("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    response.write('<!DOCTYPE html>');
    response.write('<html>');
    response.write('<head>');
    response.write("<script type='text/javascript' src='http://localhost:3000/action.js'>");

    response.write('</script>');
	
	response.write('<style>');
	response.write('label {display: inline-block;margin: 10px;}');
	response.write('</style>');
	
    response.write('</head>');
    response.write("<body onload='populate()'>")
    response.write("<div id='header'></div>")
	response.write("<div id='bod'></div>")
	
	
    response.write('</body>')
    response.write('</html>');
	

    response.end();
}

http.createServer(onRequest).listen(8000);   