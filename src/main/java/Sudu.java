import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Sudu {

	public static Node[][] result = new Node[9][9];
	public static boolean end = false;
	static {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++)
				result[i][j] = new Node(i, j);
		}
	}

	public static boolean check() {
		boolean finish = true;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (!result[i][j].isResolved()) {
					finish = false;
					return finish;
				}
			}
		}
		return finish;
	}

	public static boolean isSetEqual(Set<Integer> set1, Set<Integer> set2) {
		if (set1.size() != set2.size()) {
			return false;
		}
		boolean isFullEqual = true;
		Iterator<Integer> ite = set2.iterator();
		while (ite.hasNext()) {
			if (!set1.contains(ite.next())) {
				isFullEqual = false;
				break;
			}
		}
		return isFullEqual;
	}

	public static boolean tryCheck() {
		Set<Integer> rows = new HashSet<>();
		Set<Integer> cols = new HashSet<>();
		Set<Integer> grids = new HashSet<>();
		Set<Integer> all = IntStream.range(1, 10).boxed().collect(Collectors.toSet());
		boolean isRight = false;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				rows.add(result[i][j].getValue());
				cols.add(result[j][i].getValue());
			}
			if (isSetEqual(rows, cols)) {
				all.removeAll(cols);
				if (all.size() == 0) {
					all = IntStream.range(1, 10).boxed().collect(Collectors.toSet());
					rows.clear();
					cols.clear();
					isRight = true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		if (isRight) {
			all = IntStream.range(1, 10).boxed().collect(Collectors.toSet());
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					grids.add(result[j / 3][j % 3].getValue());
				}
				all.removeAll(grids);
				if (all.size() == 0) {
					all = IntStream.range(1, 10).boxed().collect(Collectors.toSet());
					grids.clear();
				} else {
					return false;
				}
			}
		}
		return true;
	}

	public static void printResult() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++)
				System.out.print(result[i][j].getValue() + "  ");
			System.out.println();
		}
	}

	public static List<Node> remainNodes() {
		List<Node> unFilledNodes = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (!result[i][j].isResolved()) {
					unFilledNodes.add(result[i][j]);
				}
			}
		}
		unFilledNodes.sort(new Comparator<Node>() {
			@Override
			public int compare(Node node, Node other) {
				return node.getPossibleValues().length - other.getPossibleValues().length;
			}
		});
		return unFilledNodes;
	}

	public static void init() {
		result[0][5] = new Node(2	, true);
/*
		result[0][5] = new Node(3, true);
*/
		result[0][8] = new Node(8, true);

		result[1][1] = new Node(4, true);
		result[1][6] = new Node(6, true);
/*
		result[1][3] = new Node(2, true);
*/

		result[2][2] = new Node(6, true);
		result[2][5] = new Node(5, true);
		/*result[2][8] = new Node(2, true);*/

		result[3][1] = new Node(3, true);
		result[3][6] = new Node(4, true);
		result[3][7] = new Node(9, true);

		result[4][0] = new Node(5, true);
		result[4][5] = new Node(7, true);

		result[5][6] = new Node(8, true);
	/*	result[5][2] = new Node(6, true);
		result[5][6] = new Node(2, true);
		result[5][7] = new Node(4, true);*/

		result[6][0] = new Node(2, true);
		result[6][8] = new Node(5, true);
		/*result[6][7] = new Node(9, true);*/

		result[7][4] = new Node(9, true);
/*		result[7][5] = new Node(8, true);
		result[7][6] = new Node(6, true);*/

		result[8][1] = new Node(8, true);
		result[8][4] = new Node(4, true);
/*
		result[8][8] = new Node(4, true);
*/
	}

	public static boolean exclusive() {
		boolean solve = false;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (!result[i][j].isResolved()) {
					Integer[] possible = lineScan(result[i][j]).toArray(new Integer[0]);
					if (possible.length == 1) {
						result[i][j].setValue(possible[0]);
						result[i][j].setResolved(true);
						solve = true;
					} else {
						result[i][j].setPossibleValues(possible);
					}
				}
			}
		}
		return solve;
	}

	public static Set<Integer> lineScan(Node node) {
		Set<Integer> rows = new HashSet<>();
		Set<Integer> cols = new HashSet<>();
		Set<Integer> numbers = IntStream.range(1, 10).boxed().collect(Collectors.toSet());
		int i = node.getX();
		int j = node.getY();
		for (int col = 0; col < 9; col++) {
			if (result[i][col].getValue() != 0)
				rows.add(result[i][col].getValue());
		}
		for (int row = 0; row < 9; row++) {
			if (result[row][j].getValue() != 0)
				cols.add(result[row][j].getValue());
		}
		rows.addAll(cols);
		rows.addAll(gridScan(i, j));
		numbers.removeAll(rows);
		return numbers;
	}

	public static Set<Integer> gridScan(int i, int j) {
		Set<Integer> grid = new HashSet<>();
		switch (i % 3) {
		case 0:
			switch (j % 3) {
			case 0:
				if (result[i + 1][j + 1].getValue() != 0)
					grid.add(result[i + 1][j + 1].getValue());
				if (result[i + 1][j + 2].getValue() != 0)
					grid.add(result[i + 1][j + 2].getValue());
				if (result[i + 2][j + 1].getValue() != 0)
					grid.add(result[i + 2][j + 1].getValue());
				if (result[i + 2][j + 2].getValue() != 0)
					grid.add(result[i + 2][j + 2].getValue());
				break;
			case 1:
				if (result[i + 1][j + 1].getValue() != 0)
					grid.add(result[i + 1][j + 1].getValue());
				if (result[i + 1][j - 1].getValue() != 0)
					grid.add(result[i + 1][j - 1].getValue());
				if (result[i + 2][j + 1].getValue() != 0)
					grid.add(result[i + 2][j + 1].getValue());
				if (result[i + 2][j - 1].getValue() != 0)
					grid.add(result[i + 2][j - 1].getValue());
				break;
			case 2:
				if (result[i + 1][j - 1].getValue() != 0)
					grid.add(result[i + 1][j - 1].getValue());
				if (result[i + 1][j - 2].getValue() != 0)
					grid.add(result[i + 1][j - 2].getValue());
				if (result[i + 2][j - 1].getValue() != 0)
					grid.add(result[i + 2][j - 1].getValue());
				if (result[i + 2][j - 2].getValue() != 0)
					grid.add(result[i + 2][j - 2].getValue());
			}
			break;
		case 1:
			switch (j % 3) {
			case 0:
				if (result[i + 1][j + 1].getValue() != 0)
					grid.add(result[i + 1][j + 1].getValue());
				if (result[i + 1][j + 2].getValue() != 0)
					grid.add(result[i + 1][j + 2].getValue());
				if (result[i - 1][j + 1].getValue() != 0)
					grid.add(result[i - 1][j + 1].getValue());
				if (result[i - 1][j + 2].getValue() != 0)
					grid.add(result[i - 1][j + 2].getValue());
				break;
			case 1:
				if (result[i + 1][j + 1].getValue() != 0)
					grid.add(result[i + 1][j + 1].getValue());
				if (result[i + 1][j - 1].getValue() != 0)
					grid.add(result[i + 1][j - 1].getValue());
				if (result[i - 1][j + 1].getValue() != 0)
					grid.add(result[i - 1][j + 1].getValue());
				if (result[i - 1][j - 1].getValue() != 0)
					grid.add(result[i - 1][j - 1].getValue());
				break;
			case 2:
				if (result[i + 1][j - 1].getValue() != 0)
					grid.add(result[i + 1][j - 1].getValue());
				if (result[i + 1][j - 2].getValue() != 0)
					grid.add(result[i + 1][j - 2].getValue());
				if (result[i - 1][j - 1].getValue() != 0)
					grid.add(result[i - 1][j - 1].getValue());
				if (result[i - 1][j - 2].getValue() != 0)
					grid.add(result[i - 1][j - 2].getValue());
			}
			break;
		case 2:
			switch (j % 3) {
			case 0:
				if (result[i - 1][j + 1].getValue() != 0)
					grid.add(result[i - 1][j + 1].getValue());
				if (result[i - 1][j + 2].getValue() != 0)
					grid.add(result[i - 1][j + 2].getValue());
				if (result[i - 2][j + 1].getValue() != 0)
					grid.add(result[i - 2][j + 1].getValue());
				if (result[i - 2][j + 2].getValue() != 0)
					grid.add(result[i - 2][j + 2].getValue());
				break;
			case 1:
				if (result[i - 1][j + 1].getValue() != 0)
					grid.add(result[i - 1][j + 1].getValue());
				if (result[i - 1][j - 1].getValue() != 0)
					grid.add(result[i - 1][j - 1].getValue());
				if (result[i - 2][j + 1].getValue() != 0)
					grid.add(result[i - 2][j + 1].getValue());
				if (result[i - 2][j - 1].getValue() != 0)
					grid.add(result[i - 2][j - 1].getValue());
				break;
			case 2:
				if (result[i - 1][j - 1].getValue() != 0)
					grid.add(result[i - 1][j - 1].getValue());
				if (result[i - 1][j - 2].getValue() != 0)
					grid.add(result[i - 1][j - 2].getValue());
				if (result[i - 2][j - 1].getValue() != 0)
					grid.add(result[i - 2][j - 1].getValue());
				if (result[i - 2][j - 2].getValue() != 0)
					grid.add(result[i - 2][j - 2].getValue());
			}
		}
		return grid;
	}

	public static void backTrack(List<Node> unfilledList, int len, int current) {
		if (!tryCheck() && current < len) {
			Set<Integer> numbers = lineScan(unfilledList.get(current));
			if (numbers.size() == 0) {
				return;
			} else if (numbers.size() == 1) {
				unfilledList.get(current).setValue(numbers.toArray(new Integer[0])[0]);
				backTrack(unfilledList, len, current + 1);
				if (!tryCheck())
					unfilledList.get(current).setValue(0);
			} else {
				Integer[] values = numbers.toArray(new Integer[0]);
				for (Integer value : values) {
					if (!tryCheck()) {
						unfilledList.get(current).setValue(value);
						backTrack(unfilledList, len, current + 1);
					}
				}
				if (!tryCheck())
					unfilledList.get(current).setValue(0);
			}
		}
	}

	public static void backTrack(List<Node> unfilledList) {
		int len = unfilledList.size();
		backTrack(unfilledList, len, 0);
	}

	public static void resolve() {
		while (exclusive()) {
			exclusive();
		}
		if (!check()) {
			List<Node> unfilledList = remainNodes();
			backTrack(unfilledList);
		}
	}

	public static void main(String[] args) {
		init();
		resolve();
		printResult();
	}

}
