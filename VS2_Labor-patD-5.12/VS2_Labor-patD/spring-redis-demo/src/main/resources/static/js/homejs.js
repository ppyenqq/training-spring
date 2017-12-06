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

