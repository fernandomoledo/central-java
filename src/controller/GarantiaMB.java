package controller;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import dao.TomboDAO;
import model.Tombo;
import util.Mensagens;

@ManagedBean
@ViewScoped
public class GarantiaMB {
	private List<Tombo> tombosSMP = new ArrayList<Tombo>(); //listarTombos();
	private List<Tombo> filteredTombos;
	private String tombo = "";
	
	final static Logger logger = Logger.getLogger(JiraMB.class);
	
	public void verificarGarantia(){
		TomboDAO dao = new TomboDAO();
		try{
			Tombo t = new Tombo();
			t = dao.getGarantia(this.tombo);
			if(t.getId() != 0){
				int intervalo = new Date().compareTo(t.getDtGarantia());
				
				if(intervalo == -1){
					Mensagens.setMessage(1, "Equipamento " + t.getNroTombo() + " / [NS: " + t.getSerie() + "] - " + t.getDescricao() + " tem garantia até " + new SimpleDateFormat("dd/MM/yyyy").format(t.getDtGarantia()));
				}else{
					Mensagens.setMessage(3, "Equipamento " + t.getNroTombo() + " / [NS: " + t.getSerie() + "] - " + t.getDescricao() + " tinha garantia até " + new SimpleDateFormat("dd/MM/yyyy").format(t.getDtGarantia()));
				}
			}else{
				Mensagens.setMessage(2, "Tombo "+ tombo + " não encontrado. Verifique o número digitado e tente novamente.");
			}
		}catch(Exception e){
			Mensagens.setMessage(3, "Erro ao consultar tombo. " + e.getMessage());
		}
	}
	
	public List<Tombo> listarTombos(){
		TomboDAO dao = new TomboDAO();
		try{
			return dao.listarTombos();
		}catch(Exception e){
			StringWriter stack = new StringWriter();
			e.printStackTrace(new PrintWriter(stack));
			logger.error("ERRO: " + stack.toString());
			return new ArrayList<Tombo>();
		}
	}
	
	public String getTombo() {
		return tombo;
	}

	public void setTombo(String tombo) {
		this.tombo = tombo;
	}

	public List<Tombo> getTombosSMP() {
		return tombosSMP;
	}

	public void setTombosSMP(List<Tombo> tombosSMP) {
		this.tombosSMP = tombosSMP;
	}
	
	
}
