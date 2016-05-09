package controller;



import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import dao.RelatorioDAO;
import model.Relatorio;
import util.Mensagens;

@ManagedBean
@ViewScoped
public class RelatorioMB {
	private String mes;
	private String ano;
	private Relatorio dados = new Relatorio();
	final static Logger logger = Logger.getLogger(RelatorioMB.class);
	
	public void gerarRelatorio(){
		RelatorioDAO dao = new RelatorioDAO();
		try{
			this.setDados(dao.getDados(this.mes, this.ano));
			Mensagens.setMessage(1, "Relatório para " +this.mes+ "/" +this.ano+ " gerado com sucesso.");
		}catch(Exception e){
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			Mensagens.setMessage(3, "Erro ao gerar relatório: "+e.getMessage());
			
		}
	}

	

	public String getMes() {
		return mes;
	}



	public void setMes(String mes) {
		this.mes = mes;
	}



	public String getAno() {
		return ano;
	}



	public void setAno(String ano) {
		this.ano = ano;
	}



	public Relatorio getDados() {
		return dados;
	}

	public void setDados(Relatorio dados) {
		this.dados = dados;
	}

	
	
	
}
