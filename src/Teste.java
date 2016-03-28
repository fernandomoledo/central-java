import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Andamento;
import model.Tombo;
import util.Mensagens;
import dao.AndamentoDAO;
import dao.ChamadoDAO;
import dao.TomboDAO;


public class Teste {

	
	public static void main(String[] args) {
		long id = 207104L;
		Andamento chamadoDetalhe = new Andamento();
		Andamento ultimoAndamento = new Andamento();
		List<Tombo> tombosDetalhe = new ArrayList<Tombo>();
		List<Andamento> andamentosDetalhe = new ArrayList<Andamento>();
		
		ChamadoDAO cDao = new ChamadoDAO();
		TomboDAO tDao = new TomboDAO();
		AndamentoDAO aDao = new AndamentoDAO();
		try {
			chamadoDetalhe = cDao.getInfoChamado(id);
			tombosDetalhe = tDao.getTombosPorChamado(id);
			andamentosDetalhe = aDao.getAndamentosPorChamado(id);
			ultimoAndamento = andamentosDetalhe.get(andamentosDetalhe.size()-1);
		} catch (ClassNotFoundException | SQLException e) {
			Mensagens.setMessage(3, "Não foi possível obter o detalhe do chamado. "+e.getMessage());
			e.printStackTrace();
		} 

	}

}
