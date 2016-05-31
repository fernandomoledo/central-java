package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("*.jsf")
public class ControleDeAcesso implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unused")
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain fc) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		HttpSession session = req.getSession();
		
		if(session.getAttribute("usuarioLogado") == null && 
				!req.getRequestURI().endsWith("/index.jsf") &&
				!req.getRequestURI().endsWith("/primeiroacesso.jsf") &&
				!req.getRequestURI().contains("/javax.faces.resource/")){
					String url = req.getRequestURL().toString();
					url = url.substring(url.indexOf("basedeconhecimento/")+19);
					res.sendRedirect(req.getContextPath()+"/index.jsf?returnUrl="+url);
		}else if((session.getAttribute("usuarioLogado") != null &&
				req.getRequestURI().endsWith("/index.jsf")) ||
				(session.getAttribute("usuarioLogado") != null &&
						req.getRequestURI().endsWith("/"))){
					res.sendRedirect(req.getContextPath()+"/painel.jsf");
		}else{
			fc.doFilter(request, response);
		}
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

}
