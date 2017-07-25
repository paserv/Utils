var solrCallback = "solrCallback";
var solrResponseFormat = "json";
var solrBaseUrl = "http://10.24.4.127/collaudo/solr";
var solrCollection = "ricevo";
var maxRows = 100;
var nextPage = 0;
var previousPage = 0;
var currentPage = 0;

function hidefields() {
	$( "#hidebtn" ).hide();
	$( "#showbtn" ).show();
	$( "#fields" ).hide();
}

function showfields() {
	$( "#hidebtn" ).show();
	$( "#showbtn" ).hide();
	$( "#fields" ).show();
}

function showDialog(id) {
	var detailId = "#" + id + "_d";
	$(detailId).dialog({
		modal: true,
		width: 800,
        height: 400,
	    buttons: {
	        "Chiudi": function() {
	            $( this ).dialog( "close" );
	         	},
            }
	        });
}

function callADCService(idUsage) {
	Materialize.toast("Calling Fake ADC Service", 1000);
	
	var t = $('#adctable').DataTable();
	t.clear().draw();
	t.row.add( [
	            idUsage,
	            "QWE",
	            "321",
	            "12-03-2016",
	            "KJH"
	        ] ).draw( false );
	t.row.add( [
	            idUsage,
	            "EWQ",
	            "123",
	            "11-03-2016",
	            "EWQ"
	        ] ).draw( false );
	t.row.add( [
	            idUsage,
	            "IOP",
	            "321",
	            "12-03-2016",
	            "POI"
	        ] ).draw( false );
//	var jArray = [];
//	jArray.push({
//	        "Id-Usage": idUsage,
//	        "Canale-Accettazione": "QWE",
//	        "ID-Prodotto/Servizio": "123",
//	        "Data/Ora-Accettazione": "11-03-2016",
//	        "Fase": "EWQ",
//	    });
//	jArray.push({
//        "Id-Usage": idUsage,
//        "Canale-Accettazione": "EWQ",
//        "ID-Prodotto/Servizio": "321",
//        "Data/Ora-Accettazione": "12-03-2016",
//        "Fase": "KJH",
//    });
//	jArray.push({
//        "Id-Usage": idUsage,
//        "Canale-Accettazione": "IOP",
//        "ID-Prodotto/Servizio": "123",
//        "Data/Ora-Accettazione": "13-03-2016",
//        "Fase": "POI",
//    });
//	
//	var dynatable = $('#adctable').dynatable({
//		table: {
//		    defaultColumnIdStyle: 'trimDash'
//		  },
//		  dataset: {
//		    records: jArray
//		  }
//		}).data('dynatable');
//	dynatable.settings.dataset.originalRecords = jArray;
//	dynatable.process();
}

function solrRequest() {
	$('#adctable').DataTable().clear().draw();
	$('#solrtable').DataTable().clear().draw();
	$( "#divSolr" ).hide();
	$( "#divAdc" ).hide();
	var query = buildQuery();
	Materialize.toast(query, 5000);
	if (query !== null) {
		cercaChiaveDebole(solrBaseUrl + "/" + solrCollection + "/select?q=" + query + "&wt=" + solrResponseFormat + "&json.wrf=" + solrCallback + "&rows=" + maxRows);
	}
}

function showSolrResult(json) {
	console.log(json);
	var jArray = [];	
	
	var t = $('#solrtable').DataTable();
	
	$.each(json.response.docs, function(key, doc) {
		console.log(doc.codice_prodotto_servizio);
		t.row.add( [
		            "<div class='link' id='" + doc.id_usage + "' onclick=callADCService('" + doc.id_usage + "')>" + doc.id_usage + "</div>",
		            doc.canale,
		            doc.codice_prodotto_servizio,
		            doc.data,
		            doc.fase,
		            "<div id='" + doc.id_usage + "_p' class='link' onclick='showDialog(" + doc.id_usage + ")'>Cliente: " + doc.cliente_nominativo + "...</div>" +
        		    "<div id='" + doc.id_usage + "_d' style='display:none'>" +
        			"<div><strong>Cliente</strong>: " + doc.cliente_nominativo + " Dati Fiscali: " + doc.cliente_dati_fiscali + " Indirizzo: " + doc.cliente_indirizzo + "</div>" + 
        			"<div><strong>Mittente</strong>: " + doc.mittente_nominativo + " Dati Fiscali: " + doc.mittente_dati_fiscali + " Indirizzo: " + doc.mittente_indirizzo + "</div>" +
        			"<div><strong>Destinatario</strong>: " + doc.destinatario_nominativo + " Dati Fiscali: " + doc.destinatario_dati_fiscali + " Indirizzo: " + doc.destinatario_indirizzo + "</div>" +
        			"<div><strong>Intermediario</strong>: " + doc.intermediario_nominativo + " Dati Fiscali: " + doc.intermediario_dati_fiscali + " Indirizzo: " + doc.intermediario_indirizzo + "</div>" +
        			"<div><strong>Richiedente</strong>: " + doc.richiedente_nominativo + " Dati Fiscali: " + doc.richiedente_dati_fiscali + " Indirizzo: " + doc.richiedente_indirizzo + "</div>" +
        			"</div>"
		        ] ).draw( false );
//		jArray.push({
//	        "Id-Usage": "<div class='link' id='" + doc.id_usage + "' onclick=callADCService('" + doc.id_usage + "')>" + doc.id_usage + "</div>",
//	        "Canale-Accettazione": doc.canale,
//	        "ID-Prodotto/Servizio": doc.codice_prodotto_servizio,
//	        "Data/Ora-Accettazione": doc.data,
//	        "Fase": doc.fase,
//	        "Dettagli": "<div id='" + doc.id_usage + "_p' class='link' onclick='showDialog(" + doc.id_usage + ")'>Cliente: " + doc.cliente_nominativo + "...</div>" +
//	        		    "<div id='" + doc.id_usage + "_d' style='display:none'>" +
//	        			"<div><strong>Cliente</strong>: " + doc.cliente_nominativo + " Dati Fiscali: " + doc.cliente_dati_fiscali + " Indirizzo: " + doc.cliente_indirizzo + "</div>" + 
//	        			"<div><strong>Mittente</strong>: " + doc.mittente_nominativo + " Dati Fiscali: " + doc.mittente_dati_fiscali + " Indirizzo: " + doc.mittente_indirizzo + "</div>" +
//	        			"<div><strong>Destinatario</strong>: " + doc.destinatario_nominativo + " Dati Fiscali: " + doc.destinatario_dati_fiscali + " Indirizzo: " + doc.destinatario_indirizzo + "</div>" +
//	        			"<div><strong>Intermediario</strong>: " + doc.intermediario_nominativo + " Dati Fiscali: " + doc.intermediario_dati_fiscali + " Indirizzo: " + doc.intermediario_indirizzo + "</div>" +
//	        			"<div><strong>Richiedente</strong>: " + doc.richiedente_nominativo + " Dati Fiscali: " + doc.richiedente_dati_fiscali + " Indirizzo: " + doc.richiedente_indirizzo + "</div>" +
//	        			"</div>"
//	        
//	    });
	});
	
//	var dynatable = $('#solrtable').dynatable({
//		table: {
//		    defaultColumnIdStyle: 'trimDash'
//		  },
//		  dataset: {
//		    records: jArray
//		  }
//		}).data('dynatable');
//	dynatable.settings.dataset.originalRecords = jArray;
//	dynatable.process();
	$( "#divAdc" ).show();
	$( "#divSolr" ).show();
}

function buildQuery() {
	var query = "";
	var ruolo = $("#ruolo").val();
	
	if ($('#nominativo').val() !== '') {
		query = query + ruolo + "_nominativo:" + $('#nominativo').val();
	} else {
		Materialize.toast("Campo Nominativo obbligatorio", 1000);
		return null;
	}
	
	if ($('#indirizzo').val() !== '') {
		query = query + " AND " + ruolo + "_indirizzo:" + $('#indirizzo').val();
	}
	if ($('#datifiscali').val() !== '') {
		query = query + " AND " + ruolo + "_dati_fiscali:" + $('#datifiscali').val();
	}
	query = query + " AND " + buildDateQuery($('#datada').val(), $('#dataa').val());
	if ($('#testolibero').val() !== '') {
		query = query + " AND " + $('#testolibero').val();
	}

	console.log(query);
	return query;
}

function buildDateQuery(da, a) {
	var datada = Date.parse(da, "yyyy-MM-dd");
	var dataa = Date.parse(a, "yyyy-MM-dd");
	
	if (da === '' && a !== '') {
		var a2 = new Date();
		a2.setTime(dataa);
		var da2 = addDays(dataa, "-", 5);
		return "data:[" + formatDate(da2) + " TO " + formatDate(a2) + "]";
	}
	if (da !== '' && a === '') {
		var da2 = new Date();
		da2.setTime(datada);
		var a2 = addDays(datada, "+", 5);
		return "data:[" + formatDate(da2) + " TO " + formatDate(a2) + "]";
	}
	if (da === '' && a === '') {
		var a2 = new Date();
		var da2 = addDays(a2, "-", 5);
		return "data:[" + formatDate(da2) + " TO " + formatDate(a2) + "]";
	}
	
	if (da !== '' && a !== '' ) {
		var a2 = new Date();
		var da2 = new Date();
		a2.setTime(dataa);
		da2.setTime(datada);
		if (datada < dataa) {
			if (dataa - datada > 1000*60*60*24*5) {
				Materialize.toast("Intervallo di date elevato, restringo a 5 giorni", 1000);
				var a2 = new Date();
				a2.setTime(dataa);
				var da2 = addDays(dataa, "-", 5);
				$('#datada').val(da2.getFullYear() + "-" + (da2.getMonth()+1) + "-" + da2.getDate());
				return "data:[" + formatDate(da2) + " TO " + formatDate(a2) + "]";
			} else {
				return "data:[" + formatDate(da2) + " TO " + formatDate(a2) + "]";
			}
		} else {
			Materialize.toast("Le date sono errate", 1000);
			Materialize.toast("Intervallo di date elevato, restringo a 5 giorni", 1000);
			var a2 = new Date();
			a2.setTime(dataa);
			var da2 = addDays(dataa, "-", 5);
			$('#datada').val(da2.getFullYear() + "-" + (da2.getMonth()+1) + "-" + da2.getDate());
			return "data:[" + formatDate(da2) + " TO " + formatDate(a2) + "]";
		}
	}
}

function addDays(data, oper, days) {
	var temp;
	if (oper === '+') {
		temp = data + 1000*60*60*24*days;
	} else {
		temp = data - 1000*60*60*24*days;
	}
	var result = new Date(); 
	result.setTime(temp);
	return result;
}

function formatDate(data) {
	var yyyy = data.getFullYear();
	var mm = data.getMonth()+1;
	var gg = data.getDate();
	return  yyyy + "-" + mm + "-" + gg + "T00\:00\:00.000Z";
}

function cercaChiaveDebole(url) {
	$.ajax({
			type: 'GET',
			url: url,
			jsonpCallback: solrCallback,
			contentType: "application/json",
			dataType: "jsonp",
			success: function(json) {
				showSolrResult(json);
			},
			error: function(e) {
				console.log(e.message);
			}
		});
}

