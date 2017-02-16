function changeColor() {
	$(".balance:not(:contains('-'))").css( "color", "green");
	$(".balance:contains('-')").css( "color", "red");
	
	$(".colored:contains('+')").css( "color", "green");
	$(".colored:contains('-')").css( "color", "red");
}

$(function() { 	
	changeColor();
 }); 