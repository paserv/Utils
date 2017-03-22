package it.osg.data;

import java.util.concurrent.atomic.AtomicInteger;

public class PSAR {

	private String id;
	private String nome;
	private String fan;
	private AtomicInteger postFromPage = new AtomicInteger(0);
	private AtomicInteger postFromFan = new AtomicInteger(0);
	private AtomicInteger comments = new AtomicInteger(0);
	private AtomicInteger likes = new AtomicInteger(0);
	private AtomicInteger shares = new AtomicInteger(0);
	private AtomicInteger commentsToPostFromFan = new AtomicInteger(0);
	private AtomicInteger commnetsFromPageToPostFromPage = new AtomicInteger(0);
	private AtomicInteger commnetsFromPageToPostFromFan = new AtomicInteger(0);


	public PSAR (String id, String nome) {
		setId(id);
		setNome(nome);
	}
	
	public void addPostFromPage (int value) {
		this.postFromPage.addAndGet(value);
	}

	public void addPostFromFan (int value) {
		this.postFromFan.addAndGet(value);
	}

	public void addComments (int value) {
		this.comments.addAndGet(value);
	}
	
	public void subComments (int value) {
		for (int i = 0; i < value; i++) {
			this.comments.decrementAndGet();
		}
	}

	public void addLikes (int value) {
		this.likes.addAndGet(value);
	}
	
	public void subLikes (int value) {
		for (int i = 0; i < value; i++) {
			this.likes.decrementAndGet();
		}
	}
	
	public void addShares (int value) {
		this.shares.addAndGet(value);
	}
	
	public void subShares (int value) {
		for (int i = 0; i < value; i++) {
			this.shares.decrementAndGet();
		}
	}
	
	public void addCommentsToPostFromFan (int value) {
		this.commentsToPostFromFan.addAndGet(value);
	}
	
	public void subCommentsToPostFromFan (int value) {
		for (int i = 0; i < value; i++) {
			this.commentsToPostFromFan.decrementAndGet();
		}
	}
	
	public void addCommnetsFromPageToPostFromPage (int value) {
		this.commnetsFromPageToPostFromPage.addAndGet(value);
	}
	
	public void subCommnetsFromPageToPostFromPage (int value) {
		for (int i = 0; i < value; i++) {
			this.commnetsFromPageToPostFromPage.decrementAndGet();
		}
	}
	
	public void addCommnetsFromPageToPostFromFan (int value) {
		this.commnetsFromPageToPostFromFan.addAndGet(value);
	}
	
	public void subCommnetsFromPageToPostFromFan (int value) {
		for (int i = 0; i < value; i++) {
			this.commnetsFromPageToPostFromFan.decrementAndGet();
		}
	}

	public String getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public AtomicInteger getPostFromPage() {
		return postFromPage;
	}

	public AtomicInteger getPostFromFan() {
		return postFromFan;
	}

	public AtomicInteger getComments() {
		return comments;
	}

	public AtomicInteger getLikes() {
		return likes;
	}

	public AtomicInteger getShares() {
		return shares;
	}

	public AtomicInteger getCommentsToPostFromFan() {
		return commentsToPostFromFan;
	}

	public AtomicInteger getCommnetsFromPageToPostFromPage() {
		return commnetsFromPageToPostFromPage;
	}

	public AtomicInteger getCommnetsFromPageToPostFromFan() {
		return commnetsFromPageToPostFromFan;
	}

	public String getFan() {
		return fan;
	}

	public void setFan(String fan) {
		this.fan = fan;
	}
	
	

}
