package controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import util.Mensagens;
import dao.LotacaoDAO;
import model.Lotacao;
import model.LotacaoLotacao;

@ManagedBean
@ViewScoped
public class AdminMB {
	private Lotacao lotacao = new Lotacao();
	private Lotacao secundaria = new Lotacao();

	
	/*
	 * Este método é utilizado para carregar todas as lotações existentes na tabela lotacoes do UNA para os campos autocomplete da tela admin.jsf
	 */
	public List<String> completeLotacoes(String query) throws NamingException{
		LotacaoDAO dao = new LotacaoDAO();
		List<String> lotacoes = new ArrayList<String>();
		try {
			for(Lotacao l : dao.getLotacoes()){
				if(l.getNome().contains(query.toUpperCase())){
					lotacoes.add(l.getNome());
				}
			}
			return lotacoes;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			Mensagens.setMessage(3, "Não foi possível obter a lista de lotações: "+e.getMessage());
			return null;
		}
		
	}
	
	/*
	 * Este método é responsável por salvar a vinculação (amarração) entre uma categoria principal e outra secundária da tela admin.jsf
	 */
	public void salvar() throws ClassNotFoundException, NamingException{
		LotacaoDAO dao = new LotacaoDAO();
		if(this.lotacao.getNome().equals(this.secundaria.getNome())){
			Mensagens.setMessage(3, "A lotação principal não pode ser igual a lotação secundária");
		}else{
			try {
				if(dao.salvar(this.lotacao.getNome(), this.secundaria.getNome())){
					this.lotacao = new Lotacao();
					this.secundaria = new Lotacao();
					Mensagens.setMessage(1, "Lotações amarradas com sucesso.");
					
				}else{
					Mensagens.setMessage(3, "Não foi possível amarrar as lotações.");
				}
			} catch (SQLException e) {
				Mensagens.setMessage(3, "Não foi possível amarrar as lotações: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Este método é responsável por excluir uma determinada amarração entre lotações através de seu id
	 */
	public void excluir(int id) throws ClassNotFoundException, NamingException{
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
	
	/*
	 * Este  método é responsável por listar todas as lotações amarradas na tabela lotacao_lotacao do MySQL
	 * 
	 */
	public List<LotacaoLotacao> getLotacoesAmarradas() throws ClassNotFoundException, NamingException{
		LotacaoDAO dao = new LotacaoDAO();
		try {
			return dao.getLotacoesAmarradas();
		} catch (SQLException e) {
			Mensagens.setMessage(3, "Não foi possível obter a lista de lotações amarradas: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	 

	/*
	 * getters and setters
	 */
	
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
