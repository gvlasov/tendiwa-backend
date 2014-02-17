package org.tendiwa.core;

import org.tendiwa.core.meta.Chance;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class CellCollection {
public Location location;
ArrayList<EnhancedPoint> unoccupied;
boolean hasCells = true;
private ArrayList<EnhancedPoint> cells;

public CellCollection(Collection<EnhancedPoint> cls, Location loc) {
	if (cls.isEmpty()) {
		throw new Error("Can't create an empty cell collection: argument is an empty collection");
	}
	cells = new ArrayList<>(cls);
	location = loc;
}

//	public Character setCharacter(String ammunitionType, String name) {
//		if (!hasCells) {
//			throw new Error("No more cells");
//		}
//		int cellIndex = Chance.rand(0, cells.size()-1);
//		EnhancedPoint cell = cells.get(cellIndex);
//		unsetCell(cellIndex);
//		return location.createCharacter(ammunitionType, name, cell.x, cell.y);
//	}
public static ArrayList<EnhancedPoint> rectangleToCellsList(Rectangle r) {
	ArrayList<EnhancedPoint> answer = new ArrayList<>();
	for (int i = r.x; i < r.x + r.width; i++) {
		for (int j = r.y; j < r.y + r.height; j++) {
			answer.add(new EnhancedPoint(i, j));
		}
	}
	return answer;
}

public int size() {
	return cells.size();
}

//	public void placeCharacters(ArrayList<GeneratorCharacterGroup> chs) {
//		/*
//		 * ���������� � ������� ���������� characters - ������ �� ���������,
//		 * ������� ����� ����� ���� �� ���� �����: 1. "ammunitionType" - ��� ������������
//		 * ��������� 2. ["ammunitionType",amount(,fraction)] - ��� � ����������
//		 * ����������� ����������, � ����� ������� � ������ ������ ���������
//		 * ������ ���� ��������. ������ �������� ����������� �� ���������
//		 * ������.
//		 */
//		for (GeneratorCharacterGroup ch : chs) {
//			// �������� �� ���� ����������
//			for (int i = 0; i < ch.amount; i++) {
//				if (!hasCells) {
//					// ���� � ������� ������ �� �������� ������ �� �����
//					// ��������� ������, ����� �� ���������� �������
//					// (������ ��������� ����� �������, ��� ��������� ��������
//					// this->cells[0] (��. ����� unsetCell))
//					throw new Error(
//							"� cellCollection �� �������� ����� ��� ����� ���������� - ��������� ������");
//				}
//				int cellIndex = Chance.rand(0, cells.size()-1);
//				EnhancedPoint cell = cells.get(cellIndex);
//				// ��������� ���������
//				location.createCharacter(ch.ammunitionType, ch.name, cell.x, cell.y);
//				unsetCell(cellIndex);
//			}
//		}
//	}
public void removeCellsCloseTo(int x, int y, int distance) {
	int size = cells.size();
	for (EnhancedPoint c : cells) {
		if (c.distance(x, y) <= distance) {
			cells.remove(c);
			size--;
		}
	}
}

protected void unsetCell(EnhancedPoint cell) {
	cells.remove(cell);
	if (cells.isEmpty()) {
		hasCells = false;
	}
}

/**
 * Randomly puts some elements
 *
 * @param placeable
 * 	What ammunitionType of entity to put.
 * @param amount
 * 	Total amount of elements to put.
 */
public void setElements(PlaceableInCell placeable, int amount) {
	for (int i = 0; i < amount; i++) {
		if (!hasCells) {
			throw new RuntimeException("CellCollection has no cells left");
		}
		int cellIndex = Chance.rand(0, cells.size() - 1);
		EnhancedPoint cell = cells.get(cellIndex);
		placeable.place(location.getActivePlane(), cell.x, cell.y);
		unsetCell(cell);
	}
}

/**
 * @param amount
 */
public void setObjects(ObjectType type, int amount) {
	for (int i = 0; i < amount; i++) {
		if (!hasCells) {
			throw new RuntimeException("CellCollection has no cells left");
		}
		int cellIndex = Chance.rand(0, cells.size() - 1);
		EnhancedPoint cell = cells.get(cellIndex);
		EntityPlacer.place(location.getActivePlane(), type, cell.x, cell.y);
		unsetCell(cell);
	}
}

public EnhancedPoint getRandomCell() {
	return cells.get(Chance.rand(0, cells.size() - 1));
}

public void fillWithElements(PlaceableInCell placeable) {
	// TODO Auto-generated method stub
	for (EnhancedPoint c : cells) {
		placeable.place(location.getActivePlane(), c.x, c.y);
	}
}

public ArrayList<EnhancedPoint> setElementsAndReport(PlaceableInCell placeable, int amount) {
	ArrayList<EnhancedPoint> coords = new ArrayList<>();
	for (int i = 0; i < amount; i++) {
		if (!hasCells) {
			throw new RuntimeException("CellCollection has no cells left");
		}
		int cellIndex = Chance.rand(0, cells.size() - 1);
		EnhancedPoint cell = cells.get(cellIndex);
		placeable.place(location.getActivePlane(), cell.x, cell.y);
		unsetCell(cell);
		coords.add(cell);
	}
	return coords;
}

public EnhancedPoint setElementAndReport(PlaceableInCell placeable) {
	if (!hasCells) {
		throw new Error("No more cells");
	}
	int cellIndex = Chance.rand(0, cells.size() - 1);
	EnhancedPoint cell = cells.get(cellIndex);
	placeable.place(location.getActivePlane(), cell.x, cell.y);
	unsetCell(cell);
	return cell;
}

public EnhancedPoint setObjectAndReport(ObjectType objectType) {
	if (!hasCells) {
		throw new Error("No more cells");
	}
	int cellIndex = Chance.rand(0, cells.size() - 1);
	EnhancedPoint cell = cells.get(cellIndex);
	EntityPlacer.place(location.getActivePlane(), objectType, cell.x, cell.y);

	unsetCell(cell);
	return cell;
}

}
