
document.getElementById('closenav').style.display = 'none';
document.getElementById('homenav').style.display = 'block';

/* Set the width of the side navigation to 250px and the left margin of the page content to 250px */
function openNav() {
    document.getElementById('homenav').style.display = 'none';
	document.getElementById('closenav').style.display = 'block';
	document.getElementById("mySidenav").style.width = "285px";
    document.getElementById("main").style.marginLeft = "285px";
}

/* Set the width of the side navigation to 0 and the left margin of the page content to 0 */
function closeNav() {
    document.getElementById('closenav').style.display = 'none';
	document.getElementById('homenav').style.display = 'block';
	document.getElementById("mySidenav").style.width = "0";
    document.getElementById("main").style.marginLeft = "0";
}

function snackbar() {
    // Get the snackbar DIV
    var x = document.getElementById("snackbar")

    // Add the "show" class to DIV
    x.className = "show";

    // After 3 seconds, remove the show class from DIV
    setTimeout(function(){ x.className = x.className.replace("show", ""); }, 2000);
}

var i = 0;
var random = Math.floor((Math.random() * 3) + 1);
var txt = 'What\'s new?'; 
var txt2 = 'Share your story';
var txt3 = 'Show your friends some love';
var txt4 = 'Post your thoughts';
var txtEr = '404 - Something went wrong, no fancy stuff for you :(';
var speed = 50; /* The speed/duration of the effect in milliseconds */

function typeWriter() {
	
	if (random == 1){
		if (i < txt.length) {
			document.getElementById("postArea").placeholder += txt.charAt(i);
			i++;
			setTimeout(typeWriter, speed);
		}
	} else if (random == 2){
		if (i < txt2.length) {
			document.getElementById("postArea").placeholder += txt2.charAt(i);
			i++;
			setTimeout(typeWriter, speed);
		}
	} else if (random == 3){
		if (i < txt3.length) {
			document.getElementById("postArea").placeholder += txt3.charAt(i);
			i++;
			setTimeout(typeWriter, speed);
		}
	} else if (random == 4){
		if (i < txt4.length) {
			document.getElementById("postArea").placeholder += txt4.charAt(i);
			i++;
			setTimeout(typeWriter, speed);
		}
	} 
}

