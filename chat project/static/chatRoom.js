var timeoutID;
var timeout = 45000;
var newContent = ""

function setup() {
	document.getElementById("theButton").addEventListener("click", Post, true);

	timeoutID = window.setTimeout(poller, timeout);
}

function Post() {
	var httpRequest = new XMLHttpRequest();

	if (!httpRequest) {
		alert('Cannot create an XMLHTTP instance');
		return false;
	}

	var content = document.getElementById("content").value
	httpRequest.onreadystatechange = function() { handlePost(httpRequest, content) };

	httpRequest.open("POST", "/new_message");
	httpRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

	var data;
	data = "content=" + content + "\n"

	httpRequest.send(data);
}

function handlePost(httpRequest, content) {
	if (httpRequest.readyState === XMLHttpRequest.DONE) {
		if (httpRequest.status === 200) {
			newContent = newContent + content + "<br />"
			document.getElementById("newMessage").innerHTML = newContent;
			clearInput();
		} else {
			alert("There was a problem with the post request.");
		}
	}
}

function poller() {
	var httpRequest = new XMLHttpRequest();

	if (!httpRequest) {
		alert('Giving up :( Cannot create an XMLHTTP instance');
		return false;
	}

	httpRequest.onreadystatechange = function() { handlePoll(httpRequest) };
	httpRequest.open("GET", "/items");
	httpRequest.send();
}

function handlePoll(httpRequest) {
	if (httpRequest.readyState === XMLHttpRequest.DONE) {
		if (httpRequest.status === 200) {
			document.getElementById("newMessage").innerText = newContent;
			timeoutID = window.setTimeout(poller, timeout);
		} else {
			alert("There was a problem with the poll request.  you'll need to refresh the page to recieve updates again!");
		}
	}
}

function clearInput() {
	document.getElementById("content").value = "";
}



window.addEventListener("load", setup, true);
