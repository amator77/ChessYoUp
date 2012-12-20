package com.chessyoup.connector;

public class GenericRoom implements Room {
	
	private String name;
	
	private String id;
	
	public GenericRoom(){
		
	}
	
	public GenericRoom(String name,String id){
		this.name = name;
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getId() {
		return this.id;
	}	
	
	@Override
	public String toString() {
		return "GenericRoom [name=" + name + ", id=" + id + "]";
	}
}
