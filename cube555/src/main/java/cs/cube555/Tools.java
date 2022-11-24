package cs.cube555;

import static cs.cube555.Util.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Random;

public class Tools {

	static boolean SaveToFile(String filename, Object obj) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
			oos.writeObject(obj);
			oos.close();
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}

	static Object LoadFromFile(String filename) {
		Object ret;
		try {
			ObjectInputStream oos = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
			ret = oos.readObject();
			oos.close();
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
		return ret;
	}

	static Random gen = new Random();

	static CubieCube randomCubieCube(Random gen) {
		CubieCube cc = new CubieCube();
		for (int i = 0; i < 23; i++) {
			swap(cc.xCenter, i, i + gen.nextInt(24 - i));
			swap(cc.tCenter, i, i + gen.nextInt(24 - i));
			swap(cc.wEdge, i, i + gen.nextInt(24 - i));
		}
		int eoSum = 0;
		int eParity = 0;
		for (int i = 0; i < 11; i++) {
			int swap = gen.nextInt(12 - i);
			if (swap != 0) {
				swap(cc.mEdge, i, i + swap);
				eParity ^= 1;
			}
			int flip = gen.nextInt(2);
			cc.mEdge[i] ^= flip;
			eoSum ^= flip;
		}
		cc.mEdge[11] ^= eoSum;
		int cp = 0;
		do {
			cp = gen.nextInt(40320);
		} while (eParity != getParity(cp, 8));
		cc.corner.copy(new CubieCube.CornerCube(cp, gen.nextInt(2187)));
		return cc;
	}

	static CubieCube randomCubieCube() {
		return randomCubieCube(gen);
	}

	public static String randomCube(Random gen) {
		return randomCubieCube(gen).toFacelet();
	}

	public static String randomCube() {
		return randomCube(gen);
	}
}