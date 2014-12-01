package com.gelakinetic.cyvasse.gameHelpers;

import java.util.ArrayList;

import com.gelakinetic.cyvasse.fragments.CyvasseGameFragment;

/**
 * This class calculates potential movements and attacks for units on the board,
 * as well as handles combat
 * 
 * @author Adam Feinstein
 * 
 */
public class MovementCalculator {

	private class DijkstraNode {
		boolean	visited		= false;
		int			distance	= 1000;
	}

	private final Coord	direction[]	= { new Coord(0, 1), new Coord(1, 0), new Coord(0, -1), new Coord(-1, 0) };
	private final int		largeNum		= 10000;

	/**
	 * This function finds all potential attacks for a unit. It ignores any units
	 * on the target spaces
	 * 
	 * @param attackingCoord
	 *          The coordinate of the unit about to attack
	 * @return An ArrayList<Coord> containing all the potential attack targets
	 */
	public ArrayList<Coord> findPotentialAttacks(Tile[][] board, Coord attackingCoord) {

		ArrayList<Coord> potentialAttacks = new ArrayList<Coord>();
		Unit unit = board[attackingCoord.x][attackingCoord.y].unit;

		/*
		 * figure out attack ranges by iterating over the square defined by
		 * minAttack and maxAttack
		 */
		int maxAttackRange = unit.maxAttackRange;
		int minAttackRange = unit.minAttackRange;

		for (int i = attackingCoord.x - maxAttackRange; i <= attackingCoord.x + maxAttackRange; i++) {
			for (int j = attackingCoord.y - maxAttackRange; j <= attackingCoord.y + maxAttackRange; j++) {
				if (0 <= i && i < CyvasseGameFragment.NUM_TILES && CyvasseGameFragment.LOWER_TILE_BOUND <= j && j < CyvasseGameFragment.UPPER_TILE_BOUND) {
					if (minAttackRange <= attackingCoord.distBetween(new Coord(i, j)) && attackingCoord.distBetween(new Coord(i, j)) <= maxAttackRange) {
						potentialAttacks.add(new Coord(i, j));
					}
				}
			}
		}
		return potentialAttacks;
	}

	/**
	 * This function figures out what happens in combat. It finds all units which
	 * are attacking the defending unit, including adjacent units which did not
	 * start the attack. It then checks strengths, does combat, and returns lists
	 * of which units attacked successfully and which died.
	 * 
	 * @param board
	 *          The game board with units, terrain, etc
	 * @param defendingCoord
	 *          The Coord of the unit being attacked
	 * @param attackingCoord
	 *          The Coord of the unit which started the attack
	 * @return
	 */
	public UnitsInvolvedInCombat fightUnit(Tile[][] board, Coord defendingCoord, Coord attackingCoord) {
		Unit defendingUnit = board[defendingCoord.x][defendingCoord.y].unit;

		UnitsInvolvedInCombat uiic = new UnitsInvolvedInCombat();
		ArrayList<Unit> attackingUnits = new ArrayList<Unit>();
		ArrayList<Coord> unitsToCounterattack = new ArrayList<Coord>();

		/*
		 * Only look at tiles adjacent to the defending unit to attack it. Make sure
		 * it has min attack range 1.Then add the attacking coord if it wasn't
		 * already added. It wouldn't be added if it was artillery
		 */
		for (Coord dir : direction) {
			int i = dir.x + defendingCoord.x;
			int j = dir.y + defendingCoord.y;
			if (0 <= i && i < CyvasseGameFragment.NUM_TILES) {
				if (CyvasseGameFragment.LOWER_TILE_BOUND <= j && j < CyvasseGameFragment.UPPER_TILE_BOUND) {
					if (board[i][j].unit != null && board[i][j].unit.player != defendingUnit.player) {
						if (board[i][j].unit.minAttackRange == 1) {
							attackingUnits.add(board[i][j].unit);
							uiic.attackingUnits.add(new Coord(i, j));
							unitsToCounterattack.add(new Coord(i, j));
						}
					}
				}
			}
		}
		if (!uiic.attackingUnits.contains(attackingCoord)) {
			uiic.attackingUnits.add(attackingCoord);
			attackingUnits.add(board[attackingCoord.x][attackingCoord.y].unit);
		}

		/*
		 * Sum the powers of the attacking units, compare to the defending power
		 */
		int totalAttackingStrength = 0;
		for (Unit attacker : attackingUnits) {
			totalAttackingStrength += attacker.strength;
		}

		/*
		 * Combat Wombat. All the attacking units are in uiic.attackingUnits, and
		 * uiic.deadUnits is empty
		 */
		if (totalAttackingStrength == defendingUnit.strength - 1) {
			/*
			 * Trade, everyone dies Kill all units in counterattack range if the
			 * defending unit can counterattack And then kill the defending unit
			 */
			if (defendingUnit.minAttackRange == 1) {
				for (Coord c : unitsToCounterattack) {
					uiic.attackingUnits.remove(c);
					uiic.deadUnits.add(c);
				}
			}
			uiic.deadUnits.add(defendingCoord);
		}
		else if (totalAttackingStrength >= defendingUnit.strength) {
			/*
			 * Attackers win outright, they're already in the right ArrayList
			 */
			uiic.deadUnits.add(defendingCoord);
		}
		else {
			/*
			 * Defenders win outright, destroy all units in counterattack range if the
			 * defending unit wasn't artillery
			 */
			if (defendingUnit.minAttackRange == 1) {
				for (Coord c : unitsToCounterattack) {
					uiic.attackingUnits.remove(c);
					uiic.deadUnits.add(c);
				}
				if (uiic.deadUnits.size() > 0) {
					uiic.attackingUnits.add(defendingCoord);
				}
			}
		}
		return uiic;
	}

	/**
	 * This function uses Dijkstra's Algorithm to find all tiles the selected unit
	 * can move to, and all tiles the selected unit can attack.
	 * 
	 * @param board
	 *          The game board with units, terrain, etc
	 * @param startingNode
	 *          The coordinate of the unit which will have it's moves and attacks
	 *          calculated
	 * @return Lists containing all the valid moves, and all the valid attacks
	 */
	public MovesAndAttacks findMovesThenAttacks(Tile[][] board, Coord startingNode) {
		DijkstraNode map[][] = new DijkstraNode[CyvasseGameFragment.NUM_TILES][CyvasseGameFragment.NUM_TILES + CyvasseGameFragment.STAGING_AREA_TILES];
		ArrayList<Coord> unvisitedNodes = new ArrayList<Coord>(CyvasseGameFragment.NUM_TILES * CyvasseGameFragment.NUM_TILES);

		MovesAndAttacks maa = new MovesAndAttacks();

		Coord currentNode = startingNode;
		Unit movingUnit = board[startingNode.x][startingNode.y].unit;
		boolean isArtillery = movingUnit.minAttackRange > 1;

		/*
		 * Initialize the graph and unvisitedNodes
		 */
		for (int i = 0; i < CyvasseGameFragment.NUM_TILES; i++) {
			for (int j = CyvasseGameFragment.LOWER_TILE_BOUND; j < CyvasseGameFragment.UPPER_TILE_BOUND; j++) {
				map[i][j] = new DijkstraNode();
				unvisitedNodes.add(new Coord(i, j));
			}
		}

		/*
		 * The distance to oneself is clearly zero
		 */
		map[startingNode.x][startingNode.y].distance = 0;

		/*
		 * Look at all of the currentNode's neighbors, and see if the distance
		 * through the currentNode is smaller than the current distance. When done,
		 * mark the currentNode as visited. Also keep track of the min distance
		 * neighbor
		 */
		while (unvisitedNodes.size() > 0) {

			boolean canMoveToNode = false;
			if (map[currentNode.x][currentNode.y].distance <= movingUnit.moveDistance
					&& (board[currentNode.x][currentNode.y].unit == null || currentNode.equals(startingNode))) {
				maa.moves.add(currentNode);
				canMoveToNode = true;
			}

			// Test all neighbors of the current node (aka NSEW)
			for (Coord dir : direction) {
				Coord test = new Coord(currentNode.x + dir.x, currentNode.y + dir.y);
				if (0 <= test.x && test.x < CyvasseGameFragment.NUM_TILES) {
					if (CyvasseGameFragment.LOWER_TILE_BOUND <= test.y && test.y < CyvasseGameFragment.UPPER_TILE_BOUND) {
						/*
						 * If the unit can move to the parent node, and the unit is not
						 * artillery, then the unit can attack this node
						 */
						if (!isArtillery && canMoveToNode) {
							if (!maa.attacks.contains(test)) {
								maa.attacks.add(test);
							}
						}
						/*
						 * If this node hasn't been visited yet, check to see if the
						 * distance through the current node is smaller than the previous
						 * distance. If it is, change the distance
						 */
						if (!map[test.x][test.y].visited) {
							map[test.x][test.y].distance = Math.min(map[test.x][test.y].distance,
									map[currentNode.x][currentNode.y].distance + getDistanceToNode(board, movingUnit, test));
						}
					}
				}
			}
			/*
			 * Mark the current node as visited, and remove it from the unvisited list
			 */
			map[currentNode.x][currentNode.y].visited = true;
			unvisitedNodes.remove(currentNode);

			/*
			 * If there are no more unvisited nodes, we're done
			 */
			if (unvisitedNodes.size() == 0) {
				break;
			}

			/*
			 * Find the next unvisited node with the minimum distance
			 */
			int nextMinDist = largeNum;
			currentNode = null;
			for (Coord node : unvisitedNodes) {
				if (map[node.x][node.y].distance <= nextMinDist) {
					nextMinDist = map[node.x][node.y].distance;
					currentNode = node;
				}
			}
		}

		/*
		 * TODO This is a total hack because of crossbowmen's attack range of 2. It
		 * tries to expand the attacks in all directions. Watch out for concurrent
		 * modification exceptions!
		 */
		int maxAttackRange = movingUnit.maxAttackRange;
		ArrayList<Coord> newAttacks = new ArrayList<Coord>();
		if (movingUnit.minAttackRange == 1) {
			while (maxAttackRange > 1) {
				for (Coord attack : maa.attacks) {
					for (Coord dir : direction) {
						Coord test = new Coord(attack.x + dir.x, attack.y + dir.y);
						if (0 <= test.x && test.x < CyvasseGameFragment.NUM_TILES) {
							if (CyvasseGameFragment.LOWER_TILE_BOUND <= test.y && test.y < CyvasseGameFragment.UPPER_TILE_BOUND) {
								if (!maa.attacks.contains(test) && !newAttacks.contains(test)) {
									newAttacks.add(test);
								}
							}
						}
					}
				}
				for (Coord attack : newAttacks) {
					maa.attacks.add(attack);
				}
				maxAttackRange--;
			}
		}
		return maa;
	}

	/**
	 * This function returns the movement cost of moving onto a node for a given
	 * unit
	 * 
	 * @param movingUnit
	 *          The unit which is moving
	 * @param test
	 *          the node the unit is moving to
	 * @return
	 */
	private int getDistanceToNode(Tile[][] board, Unit movingUnit, Coord test) {
		/*
		 * If the target node is occupied by an enemy, we aren't moving there
		 */
		if (board[test.x][test.y].unit != null && board[test.x][test.y].unit.player != movingUnit.player) {
			return largeNum - 1;
		}
		/*
		 * Otherwise return the penalty. Ignore friendly units because they can be
		 * passed through
		 */
		switch (board[test.x][test.y].terrain.terrainType) {
			case MOUNTAIN:
				return movingUnit.mountainPenalty;
			case RIVER:
				return movingUnit.riverPenalty;
			case FIELD:
				return 1;
			case FORTRESS:
				if (board[test.x][test.y].terrain.player == movingUnit.player) {
					return 1;
				}
				return largeNum - 1; // No walking on enemy fortresses
			default:
				break;
		}
		return largeNum;
	}

	/**
	 * This is used to see where the inactive player's units can attack.
	 * 
	 * @param board
	 *          The game board with units, terrain, etc
	 * @param selectedUnit
	 *          The enemy unit which is having it's moves or attacks calculated
	 * @return
	 */
	public MovesAndAttacks findEnemyMovesAndAttacks(Tile[][] board, Coord selectedUnit) {
		/*
		 * If the unit isn't artillery, see where it can move and then attack
		 */
		if (board[selectedUnit.x][selectedUnit.y].unit.minAttackRange == 1) {
			return findMovesThenAttacks(board, selectedUnit);
		}
		/*
		 * If the unit is artillery, see where it can attack without moving. This is
		 * more relevant than seeing where it can move.
		 */
		else {
			MovesAndAttacks artilleryAttacks = new MovesAndAttacks();
			int maxAttackRange = board[selectedUnit.x][selectedUnit.y].unit.maxAttackRange;
			int minAttackRange = board[selectedUnit.x][selectedUnit.y].unit.minAttackRange;

			for (int i = selectedUnit.x - maxAttackRange; i <= selectedUnit.x + maxAttackRange; i++) {
				for (int j = selectedUnit.y - maxAttackRange; j <= selectedUnit.y + maxAttackRange; j++) {
					if (0 <= i && i < CyvasseGameFragment.NUM_TILES && CyvasseGameFragment.LOWER_TILE_BOUND <= j && j < CyvasseGameFragment.UPPER_TILE_BOUND) {
						if (minAttackRange <= selectedUnit.distBetween(new Coord(i, j)) && selectedUnit.distBetween(new Coord(i, j)) <= maxAttackRange) {
							artilleryAttacks.attacks.add(new Coord(i, j));
						}
					}
				}
			}
			return artilleryAttacks;
		}
	}
}
