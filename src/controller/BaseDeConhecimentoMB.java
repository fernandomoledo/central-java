package controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

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
	private TreeNode root;
	private TreeNode select;
	private boolean mostra = false;
	private boolean mostraDetalhe = false;
	private String chamado = "";
	private List<Chamado> chamados = new ArrayList<Chamado>();
	private List<Chamado> chamadosFiltrados;
	private Andamento chamadoDetalhe = new Andamento();
	private List<Tombo> tombosDetalhe = new ArrayList<Tombo>();
	private List<Andamento> andamentosDetalhe = new ArrayList<Andamento>();
	private Andamento ultimoAndamento = new Andamento();
	private Chamado selecionado;
	private String termoBusca = "";
	private String termoDestaque = "";
	private String termoTroca = "";
	
	
	public BaseDeConhecimentoMB(){
		FacesContext fc = FacesContext.getCurrentInstance();
		Map<String,String> params = fc.getExternalContext().getRequestParameterMap();
		if(!params.isEmpty()){
			this.termoBusca = params.get("termo");
			this.termoDestaque = this.termoBusca.toUpperCase();
			this.termoTroca = "<mark>"+this.termoDestaque+"</mark>";
			buscar();
		}
		System.out.println(params.toString());
		 
		 root = new DefaultTreeNode("Root", null);
		 montaNode(0, root);
		 
	}
	
	public TreeNode montaNode(int pai, TreeNode noPai){
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

	public void getSelecao() throws ClassNotFoundException, SQLException{
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Filhos do nó: "+this.select.getChildCount(),null));
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
	
	public void getListaChamados() throws ClassNotFoundException, SQLException{
		String termo, equipamento;
		ChamadoDAO dao = new ChamadoDAO();
		CategoriaDAO cDao = new CategoriaDAO();
		Categoria c = new Categoria();
		if(this.select.getParent().toString().equals("Software") || this.select.getParent().getParent().equals("Software") ||
				this.select.getParent().getParent().getParent().equals("Software")){
			termo = this.select.toString();
			this.termoDestaque = termo;
			c = cDao.getCategoriaByName(termo);
			termo = c.getTermoCategoria().equals("") ? c.getNomeCategoria() : c.getTermoCategoria();
			this.chamados = dao.getChamadosSemTombo(termo);
			this.termoTroca = "<mark>"+this.termoDestaque+"</mark>";
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
	
	public void getDetalhe(){
		long id = this.selecionado.getId();
		ChamadoDAO cDao = new ChamadoDAO();
		TomboDAO tDao = new TomboDAO();
		AndamentoDAO aDao = new AndamentoDAO();
		try {
			this.chamadoDetalhe = cDao.getInfoChamado(id);
			this.tombosDetalhe = tDao.getTombosPorChamado(id);
			this.andamentosDetalhe = aDao.getAndamentosPorChamado(id);
			this.ultimoAndamento = this.andamentosDetalhe.get(this.andamentosDetalhe.size()-1);
			this.mostraDetalhe = true;
		} catch (ClassNotFoundException | SQLException e) {
			Mensagens.setMessage(3, "Não foi possível obter o detalhe do chamado. "+e.getMessage());
			e.printStackTrace();
		} 
	}
	
	public void buscar(){
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

	public String getChamado() {
		return chamado;
	}

	public void setChamado(String chamado) {
		this.chamado = chamado;
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
			Mensagens.setMessage(3, "Não foi possível obter o detalhe do chamado. "+e.getMessage());
			e.printStackTrace();
		} 
	}
	
	*/
	
	
}
