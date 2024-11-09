package test.logic;

import static org.junit.Assert.*;
import model.logic.Modelo;

import org.junit.Before;
import org.junit.Test;

public class TestModelo<T> {
	
	private static final int CAPACIDAD=100;
	private Modelo modelo;
	
	@Before
	public void setUp1() {
		modelo= new Modelo(CAPACIDAD);
	}

	public void setUp2() {
		
	}

	@Test
	public void testModelo() {
        assertNotNull(modelo);
		assertEquals(0, modelo.darTamano());  // Modelo con 0 elementos presentes.
	}

	@Test
	public void testDarTamano() {
		setUp2();
		int x=modelo.darTamano();
		assertEquals("No es el tamaño correcto", 0,x);
		// TODO
	}
}
