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

	
	/*
	 * Este m�todo � utilizado para carregar todas as lota��es existentes na tabela lotacoes do UNA para os campos autocomplete da tela admin.jsf
	 */
	public List<String> completeLotacoes(String query){
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
			Mensagens.setMessage(3, "N�o foi poss�vel obter a lista de lota��es: "+e.getMessage());
			return null;
		}
		
	}
	
	/*
	 * Este m�todo � respons�vel por salvar a vincula��o (amarra��o) entre uma categoria principal e outra secund�ria da tela admin.jsf
	 */
	public void salvar(){
		LotacaoDAO dao = new LotacaoDAO();
		if(this.lotacao.getNome().equals(this.secundaria.getNome())){
			Mensagens.setMessage(3, "A lota��o principal n�o pode ser igual a lota��o secund�ria");
		}else{
			try {
				if(dao.salvar(this.lotacao.getNome(), this.secundaria.getNome())){
					this.lotacao = new Lotacao();
					this.secundaria = new Lotacao();
					Mensagens.setMessage(1, "Lota��es amarradas com sucesso.");
					
				}else{
					Mensagens.setMessage(3, "N�o foi poss�vel amarrar de lota��es.");
				}
			} catch (SQLException e) {
				Mensagens.setMessage(3, "N�o foi poss�vel amarrar as lota��es: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Este m�todo � respons�vel por excluir uma determinada amarra��o entre lota��es atrav�s de seu id
	 */
	public void excluir(int id){
		LotacaoDAO dao = new LotacaoDAO();
		try {
			if(dao.excluir(id)){
				Mensagens.setMessage(1, "Amarra��o exclu�da com sucesso.");
				
			}else{
				Mensagens.setMessage(3, "N�o foi poss�vel excluir a amarra��o.");
			}
		} catch (SQLException e) {
			Mensagens.setMessage(3, "N�o foi poss�vel excluir a amarra��o: "+e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*
	 * Este  m�todo � respons�vel por listar todas as lota��es amarradas na tabela lotacao_lotacao do MySQL
	 * 
	 */
	public List<LotacaoLotacao> getLotacoesAmarradas(){
		LotacaoDAO dao = new LotacaoDAO();
		try {
			return dao.getLotacoesAmarradas();
		} catch (SQLException e) {
			Mensagens.setMessage(3, "N�o foi poss�vel obter a lista de lota��es amarradas: "+e.getMessage());
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
