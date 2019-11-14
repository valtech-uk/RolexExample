var express = require("express");
var cors = require("cors");
var app = express();
var fs = require("fs");
var url = require("url");
var imageDir = "/public/images/"

app.use(cors({origin: '*'}));

var publicDir = require('path').join(__dirname,'/public');
app.use(express.static(publicDir));

app.get("/head", (req, res, next) => {
 res.setHeader('Access-Control-Allow-Origin', 'http://localhost:8000');
 res.json(["<div style='font-family:Arial, Helvetica, sans-serif; font-size:40px;font-weight:bold' onclick='iter()'>Test Page</div>"]);
});

app.get("/image", (req, res, next) => {
 res.setHeader('Access-Control-Allow-Origin', 'http://localhost:8000');

});

app.get("/body", (req, res, next) => {
 res.setHeader('Access-Control-Allow-Origin', 'http://localhost:8000');
 res.json(["<label>text</label><input type='text' id='box'/><p><label>menu</label><select id='menu'><option value='1'>1</option><option value='2'>2</option><option value='3'>3</option></select></p><p><label>form</label><form id='myform'><input type='radio' id='group1' name='group' value='one'><label>one</label><input type='radio' id='group2' name='group' value='two'><label>two</label><input type='radio' id='group3' name='group' value='three'><label>three</label></form></p><p><input type='button' value='submit' id='go' onclick='proceed()'/></p>"]);
});

app.get("/hello", (req, res, next) => {
 res.setHeader('Access-Control-Allow-Origin', 'http://localhost:8000');
 res.json(["hello world?"]);
});
	

app.get('/action.js', function(req, res){
    // Website you wish to allow to connect
    res.setHeader('Access-Control-Allow-Origin', 'http://localhost:8000');
    res.sendFile(__dirname + '/action1.js');
});

app.listen(3000, () => {
 console.log("Server running on port 3000");
});