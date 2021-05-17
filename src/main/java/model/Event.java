package model;

import java.time.LocalTime;

public class Event implements Comparable<Event> {
	
	enum  EventType{
		ARRIVAL,
		TRIAGE,
		TIMEOUT,
		FREE_STUDIO,
		TREATED,
		TICK,//timer per controllare se ci sono studi liberi 
	};
	private LocalTime time;
	private EventType type;
	private Patient patient;
	public Event(LocalTime time, EventType type, Patient patient) {
		super();
		this.time = time;
		this.type = type;
		this.patient = patient;
	}
	public LocalTime getTime() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	public EventType getType() {
		return type;
	}
	public void setType(EventType type) {
		this.type = type;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	@Override
	public int compareTo(Event other) {
		// TODO Auto-generated method stub
		return this.time.compareTo(other.time);
	}
	@Override
	public String toString() {
		return "Event [time=" + time + ", type=" + type + ", patient=" + patient + "]";
	}
	

}
