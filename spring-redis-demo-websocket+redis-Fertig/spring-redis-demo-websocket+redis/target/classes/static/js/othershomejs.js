document.getElementById("snackbar").style.display = 'none';
document.getElementById("followerCheck").style.display = 'none';
document.getElementById("following").style.display = 'none';

function snackbar() {
	document.getElementById("snackbar").innerHTML = "You can't follow a person twice, silly!";
    var x = document.getElementById("snackbar")
    x.className = "show";
    setTimeout(function(){ x.className = x.className.replace("show", ""); }, 2500);
}

function snackbar2() {
	document.getElementById("snackbar").innerHTML = "Nope";
    var x = document.getElementById("snackbar")
    x.className = "show";
    setTimeout(function(){ x.className = x.className.replace("show", ""); }, 2500);
}