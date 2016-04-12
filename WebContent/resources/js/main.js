var baseUrl = "http://10.15.216.153/central/";

$("#txt_busca").keypress(function(e){
	if(e.which == 13){
		e.preventDefault();
		if($("#txt_busca").val()!=""){
			$("#btn-busca").click();
		}else{
			alert("Informe um termo para a busca!");
		}
	}
	
});

$(function() {
      $( "#categoria_pai" ).autocomplete({
      source: "categoria/categorias_complete"
    });

       $( "#categoria_filha" ).autocomplete({
      source: "categoria/categorias_complete"
    });
  });

function mostra_box(){
	$(".ibox").show();
}

function esconde_box(){
	$(".ibox").hide();
}

function montaurl(url, filtro){
	//alert(url + " - " +  filtro);
	if(url == ""){
		alert("Informe um termo para a busca!");
	}else{
		window.location="basedeconhecimento.jsf?termo="+url+"&filtro="+filtro;
	}
}

$("#busca").keypress(function(e){
	if(e.which == 13){
		 	window.location.href = baseUrl+"categoria/listar/" + $("#busca").val();		
	}	
});

$(".lista").click(function(){
	$(".lista").removeClass('active');
	$(this).addClass('active');
});

function ver_detalhe(id){
	$("#tbl").html("<img src='"+baseUrl+"resources/img/loading_3.gif' />");

	
	$.ajax({
		url: baseUrl+"basedeconhecimento/detalhe/"+id,
		success: function(data){
			$("#tbl").html(data);
		}
	});

	
}

function aguarde(){
	$("#tbl").html("<img src='"+baseUrl+"resources/img/loading_3.gif' />");
}





   
   
