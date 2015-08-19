package org.deguet.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class PoinToPointGenerator {

	public static String getServerStub(Class interf){
		StringBuilder sb = new StringBuilder();
		if (!interf.isInterface()) throw new UnsupportedOperationException();
		a(sb, "@Path(\"/"+interf.getSimpleName()+"\")");
		a(sb,"public class Server"+ interf.getSimpleName() + "{" );
		// champ vers le service de base
		a(sb,"\t"+interf.getSimpleName()+" service;");
		// constructeur
		a(sb,"\tpublic Server"+interf.getSimpleName()+"("+ interf.getSimpleName() +"  s){");
		a(sb,"\tservice = s;");
		a(sb,"\t}");
		// facade for methods
		for (Method m : interf.getMethods()){
			a(sb, "\t@POST");
			a(sb, "\t@Path(\""+m.getName()+"\")");
			a(sb, "\tpublic " + m.getReturnType().getSimpleName() + " "+m.getName()+"(");
			for (Parameter p : m.getParameters()){
				a(sb, "\t\t  @FormParam(\""+p.getName()+"\")"+p.getType().getSimpleName()+"  " +p.getName());
			}
			a(sb, "\t){");
			a(sb, "\t}");
			a(sb, "");
		}
		// serialisation deal for classes OUCH
		
		a(sb, "}" );
		return sb.toString();
	}
	
	public static void a(StringBuilder sb,String s){
		sb.append("\n"+s);
	}
	
	public static String getClientStub(Class interf){
		StringBuilder sb = new StringBuilder();
		if (!interf.isInterface()) throw new UnsupportedOperationException();
		return sb.toString();
	}
	
}
