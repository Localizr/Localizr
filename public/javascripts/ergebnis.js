var map = new L.Map('map');

// create the tile layer with correct attribution
var osmUrl='http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
var osmAttrib='Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors';
var osm = new L.TileLayer(osmUrl, {minZoom: 14, maxZoom: 18, attribution: osmAttrib});
map.setView(new L.LatLng(mapViewLat, mapViewLng),16);
map.addLayer(osm);

var geo = new GeocoderJS.createGeocoder('openstreetmap');

var markers = L.markerClusterGroup();
var poiMarkers = L.layerGroup();
var eventMarkers = L.layerGroup();
var imageMarkers = L.layerGroup();

var MarkerIcon = L.Icon.extend({
    options: {
        iconSize:     [32, 42],
        iconAnchor:   [16, 42],
        popupAnchor:  [0, -40]
    }
});

var poiIcon = new MarkerIcon({iconUrl: 'assets/images/poimarker.png'}),
    eventIcon = new MarkerIcon({iconUrl: 'assets/images/eventmarker.png'}),
    imageIcon = new MarkerIcon({iconUrl: 'assets/images/imagemarker.png'});

function addMarker(name,lat,lon,type, element) {
    if(type == "image"){
        if(_.findWhere(panoramioImages, {sTitle: element.sTitle}).marker){
            return;
        }
    }else if(type == "poi"){
        if(_.findWhere(list, {label: element.label}).marker){
            return;
        }
    }else if(type == "event"){
        if(_.findWhere(events, {title: element.title}).marker){
            return;
        }
    }

    var icon;
    if(type == "image"){
        icon = imageIcon;
    }else if(type == "poi"){
        icon = poiIcon;
    }else if(type == "event"){
        icon = eventIcon;
    }
    // console.log(name + " " + lat + " " + lon+" "+type);

    var marker = new L.marker(new L.LatLng(lat, lon), {icon: icon});
    marker.bindPopup(name);

    if(type == "image"){
        index = panoramioImages.indexOf(element);
        panoramioImages[index].marker = marker;
    }else if(type == "poi"){
        index = list.indexOf(element);
        list[index].marker = marker;
    }else if(type == "event"){
        index = events.indexOf(element);
        events[index].marker = marker;
    }
    markers.addLayer(marker);
}

function removeMarkersOfType(type){
    if(type == "Images"){
        _.each(panoramioImages, function(image) {
            markers.removeLayer(image.marker);
        });
    }else if(type == "POI"){
        _.each(list, function(poi) {
            markers.removeLayer(poi.marker);
        });
    }else if(type == "Events"){
        _.each(events, function(event, index){
            if(event.marker != null){
                markers.removeLayer(event.marker);
            }
        });
    }
}

function showMarkersOfType(type){
    if(type == "Images"){
        _.each(panoramioImages, function(image) {
            markers.addLayer(image.marker);
        });
    }else if(type == "POI"){
        _.each(list, function(poi) {
            markers.addLayer(poi.marker);
        });
    }else if(type == "Events"){
        _.each(events, function(event, index){
            if(event.marker != null) {
                markers.addLayer(event.marker);
            }
        });
    }
}

function processGeocode(event){
    return function(callback){
        geo.geocode(event.address + " " + cityName, function(result) {
            //console.log(result);
            if(result.length > 0){
                event.lat = result[0].latitude;
                event.lon = result[0].longitude;
                addMarker(event.title+" " + event.datetime,event.lat,event.lon,"event", event);
            }
            callback();
        })

    }
}

function initMap(){

    var requests = [];

    async.parallel(requests, function(){
        // Possible callback
    });

    _.each(list, function(poi) {
        addMarker(poi.label, poi.lat, poi.lon,"poi", poi);
    });

    _.each(events, function(event, index){
        addMarker(event.title, event.latitude, event.longitude, "event", event);
    });

    _.each(panoramioImages, function(image) {
        addMarker(image.sTitle+" | Author: "+image.owner_name+"<a class='link-lightbox' href='"+image.sLargeURL+"'><img src=\""+image.sURL+"\" width=\"300px\"></a>", image.dLat, image.dLng,"image", image);
    });
    
    $(".scrollPictureToMap").click(function() {
    	scrollToMap($(this));
    });
    
    $(".scrollPOIToMap").click(function() {
    	scrollToMap($(this));
    	
    	$("div.recommendation-container").hide();
    	
    	if($(this).parent().children(".recommendation-container").length > 0){
    		$(this).parent().find("div.recommendation-container").show();
    	} else {
	    	var d = { 'name': "\""+$(this).attr('data-uri')+"\"", 'lat': mapViewLat, 'lon': mapViewLng};
	    	//console.log(d);
	    	$.ajax({
	    	    type :  "POST",
	    	    dataType: 'json',
	    	    contentType : 'application/json',
	    	    //data: d,
	    	    data: JSON.stringify(d),
	    	    context: $(this),
	    	    url  :  "/recommendation",
	    	        success: function(data){
	    	        	
	    	        	//console.log(list);
	    	        	
	    	            var container = $('<div>').attr("class", "recommendation-container").appendTo($(this).parent());
	    	            $('<h4/>').html("Maybe you are also interested in...").appendTo(container);
	    	            
	    	            var poilist = $('<ul/>').attr("class","recommendation").appendTo(container);
	    	            
	    	            //_.each(data, function(poi) {
	    	            $.each(data.listPOIs, function(idx, poi) {
	    	            	var indexArray = list.push(poi)-1;
	    	                addMarker(poi.label, poi.lat, poi.lon,"poi", poi);
	    	                
	    	                var listitem = $('<li/>').appendTo(poilist);
	    	                
	    	                
	        	            $('<a/>', {
	    					    'class': 'fancybox-website link-poi link-wikipedia',
	    					    'title':'Read more about '+poi.label,
	    					    'html': 'Wikipedia',
	    					    'href': poi.wikilink
	    					}).appendTo(listitem);
	        	            
	        	            var title = 'Show '+poi.label+' on the map'
	        	            $('<a/>', {
	        	            	'class': 'scrollPOIToMapWOAjax link-poi link-map',
	        	            	'data-uri':poi.resource,
	        	            	'title': title,
	        	            	'href': '#',
	        	            	'data-type':'poi',
	        	            	'data-lat':poi.lat,
	        	            	'html': poi.label,
	        	            	'data-lon':poi.lon,
	        	            	'data-index':indexArray
	        	            }).appendTo(listitem);
	        	            
	    	            });
	    	            
	    	            $( ".scrollPOIToMapWOAjax" ).on( "click", function() {
	    	            	scrollToMap($(this));
	    	        	});
	    	        }
	    	});
    	}
    });



    $(".scrollEventToMap").click(function() {
        var index = $(this).attr('data-index');
        var lat = events[index].latitude;
        var lon = events[index].longitude;

        map.panTo(new L.LatLng(lat, lon));

        markers.zoomToShowLayer(events[index].marker, function () {
            events[index].marker.openPopup();
        });

        resize("65%", "35%");
    });

    map.addLayer(poiMarkers);
    map.addLayer(eventMarkers);
    map.addLayer(imageMarkers);

    var overlayMaps = {
        "POI": poiMarkers,
        "Images": imageMarkers,
        "Events": eventMarkers
    };

    var control = L.control.layers(null, overlayMaps, {
            collapsed: false,
            position: 'topleft'
        }
    );

    control.addTo(map);
    map.addLayer(markers);

    map.on('overlayadd', function (a) {
        showMarkersOfType(a.name);
    });
    map.on('overlayremove', function (a) {
        removeMarkersOfType(a.name);
    });
}

function scrollToMap(data){
    var lat = data.attr('data-lat');
    var lon = data.attr('data-lon');
    var index = data.attr('data-index');

    //console.log(data.attr('data-lat'), data.attr('data-lon'), data.attr('data-index'));

    map.panTo(new L.LatLng(lat, lon));

    if(data.attr('data-type') == "poi"){
        if(!list[index].marker && _.findWhere(list, {label: list[index].label}).marker){
            list[index].marker = _.findWhere(list, {label: list[index].label}).marker
        }
        
        markers.zoomToShowLayer(list[index].marker, function () {
            list[index].marker.openPopup();
        });
    }else if(data.attr('data-type') == "image"){
        markers.zoomToShowLayer(panoramioImages[index].marker, function () {
            panoramioImages[index].marker.openPopup();
        });
    }

    resize("65%", "35%");
}

function initTabs() {
	$('#myTab a').click(function (e) {
  	  e.preventDefault();
	  $(this).tab('show');
	  resize($(this).attr("data-map"), $(this).attr("data-container"));
	});
}


function initLightbox() {
	// Links in IFRame
	$("a.fancybox-website").fancybox({
        type: 'iframe'
    });
	// BILDER
	$("a.link-lightbox").fancybox({
		maxWidth: '90%',
		maxHeight: '90%',
		prevEffect	: 'none',
		nextEffect	: 'none'
	});
}

function initImageGallery() {
	$(".action-container").hover(
			function() {
				
				$(this).animate({
					backgroundColor: 'rgba(0,0,0,0.65)'
			}, 100);
				$(this).find("a").fadeIn(200);
				
		}, function() {

			$(this).find("a").fadeOut(200);
				$(this).animate({
					backgroundColor: 'rgba(0,0,0,0)'
			}, 100);
			
		}
	);
	$('#image-container').justifiedGallery({
		  rowHeight : 250,
		  lastRow : 'justify',
		  margin: 4,
		  sizeRangeSuffixes : {
			lt100 : '', 
			lt240 : '', 
			lt320 : '', 
			lt500 : '', 
			lt640 : '', 
			lt1024 : ''
		  }
		 
		});
}
	
	
function resize(map, container) {
	var f = ($("#outer-map").width() / $('#outer-map').parent().width() * 100)+"%";
	
	if(map != f)
	  $(function () {
	    $("#outer-map").animate({
	       width: map
	    }, { duration: 200, queue: false });
	    $("#outer-container").animate({
	       width: container
	    }, { duration: 200, queue: false });
	});
}

$(function() {
	initMap();
	initTabs();
	initLightbox();
	initImageGallery();
});
