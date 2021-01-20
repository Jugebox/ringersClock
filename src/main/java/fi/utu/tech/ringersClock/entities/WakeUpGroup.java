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
	private Integer hour;
	private Integer minutes;
	private boolean notRaining;
	private boolean temp;
	ArrayList<UUID> members = new ArrayList<>();

	public WakeUpGroup(UUID id, String name, Integer hour, Integer minutes) {
		super();
		this.ID = id;
		this.name = name;
		this.hour = hour;
		this.minutes = minutes;
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

	public ArrayList<UUID> getMembers() {
		return members;
	}

	public UUID getLeader() {
		return members.get(0);
	}

	public void addMemeber(UUID memberId) {
		members.add(memberId);
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getHour() {
		return hour;
	}

	public int getMinutes() {
		return minutes;
	}

	@Override
	public String toString() {
		return this.getName();
	}

}