package com.gelakinetic.cyvasse.gameHelpers;

import com.gelakinetic.cyvasse.fragments.CyvasseGameFragment;
import com.gelakinetic.cyvasse.gameHelpers.Terrain.TerrainType;
import com.gelakinetic.cyvasse.gameHelpers.Unit.UnitType;

public class Tile {
	public Terrain	terrain;
	public Unit			unit;

	public Tile(int x, int y, TerrainType terrain, UnitType unit, byte player, CyvasseGameFragment me) {
		if (terrain != null) {
			this.terrain = new Terrain(x, y, me, terrain, player);
		}
		if (unit != null) {
			this.unit = new Unit(x, y, me, unit, player);
		}
	}

	public Tile(Unit u, Terrain t) {
		this.terrain = t;
		this.unit = u;
	}

	public byte[] saveTile() {
		// Three bytes of terrainInfo and another three of unitInfo
		byte[] terrainInfo;
		byte[] unitInfo;
		if (terrain != null) {
			terrainInfo = this.terrain.saveTerrain();
		}
		else {
			// Invalid value
			terrainInfo = new byte[Terrain.BYTE_SIZE];
			for (int i = 0; i < Terrain.BYTE_SIZE; i++) {
				terrainInfo[i] = -2;
			}
		}
		if (unit != null) {
			unitInfo = this.unit.saveUnit();
		}
		else {
			// Invalid value
			unitInfo = new byte[Unit.BYTE_SIZE];
			for (int i = 0; i < Unit.BYTE_SIZE; i++) {
				unitInfo[i] = -2;
			}
		}

		byte b[] = new byte[6];
		System.arraycopy(terrainInfo, 0, b, 0, terrainInfo.length);
		System.arraycopy(unitInfo, 0, b, terrainInfo.length, unitInfo.length);
		return b;
	}
}