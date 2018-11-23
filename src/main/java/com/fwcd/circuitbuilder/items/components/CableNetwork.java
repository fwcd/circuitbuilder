package com.fwcd.circuitbuilder.items.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fwcd.circuitbuilder.core.CircuitCell;
import com.fwcd.circuitbuilder.core.CircuitGrid;
import com.fwcd.circuitbuilder.model.cable.CableColor;
import com.fwcd.circuitbuilder.utils.RelativePos;
import com.fwcd.fructose.Option;

public class CableNetwork {
	private final CircuitGrid grid;
	
	private Option<CableColor> color = Option.empty();
	private Map<RelativePos, Cable> cables = new HashMap<>();
	private Set<Cable> cableSet = new HashSet<>();
	
	public CableNetwork(CircuitGrid grid) {
		this.grid = grid;
	}
	
	private boolean add(RelativePos pos, Cable cable) {
		if (!color.isPresent()) {
			color = Option.of(cable.getColor());
		}
		
		if (color.filter(cable.getColor()::equals).isPresent()) {
			cables.put(pos, cable);
			cableSet.add(cable);
			return true;
		} else {
			return false;
		}
	}
	
	public void build(RelativePos startPos) {
		buildRecursively(grid.getCell(startPos));
	}
	
	private void buildRecursively(CircuitCell cell) {
		for (CircuitComponent component : cell) {
			if (component instanceof Cable && !contains((Cable) component)) {
				if (add(cell.getPos(), (Cable) component)) {
					((Cable) component).setNetwork(this);
					for (CircuitCell neighborCell : grid.getNeighbors(cell.getPos()).values()) {
						buildRecursively(neighborCell);
					}
				}
			}
		}
	}
	
	public boolean contains(Cable cable) {
		return cableSet.contains(cable);
	}
	
	public boolean isPowered() {
		for (RelativePos cablePos : cables.keySet()) {
			Cable cable = cables.get(cablePos);
			
			if (cable.isConnectedToEmitter(grid.getNeighbors(cablePos))) {
				return true;
			}
		}
		
		return false;
	}
}