package util;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

public class ConexaoLDAP {
	public static String autentica(String username, String password){
		Hashtable env = new Hashtable();

        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://poolldap.trt15.jus.br:389");
        env.put(Context.SECURITY_AUTHENTICATION, "none");
        
       
        NamingEnumeration<SearchResult> results = null;
        LdapContext ctx = null;
        try {
        	 ctx = new InitialLdapContext(env,null);
        	 ctx.setRequestControls(null);
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            results = ctx.search("ou=setic,ou=adm,o=trt15", "(uid="+username+")", controls);
            String fullDN = null;
            DirContext dc = null;
            dc = new InitialDirContext(env);
            if (results.hasMore()) {
            	SearchResult searchResult = (SearchResult) results.next();
            	Attributes attributes = searchResult.getAttributes();
                Attribute attr = attributes.get("cn");
                String cn = (String) attr.get();
                fullDN = searchResult.getNameInNamespace();

                dc.close();
                dc = null;

                env.put(Context.SECURITY_AUTHENTICATION, "simple");
                env.put(Context.SECURITY_PRINCIPAL, fullDN);
                env.put(Context.SECURITY_CREDENTIALS, password);

                dc = new InitialDirContext(env);
                System.out.println("Autenticado!: " + fullDN + " / " + cn);
                return cn;
                
            }
        } catch (NamingException e) {
        	e.getMessage();
            System.out.println("Credenciais inválidas!");
            return "";
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (Exception e) {
                }
            }
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception e) {
                }
            }
        }
		return "";
	}
}
