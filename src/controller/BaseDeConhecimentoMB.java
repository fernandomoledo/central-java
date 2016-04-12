package controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import util.Mensagens;
import dao.AndamentoDAO;
import dao.CategoriaDAO;
import dao.ChamadoDAO;
import dao.TomboDAO;
import model.Andamento;
import model.Categoria;
import model.CategoriaPai;
import model.Chamado;
import model.Tombo;


@ManagedBean
@ViewScoped
public class BaseDeConhecimentoMB {
	private TreeNode root; //armazena os n�s do menu �rvore
	private TreeNode select; //armazena um n� do menu �rvore selecionado
	private boolean mostra = false; //flag para mostrar ou n�o a tabela listando os chamados encontrados
	private boolean mostraDetalhe = false; //flag para mostrar ou n�o o detalhe de um chamado selecionado
	private List<Chamado> chamados = new ArrayList<Chamado>();
	private List<Chamado> chamadosFiltrados;
	private Andamento chamadoDetalhe = new Andamento();
	private List<Tombo> tombosDetalhe = new ArrayList<Tombo>();
	private List<Andamento> andamentosDetalhe = new ArrayList<Andamento>();
	private Andamento ultimoAndamento = new Andamento();
	private Chamado selecionado; //armazena o chamado selecionado
	private String termoBusca = ""; //armazena o termo de busca digitado na busca geral
	private String termoDestaque = ""; //armazena o termo de busca com a marca��o <mark></mark>
	private String termoTroca = ""; //armazena o termo que substituir� a busca, caso haja termo adicional cadastrado no banco
	private String termo="";
	private String[] termos;
	
	/*
	 * O m�todo construtor inicializa a �rvore e verifica se h� termo de busca para disparar a pesquisa
	 */
	public BaseDeConhecimentoMB() throws NamingException{
		FacesContext fc = FacesContext.getCurrentInstance();
		Map<String,String> params = fc.getExternalContext().getRequestParameterMap();
		if(!params.isEmpty()){
			if(params.get("filtro").equals("OU")){
				this.termoBusca = params.get("termo").replace(" ", " | ");
			}else if(params.get("filtro").equals("E")){
				this.termoBusca = params.get("termo").replace(" ", " & ");
			}else{
				this.termoBusca = params.get("termo").replace("||", " & ");
			}
			this.termo = this.termoBusca;
			this.termoDestaque = this.termoBusca.toUpperCase();
			this.termoTroca = "<mark>"+this.termoDestaque+"</mark>";
			buscar();
		}
		System.out.println(params.toString());
		 
		 root = new DefaultTreeNode("Root", null);
		 montaNode(0, root);
		 
	}
	
	/*
	 * Este m�todo recursivo constr�i o menu �rvore
	 */
	public TreeNode montaNode(int pai, TreeNode noPai) throws NamingException{
		List<CategoriaPai> cp = new ArrayList<CategoriaPai>();
		TreeNode node = null;
		 CategoriaDAO dao = new CategoriaDAO();
		 try {
			cp = dao.listarCategorias(pai);
		 }catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		 }
		 
		 for(int i = 0; i < cp.size(); i++){
			 node = new DefaultTreeNode(cp.get(i).getFilha().getNomeCategoria(), noPai);
			 montaNode(cp.get(i).getFilha().getIdCategoria(), node);
			 
		 }
		 return node;
	}

	/*
	 * Este m�todo captura o n� da �rvore selecionado e verifica se ele � pai ou n�o. Caso n�o seja, dispara a busca de chamados
	 */
	public void getSelecao() throws ClassNotFoundException, SQLException, NamingException{
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Filhos do n�: "+this.select.getChildCount(),null));
		chamados = new ArrayList<Chamado>();
		chamadosFiltrados = null;
		selecionado = null;
		this.mostraDetalhe = false;
		this.termoBusca = "";
		if(this.chamados.size() == 0) this.mostra = false;
		if(this.select.getChildCount() == 0){
			getListaChamados();
		}
	}
	
	/*
	 * Este m�todo retorna uma lista de todos os chamados encontrados com base na categoria selecionada na �rvore
	 */
	public void getListaChamados() throws ClassNotFoundException, SQLException, NamingException{
		String equipamento;
		ChamadoDAO dao = new ChamadoDAO();
		CategoriaDAO cDao = new CategoriaDAO();
		Categoria c = new Categoria();
		System.out.println(this.select.getParent() + " - " + this.select.getParent().getParent() + " - " + this.select.getParent().getParent().getParent());
		if(this.select.getParent().toString().equals("Software") || this.select.getParent().getParent().toString().equals("Software") ||
				this.select.getParent().getParent().getParent().toString().equals("Software")){
			termo = this.select.toString();
			this.termoDestaque = termo;
			c = cDao.getCategoriaByName(termo);
			termo = c.getTermoCategoria().equals("") ? c.getNomeCategoria() : c.getTermoCategoria();
			this.chamados = dao.getChamadosSemTombo(termo);
			this.termoTroca = "<mark>"+this.termoDestaque+"</mark>";
			System.out.println(termo + " - " + termoTroca + this.chamados.size());
		}else{
			termo = this.select.getParent().toString();
			equipamento = this.select.toString();
			this.termoDestaque = termo;
			c = cDao.getCategoriaByName(termo);
			termo = c.getTermoCategoria().equals("") ? c.getNomeCategoria() : c.getTermoCategoria();
			c = cDao.getCategoriaByName(equipamento);
			equipamento = c.getTermoCategoria().equals("") ? c.getNomeCategoria() : c.getTermoCategoria();
			this.chamados = dao.getChamadosComTombo(termo, equipamento);
			this.termoTroca = "<mark>"+this.termoDestaque+"</mark>";
		}
		
		if(this.chamados.size() > 0) this.mostra = true;
	}
	
	/*
	 * Este m�todo carrega todas as informa��es do chamado selecionado para exib�-lo na tela
	 */
	public void getDetalhe() throws NamingException{
		long id = this.selecionado.getId();
		ChamadoDAO cDao = new ChamadoDAO();
		TomboDAO tDao = new TomboDAO();
		AndamentoDAO aDao = new AndamentoDAO();
		try {
			this.chamadoDetalhe = cDao.getInfoChamado(id);
			termos = termo.split("(\\|)|(\\&)");
			for(int i = 0; i < termos.length; i++){
				System.out.println("Termo "+(i+1)+": "+termos[i].trim());
				chamadoDetalhe.setTexto(chamadoDetalhe.getTexto().toUpperCase().replace(termos[i].toUpperCase().trim(), "<mark>"+termos[i].toUpperCase().trim()+"</mark>"));
				chamadoDetalhe.getChamado().getLotacaoSolicitante().setNome(chamadoDetalhe.getChamado().getLotacaoSolicitante().getNome().toUpperCase().replace(termos[i].toUpperCase().trim(), "<mark>"+termos[i].toUpperCase().trim()+"</mark>"));
			}
			chamadoDetalhe.setTexto(chamadoDetalhe.getTexto().toUpperCase().replace(this.termoDestaque.toUpperCase().trim(), "<mark>"+this.termoDestaque.toUpperCase().trim()+"</mark>"));
			chamadoDetalhe.getChamado().getLotacaoSolicitante().setNome(chamadoDetalhe.getChamado().getLotacaoSolicitante().getNome().toUpperCase().replace(this.termoDestaque.toUpperCase().trim(), "<mark>"+this.termoDestaque.toUpperCase().trim()+"</mark>"));
			
			this.tombosDetalhe = tDao.getTombosPorChamado(id);
			for(int i = 0; i < tombosDetalhe.size(); i++){
				for(int j = 0; j<termos.length; j++){
					tombosDetalhe.get(i).setDescricao(tombosDetalhe.get(i).getDescricao().toUpperCase().replace(termos[j].toUpperCase().trim(), "<mark>"+termos[j].toUpperCase().trim()+"</mark>"));
				
				}
				tombosDetalhe.get(i).setDescricao(tombosDetalhe.get(i).getDescricao().toUpperCase().replace(termoDestaque.toUpperCase().trim(), "<mark>"+termoDestaque.toUpperCase().trim()+"</mark>"));
			}
			
			this.andamentosDetalhe = aDao.getAndamentosPorChamado(id);
			for(int i = 0; i < andamentosDetalhe.size(); i++){
				for(int j = 0; j < termos.length; j++){
					andamentosDetalhe.get(i).setTexto(andamentosDetalhe.get(i).getTexto().toUpperCase().replace(termos[j].toUpperCase().trim(), "<mark>"+termos[j].toUpperCase().trim()+"</mark>"));
					andamentosDetalhe.get(i).getUsuario().setNome(andamentosDetalhe.get(i).getUsuario().getNome().toUpperCase().replace(termos[j].toUpperCase().trim(), "<mark>"+termos[j].toUpperCase().trim()+"</mark>"));
				
				}
				andamentosDetalhe.get(i).setTexto(andamentosDetalhe.get(i).getTexto().toUpperCase().replace(termoDestaque.toUpperCase().trim(), "<mark>"+termoDestaque.toUpperCase().trim()+"</mark>"));
				andamentosDetalhe.get(i).getUsuario().setNome(andamentosDetalhe.get(i).getUsuario().getNome().toUpperCase().replace(termoDestaque.toUpperCase().trim(), "<mark>"+termoDestaque.toUpperCase().trim()+"</mark>"));
			}
			this.ultimoAndamento = this.andamentosDetalhe.get(this.andamentosDetalhe.size()-1);
			this.mostraDetalhe = true;
		} catch (ClassNotFoundException | SQLException e) {
			Mensagens.setMessage(3, "N�o foi poss�vel obter o detalhe do chamado. "+e.getMessage());
			e.printStackTrace();
		} 
	}
	
	/*
	 * Este m�todo � respons�vel por buscar de forma completa no banco UNA o termo digitado na busca geral do site
	 */
	
	public void buscar() throws NamingException{
		ChamadoDAO dao = new ChamadoDAO();
		try {
			this.chamados = dao.getChamadosBuscaGeral(this.termoBusca);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		if(this.chamados.size() > 0) this.mostra = true;
		for(Chamado c : this.chamados){
			System.out.println("Chamado "+c.getNumero());
		}
	}
	
	public TreeNode getRoot() {
		return root;
	}

	public TreeNode getSelect() {
		return select;
	}

	public void setSelect(TreeNode select) {
		this.select = select;
	}

	public List<Chamado> getChamados() {
		return chamados;
	}

	public void setChamados(List<Chamado> chamados) {
		this.chamados = chamados;
	}

	public boolean isMostra() {
		return mostra;
	}

	public void setMostra(boolean mostra) {
		this.mostra = mostra;
	}

	public List<Chamado> getChamadosFiltrados() {
		return chamadosFiltrados;
	}

	public void setChamadosFiltrados(List<Chamado> chamadosFiltrados) {
		this.chamadosFiltrados = chamadosFiltrados;
	}

	public Andamento getChamadoDetalhe() {
		return chamadoDetalhe;
	}

	public void setChamadoDetalhe(Andamento chamadoDetalhe) {
		this.chamadoDetalhe = chamadoDetalhe;
	}

	public List<Tombo> getTombosDetalhe() {
		return tombosDetalhe;
	}

	public void setTombosDetalhe(List<Tombo> tombosDetalhe) {
		this.tombosDetalhe = tombosDetalhe;
	}

	public List<Andamento> getAndamentosDetalhe() {
		return andamentosDetalhe;
	}

	public void setAndamentosDetalhe(List<Andamento> andamentosDetalhe) {
		this.andamentosDetalhe = andamentosDetalhe;
	}

	public Andamento getUltimoAndamento() {
		return ultimoAndamento;
	}

	public void setUltimoAndamento(Andamento ultimoAndamento) {
		this.ultimoAndamento = ultimoAndamento;
	}

	public Chamado getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Chamado selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isMostraDetalhe() {
		return mostraDetalhe;
	}

	public void setMostraDetalhe(boolean mostraDetalhe) {
		this.mostraDetalhe = mostraDetalhe;
	}

	public String getTermoBusca() {
		return termoBusca;
	}

	public void setTermoBusca(String termoBusca) {
		this.termoBusca = termoBusca;
	}

	public String getTermoDestaque() {
		return termoDestaque;
	}

	public void setTermoDestaque(String termoDestaque) {
		this.termoDestaque = termoDestaque;
	}

	public String getTermoTroca() {
		return termoTroca;
	}

	public void setTermoTroca(String termoTroca) {
		this.termoTroca = termoTroca;
	}

	public String[] getTermos() {
		return termos;
	}

	public void setTermos(String[] termos) {
		this.termos = termos;
	}

	
	
	/*
	public void getDetalhe(long id){
		this.setCategorias(new ArrayList<Categoria>());
		this.setShowDetail(1);
		CategoriaDAO daoCat= new CategoriaDAO();
		try {
			if(!daoCat.ehPai(this.categoriasBreadcrumb.get(this.categoriasBreadcrumb.size()-1).getIdCategoria())){
				this.ehPai = false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ChamadoDAO cDao = new ChamadoDAO();
		TomboDAO tDao = new TomboDAO();
		AndamentoDAO aDao = new AndamentoDAO();
		try {
			this.chamadoDetalhe = cDao.getInfoChamado(id);
			this.tombosDetalhe = tDao.getTombosPorChamado(id);
			this.andamentosDetalhe = aDao.getAndamentosPorChamado(id);
			this.ultimoAndamento = this.andamentosDetalhe.get(this.andamentosDetalhe.size()-1);
		} catch (ClassNotFoundException | SQLException e) {
			Mensagens.setMessage(3, "N�o foi poss�vel obter o detalhe do chamado. "+e.getMessage());
			e.printStackTrace();
		} 
	}
	
	*/
	
	
}
