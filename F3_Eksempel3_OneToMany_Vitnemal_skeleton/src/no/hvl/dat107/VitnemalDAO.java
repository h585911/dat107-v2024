package no.hvl.dat107;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

public class VitnemalDAO {

    private EntityManagerFactory emf;

    public VitnemalDAO() {
        emf = Persistence.createEntityManagerFactory("vitnemalPU");
    }
    
    /* --------------------------------------------------------------------- */

    public Vitnemal hentVitnemalForStudent(int studnr) {
        
        EntityManager em = emf.createEntityManager();
        try {
        	
        	return em.find(Vitnemal.class, studnr);
        	
        } finally {
            em.close();
        }
    }

    /* --------------------------------------------------------------------- */

    public Karakter hentKarakterForStudentIEmne(int studnr, String emnekode) {
        
        EntityManager em = emf.createEntityManager();
        
        try {
        	
        	String q = "SELECT k FROM Karakter AS k WHERE k.vitnemal.studnr = :studnr AND k.emnekode = :emnekode";
        	TypedQuery<Karakter> query = em.createQuery(q, Karakter.class);
        	query.setParameter("studnr", studnr);
        	query.setParameter("emnekode", emnekode);
        	
        	return query.getSingleResult();
        	
        } catch (NoResultException e) {
        	return null;
        } finally {
            em.close();
        }
    }
    
    /* --------------------------------------------------------------------- */

    public void registrerKarakterForStudent(int studnr, String emnekode, LocalDate eksdato, String bokstav) {
        
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        
        try {
        	tx.begin();
        	
        	Karakter gml = hentKarakterForStudentIEmne(studnr, emnekode);
        	// gml er "detatched".
        	
        	Vitnemal vm = em.find(Vitnemal.class, studnr);
        	// vm er "managed"
        	
        	if(gml != null) {
        		vm.fjernKarakter(gml);
        		
        		gml = em.merge(gml);
        		// Her blir gml "managed"
        		
        		em.remove(gml);
        		// Her blir gml "removed"
        		
        		em.flush();
        	}
        	
        	Karakter ny = new Karakter(emnekode, eksdato, bokstav);
        	// ny er "new"
        	
        	ny.setVitnemal(vm);
        	vm.leggTilKarakter(ny);
        	
        	em.persist(ny);
        	// Her blir ny "managed" og har fått id primærnøkkel.
        	
        	tx.commit();
        	
        } finally {
            em.close();
        }
    }
    
    /* --------------------------------------------------------------------- */

    public List<Karakter> hentKarakterlisteForFerdige(String emnekode) {
        
        EntityManager em = emf.createEntityManager();
        
        try {
        	
        	/* 
        	   	Finne liste av DAT107-karakterer for studenter som er
				ferdig (har sluttdato). Forventer kun denne:
				(1, DAT107, '2022-05-30', 'A', 123456)
				
        	  I SQL kan det se slik ut:
        	  		SELECT k.* 
					FROM karakter AS k 
					NATURAL JOIN vitnemal AS v
					WHERE v.studieslutt IS NOT NULL
					AND k.emnekode LIKE 'DAT107';
        	 */
        	
        	String jpqlQuery = """
        			select k 
        			from Karakter as k, 
        			k.vitnemal as v 
        			where v.studieslutt is not null
        			and k.emnekode like :emnekode""";
        	
			TypedQuery<Karakter> query = em.createQuery(jpqlQuery, Karakter.class);
			query.setParameter("emnekode", emnekode);

			return query.getResultList();
        	
        } finally {
            em.close();
        }
    }
    

}

