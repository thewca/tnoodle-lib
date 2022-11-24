package cs.cube555;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cs.cube555.Util.*;

import java.io.*;
import java.util.Random;

public class Tools {
    private static Logger logger = LoggerFactory.getLogger(Tools.class);

    public static File pruningTableFolder = null;

	static boolean SaveToFile(String filename, Object obj) {
        if (pruningTableFolder == null) {
            return false;
        }

        if (!pruningTableFolder.exists() && !pruningTableFolder.mkdirs()) {
            return false;
        }

        File pruningTable = new File(pruningTableFolder, filename);

		try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(pruningTable)))) {
			oos.writeObject(obj);
		} catch (Exception e) {
			logger.error("unable to save file", e);
			return false;
		}

		return true;
	}

	static Object LoadFromFile(String filename) {
        if (pruningTableFolder == null) {
            return null;
        }

        File pruningTable = new File(pruningTableFolder, filename);

        if (!pruningTable.exists()) {
            return null;
        }

        try(ObjectInputStream oos = new ObjectInputStream(new BufferedInputStream(new FileInputStream(pruningTable)))) {
            return oos.readObject();
		} catch (Exception e) {
            logger.error("unable to load file", e);
		}

		return null;
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
