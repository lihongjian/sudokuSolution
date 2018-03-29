public class Node {
	private boolean resolved = false;
	private Integer value = 0;
	private Integer[] possibleValues;
	private int x;
	private int y;

	public Node(boolean resolved, Integer value, int x, int y) {
		super();
		this.resolved = resolved;
		this.value = value;
		this.x = x;
		this.y = y;
	}

	public Node(int value, boolean resolved) {
		super();
		this.resolved = resolved;
		this.value = value;
	}

	public Node(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Node() {
	}

	public boolean isResolved() {
		return resolved;
	}

	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public Integer[] getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(Integer[] possibleValues) {
		this.possibleValues = possibleValues;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}