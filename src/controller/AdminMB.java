package controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import util.Mensagens;
import dao.LotacaoDAO;
import model.Lotacao;
import model.LotacaoLotacao;

@ManagedBean
@ViewScoped
public class AdminMB {
	private Lotacao lotacao = new Lotacao();
	private Lotacao secundaria = new Lotacao();

	
	
	public List<Lotacao> getLotacoes(){
		LotacaoDAO dao = new LotacaoDAO();
		try {
			return dao.getLotacoes();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			Mensagens.setMessage(3, "Não foi possível obter a lista de lotações: "+e.getMessage());
			return null;
		}
		
	}
	
	public void salvar(){
		LotacaoDAO dao = new LotacaoDAO();
		try {
			if(dao.salvar(this.lotacao.getNome(), this.secundaria.getNome())){
				this.lotacao = new Lotacao();
				this.secundaria = new Lotacao();
				Mensagens.setMessage(1, "Lotações amarradas com sucesso.");
				
			}else{
				Mensagens.setMessage(3, "Não foi possível amarrar de lotações.");
			}
		} catch (SQLException e) {
			Mensagens.setMessage(3, "Não foi possível amarrar as lotações: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void excluir(int id){
		LotacaoDAO dao = new LotacaoDAO();
		try {
			if(dao.excluir(id)){
				Mensagens.setMessage(1, "Amarração excluída com sucesso.");
				
			}else{
				Mensagens.setMessage(3, "Não foi possível excluir a amarração.");
			}
		} catch (SQLException e) {
			Mensagens.setMessage(3, "Não foi possível excluir a amarração: "+e.getMessage());
			e.printStackTrace();
		}
	}
	public List<LotacaoLotacao> getLotacoesAmarradas(){
		LotacaoDAO dao = new LotacaoDAO();
		try {
			return dao.getLotacoesAmarradas();
		} catch (SQLException e) {
			Mensagens.setMessage(3, "Não foi possível obter a lista de lotações amarradas: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	 


	public Lotacao getLotacao() {
		return lotacao;
	}


	public void setLotacao(Lotacao lotacao) {
		this.lotacao = lotacao;
	}




	public Lotacao getSecundaria() {
		return secundaria;
	}




	public void setSecundaria(Lotacao secundaria) {
		this.secundaria = secundaria;
	}

	
}
