package org.maripo.josm.easypresets.data;

import java.util.ArrayList;

public class GroupStack {
	private ArrayList<EasyPresets> stack = new ArrayList<>();
	static final String SEPA = "/";
	
	public void push(EasyPresets item) {
		stack.add(item);
	}
	
	EasyPresets pop() {
		EasyPresets item = getLast();
		stack.remove(stack.size() -1);
		return item;
	}

	private EasyPresets getLast() {
		return stack.get(stack.size() -1);
	}

	public EasyPresets pop(String rawName) {
		if (rawName.isEmpty()) {
			stack.clear();
			return null;
		}
		if (rawName.endsWith(SEPA)) {
			rawName = rawName.substring(0, rawName.length()-SEPA.length());
		}
		while (stack.size() > 0) {
			EasyPresets item = getLast();
			if (item.getLocaleName().equals(rawName)) {
				return item;
			}
			pop();
		}
		return null;
	}
}
