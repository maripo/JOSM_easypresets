package org.maripo.josm.easypresets.data;

import java.util.ArrayList;

public class GroupStack {
	private ArrayList<EasyPresets> stack = new ArrayList<>();
	static final String SEPA = "/";
	EasyPresets root;
	
	public GroupStack() {
		super();
		root = null;
	}
	
	public GroupStack(EasyPresets root) {
		this();
		setRoot(root);
	}
	
	public void setRoot(EasyPresets item) {
		clear();
		this.root = item;
		push(this.root);
	}
	
	public void clear() {
		stack.clear();
	}
	
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
			setRoot(root);
			return root;
		}
		if (rawName.endsWith(SEPA)) {
			rawName = rawName.substring(0, rawName.length()-SEPA.length());
		}
		while (stack.size() > 0) {
			EasyPresets item = getLast();
			if (item.getRawName().equals(rawName)) {
				return item;
			}
			pop();
		}
		setRoot(root);
		return root;
	}
}
