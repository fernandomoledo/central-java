package util;

public class Utilidades {
	public static String formataData(String data) {
		String novaData = "";
		String[] splitData;
		if(data.contains("/")) {
			splitData = data.split("/");
			novaData = splitData[2]+"-"+splitData[1]+"-"+splitData[0];
		}else {
			splitData = data.split("-");
			novaData = splitData[2]+"/"+splitData[1]+"/"+splitData[0];
		}
		return novaData;
	}
	
	public static String formataTickets(String tickets) {
		return tickets.replace(",", ", ");
	}
}
