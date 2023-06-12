import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Semantico implements Constants {

	private String operador;
	private StringBuilder codigo = new StringBuilder();
	private Stack<String> pilha_tipos = new Stack<>();
	private ArrayList<String> lista_id = new ArrayList<>();
	private Stack<String> pilha_rotulos = new Stack<>();
	private HashMap<String, String> tabela_simbolos = new HashMap<>();
	private static final String float64 = "float64";
	private static final String int64 = "int64";
	private static final String bool = "bool";
	private static final String string = "string";

	public void executeAction(int action, Token token) throws SemanticError {
		switch (action) {
		case 1:
			addSubMul();
			codigo.append("add").append("\n");
			break;
		case 2:
			addSubMul();
			codigo.append("sub").append("\n");
			break;
		case 3:
			addSubMul();
			codigo.append("mul").append("\n");
			break;
		case 4:
			addSubMul();
			codigo.append("div").append("\n");
			break;
		case 5:
			pilha_tipos.push(int64);
			codigo.append("ldc.i8 ").append(token.getLexeme().replaceAll("\\.", "")).append("\n");
			codigo.append("conv.r8").append("\n");
			break;
		case 6:
			pilha_tipos.push(float64);
			codigo.append("ldc.r8 ").append(token.getLexeme().replaceAll("\\.", "").replaceAll(",", ".")).append("\n");
			break;
		case 8:
			codigo.append("ldc.i8 -1").append("\n");
			codigo.append("conv.r8").append("\n");
			codigo.append("mul").append("\n");
			break;
		case 9:
			operador = token.getLexeme();
			break;
		case 10:
			acao10();
			break;
		case 11:
			pilha_tipos.push(bool);
			codigo.append("ldc.i4.1").append("\n");
			break;
		case 12:
			pilha_tipos.push(bool);
			codigo.append("ldc.i4.0").append("\n");
			break;
		case 13:
			codigo.append("ldc.i4.1").append("\n");
			codigo.append("xor").append("\n");
			break;
		case 14:
			String tipo = pilha_tipos.pop();
			if (tipo.equalsIgnoreCase(int64)) {
				codigo.append("conv.i8").append("\n");
			}
			codigo.append("call void [mscorlib]System.Console::Write(" + tipo + ")").append("\n");
			break;
		case 15:
			codigo.append("	.assembly extern mscorlib {}\n" + "	.assembly _codigo_objeto{}\n"
					+ "	.module   _codigo_objeto.exe\n" + " .class public _UNICA{\n"
					+ "	.method static public void _principal() {\n" + "	.entrypoint").append("\n");
			break;
		case 16:
			codigo.append("		ret\n" + "		}\n" + "	}\n").append("\n");
			break;
		case 17:
			codigo.append("ldstr \"\\n\"").append("\n");
			codigo.append("call void [mscorlib]System.Console::Write(" + string + ")").append("\n");
			break;
		case 18:
			bool();
			codigo.append("and").append("\n");
			break;
		case 19:
			bool();
			codigo.append("or").append("\n");
			break;
		case 20:
			pilha_tipos.push("string");
			codigo.append("ldstr " + token.getLexeme()).append("\n");
			break;
		case 21:
			break;
		case 22:
			break;
		case 23:
			break;
		case 24:
			break;
		case 25:
			break;
		case 26:
			break;
		case 27:
			break;
		case 28:
			break;
		case 29:
			break;
		case 30:
			break;
		case 31:
			break;
		case 32:
			break;
		case 33:
			break;
		default:
			System.out.println("Ação #" + action + ", Token: " + token);
			break;
		}
	}

	private void acao10() {
		String tipo1 = pilha_tipos.pop();
		String tipo2 = pilha_tipos.pop();
		pilha_tipos.push("bool");
		if (operador.equalsIgnoreCase(">")) {
			codigo.append("cgt").append("\n");
		} else if (operador.equalsIgnoreCase(">=")) {
			codigo.append("clt").append("\n");
			codigo.append("ldc.i4 0").append("\n");
			codigo.append("ceq").append("\n");
		} else if (operador.equalsIgnoreCase("<")) {
			codigo.append("clt").append("\n");
		} else if (operador.equalsIgnoreCase("<=")) {
			codigo.append("cgt").append("\n");
			codigo.append("ldc.i4 0").append("\n");
			codigo.append("ceq").append("\n");
		} else if (operador.equalsIgnoreCase("==")) {
			if (tipo1 == "string") {
				codigo.append("call int32 [mscorlib]System.String::Compare(string, string)").append("\n");
			} else {
				codigo.append("ceq").append("\n");
			}
		} else if (operador.equalsIgnoreCase("!=")) {
			codigo.append("ceq").append("\n");
			codigo.append("ldc.i4 0").append("\n");
			codigo.append("ceq").append("\n");
		}
	}

	private void bool() {
		pilha_tipos.pop();
		pilha_tipos.pop();
		pilha_tipos.push("bool");
	}

	private void addSubMul() {
		String tipo1 = pilha_tipos.pop();
		String tipo2 = pilha_tipos.pop();
		if (tipo1.contentEquals(float64) || tipo2.contentEquals(float64)) {
			pilha_tipos.push(float64);
		} else {
			pilha_tipos.push(int64);
		}
	}

	public StringBuilder getCodigo() {
		return codigo;

	}
}
