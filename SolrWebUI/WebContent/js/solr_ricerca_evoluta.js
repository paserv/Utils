var solrCallback = "solrCallback";
var solrResponseFormat = "json";
var solrBaseUrl = "http://10.24.4.127/collaudo/solr";
var solrCollection = "ricevo";
var maxRows = 10;
var nextPage = 0;
var previousPage = 0;
var currentPage = 0;

function test() {
	Materialize.toast('Test', 1000);
}

function showModal(doc) {
	$('#modalHeader').html("Id usage: " + doc.id_usage);
	$('#modal-content').html("");
	for (var key in doc) {
		  if (doc.hasOwnProperty(key)) {
			  if(doc[key][0] && doc[key][0] !== "null") {
				  $('#modal-content').append("<li class='collection-item'><div>" + key + ": " + doc[key] + "<i class='material-icons left blue-text'>navigate_next</i></div></li>");
			  }
		  }
		}
	$('#modalWindow').openModal();
}

function cercaTestoLiberoClick() {
	nextPage = 0;
	previousPage = 0;
	currentPage = 0;
	cercaSolr(0);
}

function cercaSolrPrev(stPage) {
	currentPage = currentPage - 1;
	cercaSolr(stPage);
}

function cercaSolrNext(stPage) {
	currentPage = currentPage + 1;
	cercaSolr(stPage);
}

function cercaSolr(stPage) {
	$("#preloader").show();
	$("#navig").hide();
	var query = $("#free_text").val();
	if (query) {
		$("#solr_result").html("");
		$("#solr_num_result").html("");
		cercaTestoLibero(solrBaseUrl + "/" + solrCollection + "/select?q=" + query + "&wt=" + solrResponseFormat + "&json.wrf=" + solrCallback + "&rows=" + maxRows + "&hl=true&start=" + stPage);
	} else {
		Materialize.toast('Inserisci testo da ricercare', 2000);
		$("#preloader").hide();
		$("#previousSearch").html("");
		$("#nextSearch").html("");
	}
}

function cercaTestoLibero(url) {
	$.ajax({
			type: 'GET',
			url: url,
			jsonpCallback: solrCallback,
			contentType: "application/json",
			dataType: "jsonp",
			success: function(json) {
				showTestoLiberoResult(json);
			},
			error: function(e) {
				console.log(e.message);
			}
		});
}

function showTestoLiberoResult(json) {
	var jArray = [];
	if (!$.isEmptyObject(json.highlighting)) {
		$("#preloader").hide();
		$.each(json.highlighting, function(key, doc) {
			$("#solr_result").append("<div>");
			
			var stringJson = JSON.stringify(doc).replace(/'/g, '&#39;');
			
			$("#solr_result").append("<h3 class='solr-res-title'><a class='modal-link' onclick='showModal(" + stringJson + ")'>Id Usage: "+ key + "</a></h3>");
			
			for (var currkey in doc) {
				if (doc[currkey][0] && doc[currkey][0] !== "null") {
					$("#solr_result").append("<span class='solr-res'>" + currkey + ": " + doc[currkey][0] + " - </span>");
				}
			}
						
			$("#solr_result").append("</div>");
			
		});
		
		var numFound = json.response.numFound;
		var startPage = json.response.start;
		previousPage = currentPage - 1;
		nextPage = currentPage + 1;
		$("#solr_num_result").html("Trovati " + formatMiles(numFound) + " risultati - Pagina " + (currentPage + 1) + " di " + formatMiles((Math.ceil(numFound/maxRows))));
		if (previousPage >= 0) {
			$("#previousSearch").html("<a href='#' class='right' onclick='cercaSolrPrev(" + (previousPage*maxRows) + ")'><i class='material-icons aligned-img'>keyboard_arrow_left</i>Precedenti</a>");
		} else {
			$("#previousSearch").html("<div class='right'><i class='material-icons aligned-img'>keyboard_arrow_left</i>Precedenti</div>");
		}
		
		if ((currentPage+1)*maxRows <= numFound) {
			$("#nextSearch").html("<a href='#' onclick='cercaSolrNext(" + (nextPage*maxRows) + ")'>Successivi<i class='material-icons aligned-img'>keyboard_arrow_right</i></a>");
		} else {
			$("#nextSearch").html("<div>Successivi<i class='material-icons aligned-img'>keyboard_arrow_right</i></div>");
		}
	
		$("#navig").show();
		
	} else {
		$("#solr_result").html("Non ci sono risultati");
		$("#preloader").hide();
		$("#previousSearch").html("");
		$("#nextSearch").html("");
	}
	
}


function formatMiles(nStr) {
    nStr += '';
    x = nStr.split('.');
    x1 = x[0];
    x2 = x.length > 1 ? '.' + x[1] : '';
    var rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
            x1 = x1.replace(rgx, '$1' + '.' + '$2');
    }
    return x1 + x2;
}

