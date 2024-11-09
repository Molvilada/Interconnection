package test.data_structures;

import model.data_structures.ArregloDinamico;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TestArregloDinamico {

	private static final int TAMANO=100;
	private ArregloDinamico arreglo;
	
	@Before
	public void setUp1() {
		arreglo= new ArregloDinamico(TAMANO);
	}
}
