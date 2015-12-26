$( document ).ready(function() {
    $("#advancedSearch").fancybox({
        type: 'inline',
        maxWidth: '90%',
        maxHeight: '90%',
        prevEffect	: 'none',
        nextEffect	: 'none'
    });

    navigator.geolocation.getCurrentPosition(
	    function(position){
	        // wenn Positionsbestimmung geklappt hat:
	    	$("#lat").val(position.coords.latitude);
	    	$("#lon").val(position.coords.longitude);
	    	$("#search-by-gps").show();
	    },
	    function(){
	        // wenn Positionsbestimmung einen Fehler erzeugt hat (z.B. weil Sie vom User ablehnt wurde).
	        // document.getElementById('beispiel').innerHTML ='Die Position konnte nicht ermittelt werden';
	    }
	);

    $("#iconSourceLink").fancybox({
        type: 'inline',
        maxWidth: '90%',
        maxHeight: '90%',
        prevEffect	: 'none',
        nextEffect	: 'none'
    });
});