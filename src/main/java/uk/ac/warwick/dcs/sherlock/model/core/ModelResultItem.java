package uk.ac.warwick.dcs.sherlock.model.core;

import uk.ac.warwick.dcs.sherlock.api.model.data.IModelResultItem;
import uk.ac.warwick.dcs.sherlock.api.model.data.IPairedBlocks;

import java.util.*;

public class ModelResultItem implements IModelResultItem {

	private List<IPairedBlocks> pairedBlocks;

	public ModelResultItem() {
		this.pairedBlocks = new LinkedList<>();
	}

	@Override
	public void addPairedBlocks(IPairedBlocks blockPair) {
		this.pairedBlocks.add(blockPair);
	}

	@Override
	public List<IPairedBlocks> getAllPairedBlocks() {
		return this.pairedBlocks;
	}
}
