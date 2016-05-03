package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;

import model.Andamento;
import model.Portal;
import model.Relatorio;
import util.ConexaoOracle;

public class RelatorioDAO {
	private String sql;
	private Relatorio relatorio = new Relatorio();
	
	public Relatorio getDados(String mes, String ano) throws SQLException, ClassNotFoundException, NamingException{
		sql = "select count(distinct(c.id)) as qtde from chamados c inner join andamentos an on (c.id = an.chamado and to_char(an.dt_andamento,'MM/YYYY') = ?) inner join chamado_palavra cp on c.id = cp.chamado inner join palavra_chave p on cp.palavra = p.id "+
				" right join assuntos a on c.assunto = a.id where an.classificacao = 'ABE' and a.lotacao in(SELECT l.id FROM sada.lotacao l WHERE (l.mae in(631,1301,1316)  OR l.id = 631) AND l.id not in(1298) "+
				" ) and a.ativo = 'S' and a.lotacao in(639) and    a.id not in (422,387,114,381,815,173,342,664,851,270,75,72,73,76,77,232,178,74,208,390,786,787,776,846,845,780,782,784,781,783,785,628,629,274,224,239,259,253,462,127,106,103,246)";
		Connection con = null;
		con = ConexaoOracle.abreConexao();
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1,mes+"/"+ano);
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			this.relatorio.setAno(ano);
			this.relatorio.setMes(mes);
			this.relatorio.setItem1(rs.getInt(1));
		}
		con.close();
		
		sql = "select count(distinct(c.id)) as qtde from chamados c inner join andamentos an on (c.id = an.chamado and to_char(an.dt_andamento,'MM/YYYY') = ?) "+
			" inner join chamado_palavra cp on c.id = cp.chamado inner join assuntos a on c.assunto = a.id right join palavra_chave p on cp.palavra = p.id "+
			" where an.classificacao = 'ABE' and p.lotacao in (SELECT l.id FROM   sada.lotacao l where  (l.mae in (631,1301,1316) or l.id in (631)) "+
            " and l.id not in (1298)) and upper(p.nome) not like ('[%]%') and upper(p.nome) not like (' [%]') and upper(p.nome) not like ('[%') "+
			"  and    upper(p.nome) not like ('*%') "+
			"  and    upper(p.nome) not like ('XXX') "+   
			"  and    upper(p.nome) not like ('W %') "+  
			"  and    upper(p.nome) not like ('ZIM%') "+                 
			"  and    upper(p.nome) not like ('VERSÃO 1%') "+                     
			"  and    upper(p.nome) not like ('VERSÃO 2%') "+
			" and    p.id not in (3452,3303,3271,1902,3459,3157,1915,643,2053,2055,2051,1774,1547,1864,3276,1685,273,118,584,1390,1851,2831,1668,747,1867, "+
                      " 731,3174,2133,1661,2678,1777,98,3460,3429,1562,746,1560,1869,2070,1667,634,726,1572,270,173,1702,1522,1592,80,2615,2599, "+
                      " 1721,146,2247,288,287,1978,1491,299,619,165,211,172,3443,225,549,1880,2025,1652,2124,78,6,1478,673, "+
                      " 1828,226,1949,1911,1710,2148,1646,671,143,1593,2134,2135,2132,2130,2131,1833,1854,1979,3632,1816,148,559,174,1396,1683, "+
                      " 2691,316,764,2338,1748,2122,2123,1804,2505,3437,2498,2510,3534,3468,3498,2161,2160,2158,2170,3541,2817,77,71,3461,657, "+
                      " 1508,499,2491,649,2658,3151,3619,2166,2139,697,1769,1567,2490,510,1760,308,1399,142,2628,1831,1871,228,2067,2391,2589, "+
                      " 2421,1573,3302,3563,3489,3497,229,99,1510,1561,2066,592,668,3628,488,265,263,264,328,117,1546,524,640,3618,2673,546, "+
                      " 515,1783,610,3248,1885,79,563,70,1409,260,337,1722,1727,313,3598,3595,2573,3612,1479,1815,237,2647,2645,3491,2681,516, "+
                      " 81,771,608,681,296,290,289,1822,678,1698,116,2679,1395,631,566,564,2100,2659,2653,3293,1704,238,3539, "+
                      " 583,582,581,475,2125,284,2203,2029,624,314,2337,680,1714,1772,1400,553,1790,301,490,2318,145,1523,1904,3503,1827,1907, "+
                      " 1896,1846,1834,1824,2107,1898,2001,1981,1982,1971,1861,1881,1850,1918,527,1612,507,3195,3197,2552,3486,2692,76,2507, "+
                      " 2605,2469,286,3466,2609,297,280,752,754,246,2145,2538,3494,3438,248,1693,1733,1735,1770,609,220,1986,2020,244,272,586, "+
                      " 320,2168,334,247,489,1535,692,734,755,659,176,2126,2121,2649,1830,1501,2342,331,2460,1818,2031,2837,3222,3179,3593, "+
                      " 3259,3217,74,73,72,620,622,621,2330,1591,523,1489,215,2035,2154,2044,2043,94,1723,1855,1870,2157,1542,268, "+
                      " 2201,545,3502,177,178,1659,2030,2008,149,257,2674,1847,283,641,2556,508,607,758,2834,303,670,2555,1734,304,2672,3552, "+
                      " 732,307,2184,1984,745,611,2648,2646,3476,3617,1820,1868,2540,2096,1725,2395,182,1398,1716,2548,147,606,282,281,2411, "+
                      " 1633,3558,554,2443,1967,740,605,514,618,3272,2063,2386,3562,635,636,750,1817,101,552,762)";
		con = null;
		con = ConexaoOracle.abreConexao();
		ps = con.prepareStatement(sql);
		ps.setString(1,mes+"/"+ano);
		rs = ps.executeQuery();
		while(rs.next()){
			this.relatorio.setItem2(rs.getInt(1));
		}
		con.close();
		
		sql = "select count(*) as qtde from(select distinct(c.id) from chamados c inner join andamentos an on (c.id = an.chamado and to_char(an.dt_andamento,'MM/YYYY') = ?) inner join chamado_palavra cp on c.id = cp.chamado inner join assuntos a on c.assunto = a.id right join palavra_chave p on cp.palavra = p.id where an.classificacao = 'ABE' and p.lotacao in (SELECT l.id FROM   sada.lotacao l where  (l.mae in (631,1301,1316) or l.id in (631)) "+
			" and l.id not in (1298)) "+
				"  and    upper(p.nome) not like ('[%]%')  "+
				"  and    upper(p.nome) not like (' [%]') "+
				"  and    upper(p.nome) not like ('[%') "+
				"  and    upper(p.nome) not like ('*%') "+ 
				"  and    upper(p.nome) not like ('XXX') "+  
				"  and    upper(p.nome) not like ('W %') "+ 
				"  and    upper(p.nome) not like ('ZIM%')   "+              
				"  and    upper(p.nome) not like ('VERSÃO 1%')  "+                   
				"  and    upper(p.nome) not like ('VERSÃO 2%') "+
				"  and    p.id not in (3452,3303,3271,1902,3459,3157,1915,643,2053,2055,2051,1774,1547,1864,3276,1685,273,118,584,1390,1851,2831,1668,747,1867, "+
				                   "   731,3174,2133,1661,2678,1777,98,3460,3429,1562,746,1560,1869,2070,1667,634,726,1572,270,173,1702,1522,1592,80,2615,2599, "+
				                   "   1721,146,2247,288,287,1978,1491,299,619,165,211,172,3443,225,549,1880,2025,1652,2124,78,6,1478,673, "+
				                   "   1828,226,1949,1911,1710,2148,1646,671,143,1593,2134,2135,2132,2130,2131,1833,1854,1979,3632,1816,148,559,174,1396,1683, "+
				                   "   2691,316,764,2338,1748,2122,2123,1804,2505,3437,2498,2510,3534,3468,3498,2161,2160,2158,2170,3541,2817,77,71,3461,657, "+
				                   "   1508,499,2491,649,2658,3151,3619,2166,2139,697,1769,1567,2490,510,1760,308,1399,142,2628,1831,1871,228,2067,2391,2589, "+
				                   "   2421,1573,3302,3563,3489,3497,229,99,1510,1561,2066,592,668,3628,488,265,263,264,328,117,1546,524,640,3618,2673,546, "+
				                   "   515,1783,610,3248,1885,79,563,70,1409,260,337,1722,1727,313,3598,3595,2573,3612,1479,1815,237,2647,2645,3491,2681,516, "+
				                   "   81,771,608,681,296,290,289,1822,678,1698,116,2679,1395,631,566,564,2100,2659,2653,3293,1704,238,3539, "+
				                   "   583,582,581,475,2125,284,2203,2029,624,314,2337,680,1714,1772,1400,553,1790,301,490,2318,145,1523,1904,3503,1827,1907, "+
				                   "   1896,1846,1834,1824,2107,1898,2001,1981,1982,1971,1861,1881,1850,1918,527,1612,507,3195,3197,2552,3486,2692,76,2507, "+
				                   "   2605,2469,286,3466,2609,297,280,752,754,246,2145,2538,3494,3438,248,1693,1733,1735,1770,609,220,1986,2020,244,272,586, "+
				                   "   320,2168,334,247,489,1535,692,734,755,659,176,2126,2121,2649,1830,1501,2342,331,2460,1818,2031,2837,3222,3179,3593, "+
				                   "  3259,3217,74,73,72,620,622,621,2330,1591,523,1489,215,2035,2154,2044,2043,94,1723,1855,1870,2157,1542,268, "+
				                   "   2201,545,3502,177,178,1659,2030,2008,149,257,2674,1847,283,641,2556,508,607,758,2834,303,670,2555,1734,304,2672,3552, "+
				                   "   732,307,2184,1984,745,611,2648,2646,3476,3617,1820,1868,2540,2096,1725,2395,182,1398,1716,2548,147,606,282,281,2411, "+
				                   "   1633,3558,554,2443,1967,740,605,514,618,3272,2063,2386,3562,635,636,750,1817,101,552,762) "+
				" intersect "+
				" select distinct(c.id) from chamados c inner join andamentos an on (c.id = an.chamado and to_char(an.dt_andamento,'MM/YYYY') = ?) "+
				" inner join chamado_palavra cp on c.id = cp.chamado inner join palavra_chave p on cp.palavra = p.id right join assuntos a on c.assunto = a.id "+
				" where an.classificacao = 'ABE' and a.lotacao in(SELECT l.id FROM sada.lotacao l WHERE (l.mae in(631,1301,1316)  OR l.id = 631) AND l.id not in(1298)) "+
				" and a.ativo = 'S' and a.lotacao in(639) and a.id not in(422,387,114,381,815,173,342,664,851,270,75,72,73,76,77,232,178,74,208,390,786,787,776,846,845,780,782,784,781,783,785,628,629,274,224,239,259,253,462,127,106,103,246)) ";
		con = null;
		con = ConexaoOracle.abreConexao();
		ps = con.prepareStatement(sql);
		ps.setString(1,mes+"/"+ano);
		ps.setString(2,mes+"/"+ano);
		rs = ps.executeQuery();
		while(rs.next()){
			this.relatorio.setItem3(rs.getInt(1));
		}
		con.close();
		
		sql = "select count(*) as qtde from (select distinct(c.id) from chamados c inner join andamentos an on (c.id = an.chamado and to_char(an.dt_andamento,'MM/YYYY') = ?) inner join chamado_palavra cp on c.id = cp.chamado inner join palavra_chave p on cp.palavra = p.id "+

			" right join assuntos a on c.assunto = a.id where an.classificacao = 'ABE' and 	a.lotacao in(SELECT l.id FROM sada.lotacao l WHERE (l.mae in(631,1301,1316)  OR l.id = 631) AND l.id not in(1298)) "+
			
			" and a.ativo = 'S'	and a.lotacao in(639) and    a.id not in (422,387,114,381,815,173,342,664,851,270,75,72,73,76,77,232,178,74,208,390,786,787, 776,846,845,780,782,784,781,783,785,628,629,274,224,239,259,253,462,127,106,103,246) "+
			" minus select distinct(c.id) from chamados c inner join andamentos an on (c.id = an.chamado and to_char(an.dt_andamento,'MM/YYYY') = ?) inner join chamado_palavra cp on c.id = cp.chamado "+
			" inner join assuntos a on c.assunto = a.id	right join palavra_chave p on cp.palavra = p.id "+
			" where	an.classificacao = 'ABE' and p.lotacao in (SELECT l.id  FROM   sada.lotacao l where  (l.mae in (631,1301,1316) or     l.id in (631)) and    l.id not in (1298)) "+
			"  and    upper(p.nome) not like ('[%]%')  "+
			"  and    upper(p.nome) not like (' [%]') "+
			"  and    upper(p.nome) not like ('[%') "+
			"  and    upper(p.nome) not like ('*%')  "+
			"  and    upper(p.nome) not like ('XXX')  "+  
			"  and    upper(p.nome) not like ('W %')   "+
			"  and    upper(p.nome) not like ('ZIM%')   "+              
			"  and    upper(p.nome) not like ('VERSÃO 1%')  "+                    
			"  and    upper(p.nome) not like ('VERSÃO 2%') "+
			"  and    p.id not in (3452,3303,3271,1902,3459,3157,1915,643,2053,2055,2051,1774,1547,1864,3276,1685,273,118,584,1390,1851,2831,1668,747,1867, "+
            "  731,3174,2133,1661,2678,1777,98,3460,3429,1562,746,1560,1869,2070,1667,634,726,1572,270,173,1702,1522,1592,80,2615,2599, "+
            "  1721,146,2247,288,287,1978,1491,299,619,165,211,172,3443,225,549,1880,2025,1652,2124,78,6,1478,673, "+
            "   1828,226,1949,1911,1710,2148,1646,671,143,1593,2134,2135,2132,2130,2131,1833,1854,1979,3632,1816,148,559,174,1396,1683, "+
            "   2691,316,764,2338,1748,2122,2123,1804,2505,3437,2498,2510,3534,3468,3498,2161,2160,2158,2170,3541,2817,77,71,3461,657, "+
            "   1508,499,2491,649,2658,3151,3619,2166,2139,697,1769,1567,2490,510,1760,308,1399,142,2628,1831,1871,228,2067,2391,2589, "+
            "   2421,1573,3302,3563,3489,3497,229,99,1510,1561,2066,592,668,3628,488,265,263,264,328,117,1546,524,640,3618,2673,546, "+
            "   515,1783,610,3248,1885,79,563,70,1409,260,337,1722,1727,313,3598,3595,2573,3612,1479,1815,237,2647,2645,3491,2681,516, "+
            "   81,771,608,681,296,290,289,1822,678,1698,116,2679,1395,631,566,564,2100,2659,2653,3293,1704,238,3539, "+
            "   583,582,581,475,2125,284,2203,2029,624,314,2337,680,1714,1772,1400,553,1790,301,490,2318,145,1523,1904,3503,1827,1907, "+
            "   1896,1846,1834,1824,2107,1898,2001,1981,1982,1971,1861,1881,1850,1918,527,1612,507,3195,3197,2552,3486,2692,76,2507, "+
            "   2605,2469,286,3466,2609,297,280,752,754,246,2145,2538,3494,3438,248,1693,1733,1735,1770,609,220,1986,2020,244,272,586, "+
            "   320,2168,334,247,489,1535,692,734,755,659,176,2126,2121,2649,1830,1501,2342,331,2460,1818,2031,2837,3222,3179,3593, "+
            "   3259,3217,74,73,72,620,622,621,2330,1591,523,1489,215,2035,2154,2044,2043,94,1723,1855,1870,2157,1542,268, "+
            "   2201,545,3502,177,178,1659,2030,2008,149,257,2674,1847,283,641,2556,508,607,758,2834,303,670,2555,1734,304,2672,3552, "+
            "   732,307,2184,1984,745,611,2648,2646,3476,3617,1820,1868,2540,2096,1725,2395,182,1398,1716,2548,147,606,282,281,2411, "+
            "   1633,3558,554,2443,1967,740,605,514,618,3272,2063,2386,3562,635,636,750,1817,101,552,762))";
		
		con = null;
		con = ConexaoOracle.abreConexao();
		ps = con.prepareStatement(sql);
		ps.setString(1,mes+"/"+ano);
		ps.setString(2,mes+"/"+ano);
		rs = ps.executeQuery();
		while(rs.next()){
			this.relatorio.setItem4(rs.getInt(1));
		}
		con.close();
		
		sql = "select count(*) as qtde from(select distinct(c.id) from chamados c inner join andamentos an on (c.id = an.chamado and to_char(an.dt_andamento,'MM/YYYY') = ?) inner join chamado_palavra cp on c.id = cp.chamado inner join assuntos a on c.assunto = a.id right join palavra_chave p on cp.palavra = p.id where an.classificacao = 'ABE' and p.lotacao in (SELECT l.id FROM   sada.lotacao l where  (l.mae in (631,1301,1316) or l.id in (631)) "+
				" and l.id not in (1298)) "+
					"  and    upper(p.nome) not like ('[%]%')  "+
					"  and    upper(p.nome) not like (' [%]') "+
					"  and    upper(p.nome) not like ('[%') "+
					"  and    upper(p.nome) not like ('*%') "+ 
					"  and    upper(p.nome) not like ('XXX') "+  
					"  and    upper(p.nome) not like ('W %') "+ 
					"  and    upper(p.nome) not like ('ZIM%')   "+              
					"  and    upper(p.nome) not like ('VERSÃO 1%')  "+                   
					"  and    upper(p.nome) not like ('VERSÃO 2%') "+
					"  and    p.id not in (3452,3303,3271,1902,3459,3157,1915,643,2053,2055,2051,1774,1547,1864,3276,1685,273,118,584,1390,1851,2831,1668,747,1867, "+
					                   "   731,3174,2133,1661,2678,1777,98,3460,3429,1562,746,1560,1869,2070,1667,634,726,1572,270,173,1702,1522,1592,80,2615,2599, "+
					                   "   1721,146,2247,288,287,1978,1491,299,619,165,211,172,3443,225,549,1880,2025,1652,2124,78,6,1478,673, "+
					                   "   1828,226,1949,1911,1710,2148,1646,671,143,1593,2134,2135,2132,2130,2131,1833,1854,1979,3632,1816,148,559,174,1396,1683, "+
					                   "   2691,316,764,2338,1748,2122,2123,1804,2505,3437,2498,2510,3534,3468,3498,2161,2160,2158,2170,3541,2817,77,71,3461,657, "+
					                   "   1508,499,2491,649,2658,3151,3619,2166,2139,697,1769,1567,2490,510,1760,308,1399,142,2628,1831,1871,228,2067,2391,2589, "+
					                   "   2421,1573,3302,3563,3489,3497,229,99,1510,1561,2066,592,668,3628,488,265,263,264,328,117,1546,524,640,3618,2673,546, "+
					                   "   515,1783,610,3248,1885,79,563,70,1409,260,337,1722,1727,313,3598,3595,2573,3612,1479,1815,237,2647,2645,3491,2681,516, "+
					                   "   81,771,608,681,296,290,289,1822,678,1698,116,2679,1395,631,566,564,2100,2659,2653,3293,1704,238,3539, "+
					                   "   583,582,581,475,2125,284,2203,2029,624,314,2337,680,1714,1772,1400,553,1790,301,490,2318,145,1523,1904,3503,1827,1907, "+
					                   "   1896,1846,1834,1824,2107,1898,2001,1981,1982,1971,1861,1881,1850,1918,527,1612,507,3195,3197,2552,3486,2692,76,2507, "+
					                   "   2605,2469,286,3466,2609,297,280,752,754,246,2145,2538,3494,3438,248,1693,1733,1735,1770,609,220,1986,2020,244,272,586, "+
					                   "   320,2168,334,247,489,1535,692,734,755,659,176,2126,2121,2649,1830,1501,2342,331,2460,1818,2031,2837,3222,3179,3593, "+
					                   "  3259,3217,74,73,72,620,622,621,2330,1591,523,1489,215,2035,2154,2044,2043,94,1723,1855,1870,2157,1542,268, "+
					                   "   2201,545,3502,177,178,1659,2030,2008,149,257,2674,1847,283,641,2556,508,607,758,2834,303,670,2555,1734,304,2672,3552, "+
					                   "   732,307,2184,1984,745,611,2648,2646,3476,3617,1820,1868,2540,2096,1725,2395,182,1398,1716,2548,147,606,282,281,2411, "+
					                   "   1633,3558,554,2443,1967,740,605,514,618,3272,2063,2386,3562,635,636,750,1817,101,552,762) "+
					" minus "+
					" select distinct(c.id) from chamados c inner join andamentos an on (c.id = an.chamado and to_char(an.dt_andamento,'MM/YYYY') = ?) "+
					" inner join chamado_palavra cp on c.id = cp.chamado inner join palavra_chave p on cp.palavra = p.id right join assuntos a on c.assunto = a.id "+
					" where an.classificacao = 'ABE' and a.lotacao in(SELECT l.id FROM sada.lotacao l WHERE (l.mae in(631,1301,1316)  OR l.id = 631) AND l.id not in(1298)) "+
					" and a.ativo = 'S' and a.lotacao in(639) and a.id not in(422,387,114,381,815,173,342,664,851,270,75,72,73,76,77,232,178,74,208,390,786,787,776,846,845,780,782,784,781,783,785,628,629,274,224,239,259,253,462,127,106,103,246)) ";
		con = null;
		con = ConexaoOracle.abreConexao();
		ps = con.prepareStatement(sql);
		ps.setString(1,mes+"/"+ano);
		ps.setString(2,mes+"/"+ano);
		rs = ps.executeQuery();
		while(rs.next()){
			this.relatorio.setItem5(rs.getInt(1));
		}
		con.close();
		
		sql = "select count(*) as qtde from (select distinct(c.id) from chamados c inner join andamentos an on (c.id = an.chamado and to_char(an.dt_andamento,'MM/YYYY') = ?) inner join chamado_palavra cp on c.id = cp.chamado inner join palavra_chave p on cp.palavra = p.id "+

			" right join assuntos a on c.assunto = a.id where an.classificacao = 'ABE' and 	a.lotacao in(SELECT l.id FROM sada.lotacao l WHERE (l.mae in(631,1301,1316)  OR l.id = 631) AND l.id not in(1298)) "+
			
			" and a.ativo = 'S'	and a.lotacao in(639) and    a.id not in (422,387,114,381,815,173,342,664,851,270,75,72,73,76,77,232,178,74,208,390,786,787, 776,846,845,780,782,784,781,783,785,628,629,274,224,239,259,253,462,127,106,103,246) "+
			" union select distinct(c.id) from chamados c inner join andamentos an on (c.id = an.chamado and to_char(an.dt_andamento,'MM/YYYY') = ?) inner join chamado_palavra cp on c.id = cp.chamado "+
			" inner join assuntos a on c.assunto = a.id	right join palavra_chave p on cp.palavra = p.id "+
			" where	an.classificacao = 'ABE' and p.lotacao in (SELECT l.id  FROM   sada.lotacao l where  (l.mae in (631,1301,1316) or     l.id in (631)) and    l.id not in (1298)) "+
			"  and    upper(p.nome) not like ('[%]%')  "+
			"  and    upper(p.nome) not like (' [%]') "+
			"  and    upper(p.nome) not like ('[%') "+
			"  and    upper(p.nome) not like ('*%')  "+
			"  and    upper(p.nome) not like ('XXX')  "+  
			"  and    upper(p.nome) not like ('W %')   "+
			"  and    upper(p.nome) not like ('ZIM%')   "+              
			"  and    upper(p.nome) not like ('VERSÃO 1%')  "+                    
			"  and    upper(p.nome) not like ('VERSÃO 2%') "+
			"  and    p.id not in (3452,3303,3271,1902,3459,3157,1915,643,2053,2055,2051,1774,1547,1864,3276,1685,273,118,584,1390,1851,2831,1668,747,1867, "+
            "  731,3174,2133,1661,2678,1777,98,3460,3429,1562,746,1560,1869,2070,1667,634,726,1572,270,173,1702,1522,1592,80,2615,2599, "+
            "  1721,146,2247,288,287,1978,1491,299,619,165,211,172,3443,225,549,1880,2025,1652,2124,78,6,1478,673, "+
            "   1828,226,1949,1911,1710,2148,1646,671,143,1593,2134,2135,2132,2130,2131,1833,1854,1979,3632,1816,148,559,174,1396,1683, "+
            "   2691,316,764,2338,1748,2122,2123,1804,2505,3437,2498,2510,3534,3468,3498,2161,2160,2158,2170,3541,2817,77,71,3461,657, "+
            "   1508,499,2491,649,2658,3151,3619,2166,2139,697,1769,1567,2490,510,1760,308,1399,142,2628,1831,1871,228,2067,2391,2589, "+
            "   2421,1573,3302,3563,3489,3497,229,99,1510,1561,2066,592,668,3628,488,265,263,264,328,117,1546,524,640,3618,2673,546, "+
            "   515,1783,610,3248,1885,79,563,70,1409,260,337,1722,1727,313,3598,3595,2573,3612,1479,1815,237,2647,2645,3491,2681,516, "+
            "   81,771,608,681,296,290,289,1822,678,1698,116,2679,1395,631,566,564,2100,2659,2653,3293,1704,238,3539, "+
            "   583,582,581,475,2125,284,2203,2029,624,314,2337,680,1714,1772,1400,553,1790,301,490,2318,145,1523,1904,3503,1827,1907, "+
            "   1896,1846,1834,1824,2107,1898,2001,1981,1982,1971,1861,1881,1850,1918,527,1612,507,3195,3197,2552,3486,2692,76,2507, "+
            "   2605,2469,286,3466,2609,297,280,752,754,246,2145,2538,3494,3438,248,1693,1733,1735,1770,609,220,1986,2020,244,272,586, "+
            "   320,2168,334,247,489,1535,692,734,755,659,176,2126,2121,2649,1830,1501,2342,331,2460,1818,2031,2837,3222,3179,3593, "+
            "   3259,3217,74,73,72,620,622,621,2330,1591,523,1489,215,2035,2154,2044,2043,94,1723,1855,1870,2157,1542,268, "+
            "   2201,545,3502,177,178,1659,2030,2008,149,257,2674,1847,283,641,2556,508,607,758,2834,303,670,2555,1734,304,2672,3552, "+
            "   732,307,2184,1984,745,611,2648,2646,3476,3617,1820,1868,2540,2096,1725,2395,182,1398,1716,2548,147,606,282,281,2411, "+
            "   1633,3558,554,2443,1967,740,605,514,618,3272,2063,2386,3562,635,636,750,1817,101,552,762))";
		
		con = null;
		con = ConexaoOracle.abreConexao();
		ps = con.prepareStatement(sql);
		ps.setString(1,mes+"/"+ano);
		ps.setString(2,mes+"/"+ano);
		rs = ps.executeQuery();
		while(rs.next()){
			this.relatorio.setItem6(rs.getInt(1));
		}
		con.close();
		System.out.println("Relatório gerado com sucesso!!!");
		return this.relatorio;
	}
}
