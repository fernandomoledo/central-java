var baseUrl = "http://10.15.216.153/central/";

$("#exportar").click(function(e){
	window.open('data:application/vnd.ms-excel,'+ $("#saida-dados").html());
	e.preventDefault();
});

$("#teste-btn").click(function(){
	if($("#teste-btn").val() == "e"){
		$("#div-sidebar").hide("slow");
		$("#tbl").removeClass("col-md-7");
		$("#tbl").addClass("col-md-12");
		$("#teste-btn").html("&gt;");
		$("#teste-btn").val("m");
		$("#teste-btn").attr("title","Mostrar  a lista de chamados");
	}else{
		$("#div-sidebar").show("slow");
		$("#tbl").removeClass("col-md-12");
		$("#tbl").addClass("col-md-7");
		$("#teste-btn").html("&lt;");
		$("#teste-btn").val("e");
		$("#teste-btn").attr("title","Esconder  a lista de chamados");
	}
});

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

function mostra_box2(){
	$(".ibox2").show();
}

function esconde_box2(){
	$(".ibox2").hide();
}

function mostra_box3(){
	$(".ibox3").show();
}

function esconde_box3(){
	$(".ibox3").hide();
}

function mostra_box4(){
	$(".ibox4").show();
}

function esconde_box4(){
	$(".ibox4").hide();
}

function montaurl(url, filtro){
	//alert(url + " - " +  filtro);
	if(url == ""){
		alert("Informe um termo para a busca!");
	}else{
		var busca = encodeURI(url.replace(/\\/g, "\\\\"));
		window.location="arvore.jsf?termo="+busca+"&filtro="+filtro;
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





   
   
