package it.osg.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "IdName")
public class IdName {

	String identificativo;
	String nome;
	
	public IdName () {

	}
	
	public IdName (String id, String nm) {
		setIdentificativo(id);
		setNome(nm);
	}
	
	public String getIdentificativo() {
		return identificativo;
	}
	
	
	public void setIdentificativo(String identificativo) {
		this.identificativo = identificativo;
	}



	public String getNome() {
		return nome;
	}



	public void setNome(String nome) {
		this.nome = nome;
	}



	@Override
	public String toString() {
		return identificativo + " " + nome;
	}
	
}
