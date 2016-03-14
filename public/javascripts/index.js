$(document).ready(function() {
    $("#iconSourceLink").fancybox({
        type: 'inline',
        maxWidth: '90%',
        maxHeight: '90%',
        prevEffect	: 'none',
        nextEffect	: 'none'
    });

    $("#gobutton").prop("disabled", true);
    

    $("#gobutton").click(function() {
        fillInAddress();
    	return false;
    });
    $("#gogps").click(function() {
    	geolocate();
    	return false;
    });

    $(".spinner-container").show();
});


$(document).keypress(
	    function(event){
	     if (event.which == '13') {
	        event.preventDefault();
	      }
});

var placeSearch, autocomplete;
var componentForm = {
  locality: 'long_name',
  administrative_area_level_1: 'short_name',
  country: 'long_name'
};

function initAutocomplete() {
    $(".spinner-container").hide();
    autocomplete = new google.maps.places.Autocomplete(($('#autocomplete')[0]),{types: ['(cities)']});
    autocomplete.addListener('place_changed', function(){
        $("#gobutton").prop("disabled", false);
    });
}

function fillInAddress() {
  $(".spinner-container").show();
  var place = autocomplete.getPlace();

  $("#lat").val(place.geometry.location.lat());
  $("#lon").val(place.geometry.location.lng());
  $("#name").val(place.name);
  
  $("#search-form").submit();
}
// [END region_fillform]

// [START region_geolocation]
// Bias the autocomplete object to the user's geographical location,
// as supplied by the browser's 'navigator.geolocation' object.
function geolocate() {
  if (navigator.geolocation) {
	  $(".spinner-container").show();
    navigator.geolocation.getCurrentPosition(function(position) {
        var lat = position.coords.latitude;
        var lng = position.coords.longitude;

        var geocoder =  new google.maps.Geocoder();

        var latlng = new google.maps.LatLng(lat, lng);
        geocoder.geocode({
          'latLng': latlng
        }, function (results, status) {
          if (status === google.maps.GeocoderStatus.OK) {
            if (results[0]) {
                $("#autocomplete").val(results[0].formatted_address);
                $("#lat").val(results[0].geometry.location.lat());
                $("#lon").val(results[0].geometry.location.lng());
                $("#name").val(results[0].address_components[3].long_name);
                $("#search-form").submit();
            } else {
                //console.log("NO SUCCESS")
            }
          }
        });

    });
  }
}
// [END region_geolocation]