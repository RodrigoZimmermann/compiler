import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public class Semantico implements Constants {

	private String operador;
	private StringBuilder codigo = new StringBuilder();
	private Stack<String> pilha_tipos = new Stack<>();
	private ArrayList<String> lista_id = new ArrayList<>();
	private Stack<String> pilha_rotulos = new Stack<>();
	private HashMap<String, String> tabela_simbolos = new HashMap<>();
	int contadorRotulo = 1;
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
			String tipo14 = pilha_tipos.pop();
			if (tipo14.equalsIgnoreCase(int64)) {
				codigo.append("conv.i8").append("\n");
			}
			codigo.append("call void [mscorlib]System.Console::Write(" + tipo14 + ")").append("\n");
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
			codigo.append(".locals(int64 _temp_int, float64 _temp_float, string _temp_str, bool _temp_bool)");
			break;
		case 22:
			lista_id.add(token.getLexeme());
			break;
		case 23:
			acao23(token);
			break;
		case 24:
			acao24();
			break;
		case 25:
			acao25();
			break;
		case 26:
			acao26();
			break;
		case 27:
			acao27();
			break;
		case 28:
			acao28();
			break;
		case 29:
			acao29();
			break;
		case 30:
			acao30();
			break;
		case 31:
			acao31();
			break;
		case 32:
			acao32();
			break;
		case 33:
			acao33();
			break;
		default:
			System.out.println("Ação #" + action + ", Token: " + token);
			break;
		}
	}

	private void acao31() {
		String tipo = pilha_tipos.pop();
		String rotulo = "r" + contadorRotulo;
		codigo.append(rotulo + ":" + "\n");
		pilha_rotulos.push(rotulo);
		contadorRotulo++;
	}

	private void acao32() {
		String rotulo = "r" + contadorRotulo;
		codigo.append("brtrue " + rotulo + "\n");
		pilha_rotulos.push(rotulo);
		contadorRotulo++;
	}

	private void acao33() {
		String rotuloDesempilhado1 = pilha_rotulos.pop();
		String rotuloDesempilhado2 = pilha_rotulos.pop();
		codigo.append("br " + rotuloDesempilhado1 + "\n");
		codigo.append(rotuloDesempilhado2 + ":" + "\n");
	}

	private void acao30() {
		String rotulo = "r" + contadorRotulo;
		codigo.append("br " + rotulo + "\n");

		String rotuloDesempilhado = pilha_rotulos.pop();
		codigo.append(rotuloDesempilhado + ":" + "\n");

		pilha_rotulos.push(rotulo);
		contadorRotulo++;

	}

	private void acao29() {
		String rotuloDesempilhado = pilha_rotulos.pop();
		codigo.append(rotuloDesempilhado + ":" + "\n");
	}

	private void acao28() {
		String tipo = pilha_tipos.pop();
		String rotulo = "r" + contadorRotulo;
		codigo.append("brfalse " + rotulo + "\n");
		pilha_rotulos.push(rotulo);
		contadorRotulo++;
	}

	private void acao27() throws SemanticError {
		for (String identificador : lista_id) {
			String tipo = validaString(tabela_simbolos.get(identificador));

			if (tipo.isBlank()) {
				throw new SemanticError(" Identificador " + identificador + " não declarado");
			}

			codigo.append("call string [mscorlib]System.Console::ReadLine()" + "\n");

			if (tipo == int64) {
				codigo.append("call int64 [mscorlib]System.Int64::Parse(string)" + "\n");
			} else if (tipo == float64) {
				codigo.append("call float64 [mscorlib]System.Double::Parse(string)" + "\n");
			} else if (tipo == bool) {
				codigo.append("call bool [mscorlib]System.Boolean::Parse(string)" + "\n");
			}
			codigo.append("stloc " + identificador + "\n");
		}
		lista_id.clear();
	}

	private void acao26() throws SemanticError {
		String rotulo = "r" + contadorRotulo;
		codigo.append("brfalse " + rotulo + "\n");
		contadorRotulo++;

		String tipoVariavel = lista_id.get(lista_id.size());
		codigo.append("ldloc " + tipoVariavel + "\n");
		lista_id.remove(lista_id.size());

		for (int i = 0; i < lista_id.size() - 1; i++) {
			codigo.append("dup").append("\n");
		}

		for (String identificador : lista_id) {
			String tipo = validaString(tabela_simbolos.get(identificador));
			if (tipo.isBlank()) {
				codigo.append(".locals(" + getTipoVariavel(tipoVariavel) + " " + identificador + ")");
				tabela_simbolos.put(identificador, getTipoVariavel(tipoVariavel));
			} else if (getTipoVariavel(tipoVariavel) == tipo) {
				codigo.append("stloc " + identificador + "\n");
			} else if (tipo == float64 && getTipoVariavel(tipoVariavel) == int64) {
				codigo.append("conv.i8").append("\n");
				codigo.append("stloc " + identificador + "\n");
			} else {
				throw new SemanticError(" tipos incompatíveis em comando de atribuição");
			}
		}

		lista_id.clear();
		codigo.append(rotulo + ":" + "\n");
	}

	private void acao25() {
		String tipo = pilha_tipos.pop();

		if (tipo == int64) {
			codigo.append("conv.i8" + "\n");
		}

		String variavel = "";
		switch (tipo) {
		case int64:
			variavel = "_temp_int";
			break;
		case float64:
			variavel = "_temp_float";
			break;
		case string:
			variavel = "_temp_str";
			break;
		case bool:
			variavel = "_temp_bool";
			break;
		}
		codigo.append("stloc " + variavel + "\n");
		lista_id.add(variavel);
	}

	private void acao24() throws SemanticError {
		String tipoPilha = pilha_tipos.pop();
		for (int i = 0; i < lista_id.size() - 1; i++) {
			codigo.append("dup").append("\n");
		}
		for (String identificador : lista_id) {
			String tipo = validaString(tabela_simbolos.get(identificador));
			if (tipo.isBlank()) {
				codigo.append(".locals(" + tipoPilha + " " + identificador + ")");
				tabela_simbolos.put(identificador, tipoPilha);
				if (tipoPilha == int64) {
					codigo.append("conv.i8").append("\n");
				}
				codigo.append("stloc " + identificador + "\n");
			} else if (tipoPilha == tipo) {
				codigo.append("stloc " + identificador + "\n");
			} else if (tipo == float64 && tipoPilha == int64) {
				codigo.append("conv.i8").append("\n");
				codigo.append("stloc " + identificador + "\n");
			} else {
				throw new SemanticError(" tipos incompatíveis em comando de atribuição");
			}
		}
		lista_id.clear();
	}

	private void acao23(Token token) throws SemanticError {
		String identificador = token.getLexeme();
		if (validaString(tabela_simbolos.get(identificador)).isBlank()) {
			throw new SemanticError(" Identificador " + identificador + " não declarado");
		}
		codigo.append("ldloc " + token.getLexeme() + "\n");
		String tipo = tabela_simbolos.get(identificador);
		if (tipo == "int64") {
			codigo.append("conv.r8" + "\n");
		}
		pilha_tipos.push(tipo);
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

	private String validaString(Object value) {
		if (value == null) {
			return "";
		} else {
			return (String) value;
		}
	}

	private String getTipoVariavel(String value) {
		switch (value) {
		case "_temp_int":
			return int64;
		case "_temp_float":
			return float64;
		case "_temp_str":
			return string;
		case "_temp_bool":
			return bool;
		}
		return value;
	}

	public StringBuilder getCodigo() {
		return codigo;
	}
}
