package fi.utu.tech.ringersClock.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/*
 * Entity class presenting a WakeUpGroup. The class is not complete.
 * You need to add some variables.
 */

public class WakeUpGroup implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private UUID ID;
	private int start;
	private int end;
	private boolean notRaining;
	private boolean tempOver0;
	ArrayList<String> members = new ArrayList<>();

	public WakeUpGroup(UUID id, String name) {
		super();
		this.ID = id;
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public UUID getID() {
		return this.ID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setID(UUID ID) {
		this.ID = ID;
	}

	public ArrayList<String> getMembers() {
		return members;
	}

	public String getLeader() {
		return members.get(0);
	}

	@Override
	public String toString() {
		return this.getName();
	}

}