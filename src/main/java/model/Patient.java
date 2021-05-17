package model;

import java.time.LocalTime;

public class Patient implements Comparable<Patient>{
	
	public enum ColorCode{
		NEW, //IN TRIAGE
		WHITE,YELLOW,RED,BLACK,//IN SALA D'ATTESA
		OUT,//A CASA (HO ABBANDONATO O SONO STATO CURATO )
		TREATING
	}
	
	private int num;
	private LocalTime arrivalTime;
	private ColorCode color;
	public Patient(int num,LocalTime arrivalTime, ColorCode color) {
		super();
		this.num=num;
		this.arrivalTime = arrivalTime;
		this.color = color;
	}
	public LocalTime getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(LocalTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public ColorCode getColor() {
		return color;
	}
	public void setColor(ColorCode color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "Patient [num=" + num + ", arrivalTime=" + arrivalTime + ", color=" + color + "]";
	}
	@Override
	public int compareTo(Patient other) {
		// Decide la priorita -1 se passa this +1 se passa other  e 0 se passano uguale
		if(this.color.equals(other.color)) {
			return this.arrivalTime.compareTo(other.arrivalTime);
		}else if(this.color.equals(Patient.ColorCode.RED))
			return -1;
		else if (other.color.equals(Patient.ColorCode.RED)) {
			return +1;
		}if(this.color.equals(Patient.ColorCode.YELLOW)) //caso Y-W
			return -1;
		else{//caso W-Y
			return +1;
		}
	
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + num;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Patient other = (Patient) obj;
		if (num != other.num)
			return false;
		return true;
	}

	
	

}
