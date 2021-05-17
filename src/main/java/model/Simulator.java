package model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import model.Event.EventType;
import model.Patient.ColorCode;

public class Simulator {
	// coda eventi 
	
	PriorityQueue<Event>queue;
	
	//modello del mondo
	
	List<Patient> patients;
	 PriorityQueue<Patient> waitingRoom;
	
	private int freeStudios;//numero di studi liberi
	 Patient.ColorCode ultimoColore;
	
	//Parametri di input
	
	private int totStudios =3;// NS
	private int numPatients; //NP
	
	private Duration T_ARRIVAL= Duration.ofMinutes(5);
	private Duration DURATION_TRIAGE =Duration.ofMinutes(10);
	private Duration DURATION_WHITE=Duration.ofMinutes(10);
	private Duration DURATION_YELLOW=Duration.ofMinutes(15);
	private Duration DURATION_RED =Duration.ofMinutes(30);
	
	private Duration TIMEOUT_WHITE=Duration.ofMinutes(60);
	private Duration TIMEOUT_YELLOW=Duration.ofMinutes(30);
	private Duration TIMEOUT_RED =Duration.ofMinutes(30);
	
	private LocalTime StartTime =LocalTime.of(8, 00);
	
	private LocalTime endTime = LocalTime.of(20, 00);
	
	//paramentri di output
	
	private int patientsTreated;
	private int patientsAbandoned;
	private int patientsDead;

	
	
	//inizializza il simulatore e crea gli eventi inziali 
	public void init() {
		//inizializza coda eventi
		this.queue = new PriorityQueue<Event>();
		
		//inizializza modello del mondo
		this.patients = new ArrayList<>();
		this.waitingRoom = new PriorityQueue<>();
		this.freeStudios= this.totStudios;
	    ultimoColore = Patient.ColorCode.RED;
		//inizializza i parametri di output
		this.patientsAbandoned =0;
		this.patientsDead=0;
		this.patientsTreated=0;
		
		//inietta  gli eventi di tipo input(Arrival)
		LocalTime ora = this.StartTime;
		int inseriti = 0;
//		Patient.ColorCode colore = ColorCode.WHITE;
		while (ora.isBefore(this.endTime)&&inseriti<this.numPatients) {
			Patient p= new Patient(inseriti ,ora,ColorCode.NEW);
			Event e = new Event(ora,EventType.ARRIVAL,p);
			
			this.queue.add(e);
			this.patients.add(p);
			
			
			inseriti++;
			ora=ora.plus(T_ARRIVAL);
			
		}
		
	}
	public void run() {
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			System.out.println(e);
			processEvent(e);
		}
	}

	private void processEvent(Event e) {
		Patient p = e.getPatient();
		LocalTime ora = e.getTime();
		
		
		switch(e.getType()) {
		case ARRIVAL:
			this.queue.add(new Event(ora.plus(DURATION_TRIAGE),EventType.TRIAGE,p));
			break;
		case TRIAGE:
			p.setColor(prossimoColore());
			if(p.getColor().equals(Patient.ColorCode.WHITE))
				this.queue.add(new Event(ora.plus(TIMEOUT_WHITE),EventType.TIMEOUT,p));
			
			else if(p.getColor().equals(Patient.ColorCode.YELLOW))
				this.queue.add(new Event(ora.plus(TIMEOUT_YELLOW),EventType.TIMEOUT,p));
			else if(p.getColor().equals(Patient.ColorCode.RED))
				this.queue.add(new Event(ora.plus(TIMEOUT_RED),EventType.TIMEOUT,p));
			break;
		case FREE_STUDIO:
			if(this.freeStudios==0)
				return;
//			quale paziente ha il diritto di entrare ????
			Patient primo = this.waitingRoom.poll();
			if(primo!=null) {
				//ammetti il paziente nello studio 
				if(primo.getColor().equals(ColorCode.WHITE))
						this.queue.add(new Event(ora.plus(DURATION_WHITE),EventType.TREATED,primo));
				if(primo.getColor().equals(ColorCode.YELLOW))
					this.queue.add(new Event(ora.plus(DURATION_YELLOW),EventType.TREATED,primo));
				if(primo.getColor().equals(ColorCode.RED))
					this.queue.add(new Event(ora.plus(DURATION_RED),EventType.TREATED,primo));
				
				primo.setColor(ColorCode.TREATING);
				this.freeStudios--;
						
			
			}
			break;
			
		case TIMEOUT:
			Patient.ColorCode colore = p.getColor();
			switch(colore) {
			case WHITE:
				p.setColor(ColorCode.OUT);
				this.patientsAbandoned++;
				this.waitingRoom.remove(p);
				break;
			case YELLOW:
				this.waitingRoom.remove(p);
				p.setColor(ColorCode.RED);
				this.queue.add(new Event(ora.plus(TIMEOUT_RED),EventType.TIMEOUT,p));
				this.waitingRoom.add(p);
				break;
			case RED:
				p.setColor(ColorCode.BLACK);
				this.patientsDead++;
				break;
			default:
//				System.out.println("Errore:Timeout con colore "+colore);
				break;
			}
			break;
		case TREATED:
			this.patientsTreated++;
			p.setColor(ColorCode.OUT);
			this.freeStudios++;
			this.queue.add(new Event(ora,EventType.FREE_STUDIO,null));
			
			break;
			
		case TICK:
			if(this.freeStudios>0&&!this.waitingRoom.isEmpty()) {
				this.queue.add(new Event(ora,EventType.FREE_STUDIO,null));	
			}
			if(ora.isBefore(this.endTime))
				this.queue.add(new Event(ora,EventType.FREE_STUDIO,null));
			break;
			
		}
		
	}
	private Patient.ColorCode prossimoColore() {
		if (ultimoColore.equals(ColorCode.WHITE))
			ultimoColore = ColorCode.YELLOW;
		else if (ultimoColore.equals(ColorCode.YELLOW))
			ultimoColore = ColorCode.RED;
		else
			ultimoColore = ColorCode.WHITE;
		return ultimoColore;
	}
	public int getNumPatients() {
		return numPatients;
	}

	public void setNumPatients(int numPatients) {
		this.numPatients = numPatients;
	}

	public void setTotStudios(int totStudios) {
		this.totStudios = totStudios;
	}

	public void setT_ARRIVAL(Duration t_ARRIVAL) {
		T_ARRIVAL = t_ARRIVAL;
	}

	public void setDURATION_TRIAGE(Duration dURATION_TRIAGE) {
		DURATION_TRIAGE = dURATION_TRIAGE;
	}

	public void setDURATION_WHITE(Duration dURATION_WHITE) {
		DURATION_WHITE = dURATION_WHITE;
	}

	public void setDURATION_YELLOW(Duration dURATION_YELLOW) {
		DURATION_YELLOW = dURATION_YELLOW;
	}

	public void setDURATION_RED(Duration dURATION_RED) {
		DURATION_RED = dURATION_RED;
	}

	public void setTIMEOUT_WHITE(Duration tIMEOUT_WHITE) {
		TIMEOUT_WHITE = tIMEOUT_WHITE;
	}

	public void setTIMEOUT_YELLOW(Duration tIMEOUT_YELLOW) {
		TIMEOUT_YELLOW = tIMEOUT_YELLOW;
	}

	public void setTIMEOUT_RED(Duration tIMEOUT_RED) {
		TIMEOUT_RED = tIMEOUT_RED;
	}
	

}
