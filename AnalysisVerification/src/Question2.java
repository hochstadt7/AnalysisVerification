import ast.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Question2 extends Question {

	@Override
	Map<String, String> applyAbstractFunction(Map<String, String> variables, Command command) {
		return null;
	}

	@Override
	Map<String, Map<String, String>> computeRelations(Map<String, String> state) {
		return null;
	}

	@Override
	Map<String, String> join(Map<String, String> state1, Map<String, String> state2) {
		return null;
	}

	@Override
	boolean assertion(String assertCommand, Vertex last) {
		return false;
	}
}