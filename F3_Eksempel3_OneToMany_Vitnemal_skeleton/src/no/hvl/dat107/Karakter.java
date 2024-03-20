package no.hvl.dat107;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(schema = "forelesning3_one2many")
public class Karakter {
	
	@Id // Primærnøkkel
	@GeneratedValue (strategy = GenerationType.IDENTITY) // Autogenerert id (karnr)
	private int karnr;
	
	private String emnekode;
	private LocalDate eksdato;
	private String bokstav;
	
	@ManyToOne // Mangle til ein forbindelse mellom karakter og vitnemål
	@JoinColumn(name = "studnr") // Atributtnøkkel som er foreign-key.
	private Vitnemal vitnemal; // Peker til Vitnemål-klassen for foreign-key "studnr". Ikkje bland inn foreign-key her.
	
	public Karakter() {}
	
	public Karakter(String emnekode, LocalDate eksdato, String bokstav) {
		super();
		this.emnekode = emnekode;
		this.eksdato = eksdato;
		this.bokstav = bokstav;
	}
	
	public void setVitnemal(Vitnemal vitnemal) {
		this.vitnemal = vitnemal;
	}

	@Override
	public String toString() {
		return "\nKarakter [karnr=" + karnr + ", emnekode=" + emnekode + ", eksdato=" + eksdato + ", bokstav=" + bokstav
				+ "]";
	}
	
	
}
